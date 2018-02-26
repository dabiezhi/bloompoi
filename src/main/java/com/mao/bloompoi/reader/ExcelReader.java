package com.mao.bloompoi.reader;

import com.mao.bloompoi.Constant;
import com.mao.bloompoi.utils.ExcelUtils;
import com.mao.bloompoi.utils.Pair;
import com.mao.bloompoi.utils.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 读取Excel文件转换成集合
 * <p>
 * 该类用于读取Excel文档并将文档中的所有行转换为List
 * <p>
 * 要通过在构造函数中传递一个Workbook对象来解析Excel, the Class is passed to reflect the value for
 * a particular type of reflection. 该类通过反射来得到值
 * @author bloom
 * @date 2018/2/25
 */
public class ExcelReader<T> {

    private Workbook workbook;
    private Class<T> type;
    private int startRowIndex = 1; // 开始读取的行数 默认为1
    private Function<T, ValidResult> validFunction; // 暂时该字段无用
    private ExcelResult<T> excelResult = new ExcelResult<>(); // 用于存放Excel表中每行每列数据类型的校验结果
    private Map<String, Integer> colMap = new HashMap<>(); // 用于存放列名-列号的映射关系
    private Map<String, Integer> specRowMap = new HashMap<>(); // 用于存在特殊字段列名-行号的映射关系
    private Map<String, String> specialValueMap = new HashMap<>(); // 用于存在特殊字段列名-值的映射关系
    private Boolean flag = true; // 用于控制特殊字段校验的flag

    public ExcelReader(Workbook workbook, Class<T> type) {
        this.workbook = workbook;
        this.type = type;
        colMap = ExcelUtils.getFieldNameAndColMap(type);
        specRowMap = ExcelUtils.getSpecialFieldNameAndRowMap(type);
    }

    /**
     * 读取Excel表中数据返回相关列名、行号映射关系,数据集合、数据类型校验结果
     *
     * @return ExcelResult Excel结果封装类
     */
    public ExcelResult<T> asResult() {
        List<T> rows = this.asStream().map(Pair::getV).collect(Collectors.toList());
        this.excelResult.setRows(rows);
        this.excelResult.setColMap(colMap);
        this.excelResult.setSpecRowMap(specRowMap);
        return this.excelResult;
    }

    /**
     * 读取Excel表中数据转换成数据集合
     *
     * @return 数据集合
     */
    public List<T> asList() {
        return this.asStream().map(Pair::getV).collect(Collectors.toList());
    }

    /**
     * 读取Excel表中数据转换成Stream流
     *
     * @return Stream流
     */
    private Stream<Pair<Integer, T>> asStream() {
        // 获取Sheet对象
        Sheet sheet = workbook.getSheetAt(0);
        // 获取该Sheet中第一行行号、最后一行行号
        int firstRowNum = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();

        List<Pair<Integer, T>> list = new ArrayList<>(lastRowNum);
        // 循环读取行数据，若存在特殊字段，则put到specialValueMap中以便实体中特殊字段反射赋值
        buildSpecialMap(sheet);
        // 从startRowIndex开始，遍历每一行，反射生成实体
        for (int rowNum = firstRowNum + this.startRowIndex; rowNum <= lastRowNum; rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (null == row) {
                continue;
            }
            // 根据row对象反射生成实体
            T item = this.buildItem(row);
            if (null != item) {
                list.add(new Pair<>(rowNum, item));
            }
        }
        return list.stream();
    }

    /**
     * 根据行对象反射生成实体对象
     *
     * @param row 行对象
     * @return 实体
     */
    private T buildItem(Row row) {
        T item = ExcelUtils.newInstance(type);
        if (null == item) {
            return null;
        }
        int firstCellNum = row.getFirstCellNum();
        int lastCellNum = row.getLastCellNum();
        for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
            Cell cell = row.getCell(cellNum);
            String value = ExcelUtils.getCellValue(cell);
            // 校验数据是否符合自定义标准,返回字符串不为null 则不符合自定义标准
            String validMsg = ExcelUtils.validFieldByAnnotation(item, row.getRowNum(), cellNum,
                    value, Boolean.FALSE);
            // 该处代码暂无用
            if (null != this.validFunction) {
                ValidResult validResult = validFunction.apply(item);
                validMsg = validResult.getMsg();
            }
            // 如果校验信息不为空,则将错误信息(行号-列号-错误信息)存入到ValidResult对象中
            if (StringUtils.isNotBlank(validMsg)) {
                excelResult.addValidResult(new ValidResult(row.getRowNum(), cellNum, validMsg));
            }
            // 根据(行号-列号-值)反射赋值到正确的Field上
            ExcelUtils.writeToField(item, row.getRowNum(), cellNum, value, Boolean.FALSE);
        }
        // 第一次进入校验特殊字段的特殊值,进行特殊字段校验+特使字段的赋值
        if (flag) {
            specialValueMap.forEach((k, v) -> {
                Integer rowNum = Integer.valueOf(k.substring(0, k.indexOf(Constant.UNDERLINE)));
                Integer colNum = Integer.valueOf(k.substring(k.indexOf(Constant.UNDERLINE) + 1));
                String validMsg = ExcelUtils.validFieldByAnnotation(item, rowNum, colNum, v,
                        Boolean.TRUE);
                if (StringUtils.isNotBlank(validMsg)) {
                    excelResult.addValidResult(new ValidResult(rowNum, colNum, validMsg));
                }
                ExcelUtils.writeToField(item, rowNum, colNum, v, Boolean.TRUE);
            });
            flag = false;
        }
        ExcelUtils.writeToField(item, row.getRowNum(), -999, String.valueOf(row.getRowNum()),
                Boolean.FALSE);
        return item;
    }

    /**
     *循环读取行数据，若存在特殊字段，则put到specialValueMap中以便实体中特殊字段反射赋值
     *
     * @param sheet sheet对象
     */
    private void buildSpecialMap(Sheet sheet) {
        for (int i = 0; i < startRowIndex; i++) {
            Map<String, Integer> specialMap = ExcelUtils.getSpecialFieldMap(type);
            Row row = sheet.getRow(i);
            if (specialMap.values().contains(i)) {
                for (int cellNum = row.getFirstCellNum(); cellNum < row
                        .getLastCellNum(); cellNum++) {
                    Cell cell = row.getCell(cellNum);
                    boolean isSpecialRow = specialMap.values().contains(row.getRowNum());
                    boolean flag = isSpecialRow && null == specialMap
                            .get(row.getRowNum() + Constant.UNDERLINE + cellNum);
                    if (flag) {
                        continue;
                    }
                    String value = ExcelUtils.getCellValue(cell);
                    specialValueMap.put(row.getRowNum() + Constant.UNDERLINE + cellNum, value);
                }
            }
        }
    }

    /**
     * 该方法暂无用
     *
     * @param validFunction function对象
     * @return ExcelReader对象
     */
    public ExcelReader<T> valid(Function<T, ValidResult> validFunction) {
        this.validFunction = validFunction;
        return this;
    }

    /**
     * 设置Excel的开始读取行号
     * @param startRowIndex 开始读取行号
     * @return ExcelReader 读取对象
     */
    public ExcelReader<T> startRow(int startRowIndex) {
        this.startRowIndex = startRowIndex;
        return this;
    }
}
