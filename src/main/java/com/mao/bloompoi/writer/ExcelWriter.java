package com.mao.bloompoi.writer;

import com.mao.bloompoi.Constant;
import com.mao.bloompoi.annotation.ExcelField;
import com.mao.bloompoi.enums.ExcelType;
import com.mao.bloompoi.exception.ExcelException;
import com.mao.bloompoi.utils.ExcelUtils;
import com.mao.bloompoi.utils.ReflectUtils;
import com.mao.bloompoi.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by mao on 2018/2/14.
 */
public interface ExcelWriter extends Constant {

    public static final Map<String, List> LIST_CACHE = new HashMap<>(8);

    default <T> void export(Exporter<T> exporter, OutputStream outputStream) throws ExcelException {
        Collection<T> data = exporter.getData();
        if (null == data || data.size() == 0) {
            throw new ExcelException("Export excel data is empty.");
        }
        try {
            Sheet sheet;
            Workbook workbook;
            CellStyle headerStyle = null;
            CellStyle columnStyle = null;
            CellStyle titleStyle = null;

            T data0 = data.iterator().next();
            // Set Excel header
            Iterator<T> iterator = data.iterator();
            Map<String, Integer> writeFieldNames = ExcelUtils.getWriteFieldNames(data0.getClass());

            int startRow = exporter.startRow();

            if (StringUtils.isNotBlank(exporter.getTemplatePath())) {
                InputStream in = ExcelWriter.class.getClassLoader().getResourceAsStream(exporter.getTemplatePath());
                workbook = WorkbookFactory.create(in);
                sheet = workbook.getSheetAt(0);
                columnStyle = this.defaultColumnStyle(workbook);
            } else {
                workbook = exporter.getExcelType().equals(ExcelType.XLSX) ? new XSSFWorkbook() : new HSSFWorkbook();
                sheet = workbook.createSheet(ExcelUtils.getSheetName(data0));

                if (null != exporter.getTitleStyle()) {
                    titleStyle = exporter.getTitleStyle().apply(workbook);
                } else {
                    titleStyle = this.defaultTitleStyle(workbook);
                }

                if (null != exporter.getHeaderStyle()) {
                    headerStyle = exporter.getHeaderStyle().apply(workbook);
                } else {
                    headerStyle = this.defaultHeaderStyle(workbook);
                }

                if (null != exporter.getColumnStyle()) {
                    columnStyle = exporter.getColumnStyle().apply(workbook);
                } else {
                    columnStyle = this.defaultColumnStyle(workbook);
                }

                String headerTitle = exporter.getHeaderTitle();
                int colIndex = 0;
                if (null != headerTitle) {
                    colIndex = 1;
                    this.writeTitleRow(titleStyle, sheet, headerTitle, writeFieldNames.keySet().size());
                }
                this.writeColumnNames(colIndex, headerStyle, sheet, writeFieldNames);
                startRow += colIndex;
            }

            clearShape(sheet, exporter.getExcelType());

            this.writeRows(sheet, columnStyle, iterator, writeFieldNames, startRow);

            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    default void writeTitleRow(CellStyle cellStyle, Sheet sheet, String title, int maxColIndex) {
        Row titleRow = sheet.createRow(0);
        for (int i = 0; i <= maxColIndex; i++) {
            Cell cell = titleRow.createCell(i);
            if (i == 0) {
                cell.setCellValue(title);
            }
            cell.setCellStyle(cellStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, maxColIndex));
    }

    default void writeColumnNames(int rowIndex, CellStyle headerStyle, Sheet sheet, Map<String, Integer> columnNames) {
        Row rowHead = sheet.createRow(rowIndex);
        columnNames.forEach((k, v) -> {
            Cell cell = rowHead.createCell(v);
            if (null != headerStyle) {
                cell.setCellStyle(headerStyle);
            }
            cell.setCellValue(k);
        });
    }

    default <T> void writeRows(Sheet sheet, CellStyle columnStyle, Iterator<T> iterator, Map<String, Integer> writeFieldNames, int startRow) {
        for (int rowNum = startRow; iterator.hasNext(); rowNum++) {
            T item = iterator.next();
            Row row = sheet.createRow(rowNum);
            List<Field> fields = ExcelUtils.getAndSaveFields(item.getClass());
            fields.forEach(field -> {
                ExcelField excelField = field.getAnnotation(ExcelField.class);
                Integer colNum = writeFieldNames.get(excelField.columnName());
                if (null != colNum) {
                    field.setAccessible(true);
                    Cell cell = row.createCell(colNum);
                    String value = ExcelUtils.getColumnValue(item, field);
                    cell.setCellValue(value);
                    cell.setCellStyle(columnStyle);
                    sheet.autoSizeColumn(colNum);
                }
            });
        }
    }

    default void clearShape(Sheet sheet, ExcelType excelType) {
        // 删除模板中的图形，文本框
        if (ExcelType.XLS.equals(excelType)) {
            HSSFPatriarch drawing = (HSSFPatriarch) sheet.getDrawingPatriarch();
            if (null != drawing) {
                drawing.clear();
            }
        } else if (ExcelType.XLSX.equals(excelType)) {
            // xssf找不到清除的方法，使用隐藏代替
            Drawing<?> drawing = sheet.getDrawingPatriarch();
            if (null != drawing) {
                Iterator<?> it = drawing.iterator();
                while (it.hasNext()) {
                    Object o = it.next();
                    if (o instanceof XSSFSimpleShape) {
                        XSSFSimpleShape shape = (XSSFSimpleShape) o;
                        XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();
                        // 清空文本
                        shape.clearText();
                        // 转换成线形状，可以不转
                        shape.setShapeType(ShapeType.LINE.ooxmlId);
                        // 使得图形可移动和改变大小
                        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                        // 移动图形到表单左上角，并隐藏大小
                        anchor.setDx1(0);
                        anchor.setDy1(0);
                        anchor.setDx2(0);
                        anchor.setDy2(0);
                        anchor.setRow1(0);
                        anchor.setCol1(0);
                        anchor.setRow2(0);
                        anchor.setCol2(0);
                    }
                }
            }
        }
    }

    default <T> void exportBySpel(Exporter<T> exporter, OutputStream outputStream) throws ExcelException {
        try {
            Workbook workbook = null;
            InputStream in = new FileInputStream(exporter.getTemplatePath());
            workbook = WorkbookFactory.create(in);
            for (Integer i : exporter.getDataMap().keySet()) {
                getSheetAt(workbook, exporter.getDataMap().get(i), i);
            }
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    default void getSheetAt(Workbook workbook, Object obj, int index) {
        Sheet sheet = workbook.getSheetAt(index);
        Map<Integer, Row> rowMap = new HashMap<>();
        Iterator<Row> rows = sheet.rowIterator();
        ExpressionParser parser = new SpelExpressionParser();
        // SPEL上下文
        EvaluationContext context = new StandardEvaluationContext();
        context.setVariable(obj.getClass().getSimpleName(), obj);

        Integer specialIndex = -1;
        String specialValue = Constant.EMPTY_STRING;
        Integer specialNum = 0;
        Integer specialRow = -1;
        while (rows.hasNext()) {
            Row row = rows.next();
            int colNum = row.getLastCellNum() + specialNum;
            for (int i = 0; i < colNum; i++) {
                Cell cell = row.getCell(i);
                if (null == cell) {
                    cell = row.createCell(i);
                }
                String value = ExcelUtils.getCellValue(cell);
                this.getSheetAtSplit(cell, parser, context, value, EXCEL_MODEL_ANNOTATION);

                if (value.contains(EXCEL_HORIZONTAL_LIST_ANNOTATION)) {
                    specialIndex = i;
                    List list = getListByReflect(cell, value, obj);
                    colNum += list.size() - 1;
                    for (int j = 0; j < list.size(); j++) {
                        Cell c = row.createCell(i + j);
                        c.setCellStyle(this.defaultColumnStyle(workbook));
                        c.setCellValue(parser
                                .parseExpression(
                                        String.format(value, j)
                                                .substring(value.indexOf(POUND)))
                                .getValue(context, String.class));
                    }
                    i += list.size() - 1;
                    specialNum += list.size() - 1;
                    specialRow = Integer.valueOf(value.substring(value.indexOf(LEFT_BRACE) + 1, value.indexOf(RIGHT_BRACE))) + 1;
                }

                if (value.contains(EXCEL_VERTICAL_LIST_ANNOTATION)) {
                    List list = getListByReflect(cell, value, obj);
                    for (int j = 0; j < list.size(); j++) {
                        cell = getVerticalValueByList(sheet, row, rowMap, i, j);
                        cell.setCellValue(parser
                                .parseExpression(
                                        String.format(value, j)
                                                .substring(value.indexOf(POUND)))
                                .getValue(context, String.class));
                    }
                }
                if (-1 != specialRow && row.getRowNum() >= specialRow && specialIndex <= i) {
                    specialValue = specialValue.equals(Constant.EMPTY_STRING) ? value : specialValue;
                    List list = getListByReflect(cell, specialValue, obj);
                    for (int j = 0; j < list.size(); j++) {
                        cell = getVerticalValueByList(sheet, row, rowMap, i, j);
                        Cell titleCell = sheet.getRow(specialRow - 1).getCell(i);
                        String titleValue = ExcelUtils.getCellValue(titleCell);
                        cell.setCellValue(String.valueOf(parser
                                .parseExpression(
                                        String.format(specialValue, 0, titleValue)
                                                .substring(specialValue.indexOf(POUND)))
                                .getValue(context)));
                    }
                }
            }
        }
    }

    default List getListByReflect(Cell cell, String value, Object obj) {
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        value = value.substring(value.indexOf(LEFT_BRACKET) + 1,
                value.indexOf(RIGHT_BRACKET));
        List list = LIST_CACHE.get(value);
        list = Optional.ofNullable(list).orElse(new ArrayList());
        if (CollectionUtils.isEmpty(list)) {
            list = (List) ReflectUtils.getFieldValueByName(value,
                    obj);
            LIST_CACHE.put(value, list);
        }
        clearAnnotationLogo(list, cell);
        return list;
    }

    default Cell getVerticalValueByList(Sheet sheet, Row row, Map<Integer, Row> rowMap,
                                        int colIndex, int rowIndex) {
        Cell cell = row.getCell(colIndex);
        if (rowIndex != 0) {
            Row rowCopy = (null == rowMap.get(row.getRowNum() + rowIndex))
                    ? sheet.createRow(row.getRowNum() + rowIndex)
                    : rowMap.get(row.getRowNum() + rowIndex);
            rowMap.put(row.getRowNum() + rowIndex, rowCopy);
            return rowCopy.createCell(colIndex);
        }
        return cell;
    }

    default void clearAnnotationLogo(List list, Cell cell) {
        if (CollectionUtils.isEmpty(list)) {
            cell.setCellValue("");
        }
    }

    default void getSheetAtSplit(Cell cell, ExpressionParser parser,
                                 EvaluationContext context, String value, String modelLogo) {
        if (null != cell && value.contains(modelLogo)) {
            cell.setCellValue(
                    parser.parseExpression(value.substring(value.indexOf(POUND)))
                            .getValue(context, String.class));
        }
    }

    default Boolean checkSpecicalRow(Row row, String specialValue) {
        return row.getRowNum() == Integer.valueOf(specialValue.substring(specialValue.indexOf(LEFT_BRACE) + 1,
                specialValue.indexOf(RIGHT_BRACE)));
    }

    <T> void export(Exporter<T> exporter) throws ExcelException;

    <T> void exportBySpel(Exporter<T> exporter) throws ExcelException;
}
