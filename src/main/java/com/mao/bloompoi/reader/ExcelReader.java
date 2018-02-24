package com.mao.bloompoi.reader;

import com.mao.bloompoi.Constant;
import com.mao.bloompoi.utils.ExcelUtils;
import com.mao.bloompoi.utils.Pair;
import com.mao.bloompoi.utils.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by mao on 2018/2/16.
 */
public class ExcelReader<T> {

    private Workbook workbook;
    private Class<T> type;
    private int startRowIndex = 1;
    private Function<T, ValidResult> validFunction;
    private ExcelResult<T> excelResult = new ExcelResult<>();

    public ExcelReader(Workbook workbook, Class<T> type) {
        this.workbook = workbook;
        this.type = type;
    }

    public ExcelResult<T> asResult() {
        List<T> rows = this.asStream().map(Pair::getV).collect(Collectors.toList());
        this.excelResult.setRows(rows);
        return this.excelResult;
    }

    public List<T> asList() {
        return this.asStream().map(Pair::getV).collect(Collectors.toList());
    }

    private Stream<Pair<Integer, T>> asStream() {
        Sheet sheet = workbook.getSheetAt(0);

        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        List<Pair<Integer, T>> list = new ArrayList<>(lastRowNum);

        for (int rowNum = firstRowNum + this.startRowIndex; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (null == row) {
                continue;
            }
            T item = this.buildItem(row);
            if (null != item) {
                list.add(new Pair<>(rowNum, item));
            }
        }
        return list.stream();
    }


    private T buildItem(Row row) {
        T item = ExcelUtils.newInstance(type);
        Map<String, Integer> specialMap = ExcelUtils.getSpecialFieldMap(type);
        if (null == item) {
            return null;
        }
        int firstCellNum = row.getFirstCellNum();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            boolean isSpecialRow= specialMap.values().contains(row.getRowNum());
            boolean flag = specialMap.values().contains(row.getRowNum())
                    && null == specialMap.get(row.getRowNum() + Constant.UNDERLINE + cellNum);
            if (flag) {
                continue;
            }
            String value = ExcelUtils.getCellValue(cell);

            String validMsg = ExcelUtils.validFieldByAnnotation(item, cellNum, value,isSpecialRow);
            if (null != this.validFunction) {
                ValidResult validResult = validFunction.apply(item);
                validMsg = validResult.getMsg();
            }
            if (StringUtils.isNotBlank(validMsg)) {
                excelResult.addValidResult(new ValidResult(row.getRowNum(), cellNum, validMsg));
            }
            ExcelUtils.writeToField(item, cellNum, value, isSpecialRow);
        }
        return item;
    }

    public ExcelReader<T> valid(Function<T, ValidResult> validFunction) {
        this.validFunction = validFunction;
        return this;
    }

    public ExcelReader<T> startRow(int startRowIndex) {
        this.startRowIndex = startRowIndex;
        return this;
    }
}
