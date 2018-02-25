package com.mao.bloompoi.writer;

import com.mao.bloompoi.Constant;
import com.mao.bloompoi.annotation.ExcelField;
import com.mao.bloompoi.enums.ExcelType;
import com.mao.bloompoi.exception.ExcelException;
import com.mao.bloompoi.reader.ValidResult;
import com.mao.bloompoi.utils.ExcelUtils;
import com.mao.bloompoi.utils.ReflectUtils;
import com.mao.bloompoi.utils.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.sl.usermodel.ShapeType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by mao on 2018/2/14.
 */
public interface ExcelWriter extends Constant {

    public static Map<String, List> LIST_CACHE = new HashMap<>(8);

    default <T> void export(Exporter<T> exporter, OutputStream outputStream) throws ExcelException {
        Collection<T> data = exporter.getData();
        if (CollectionUtils.isEmpty(data)) {
            throw new ExcelException("Export excel data is empty.");
        }

        try (Workbook workbook = createWork(exporter)) {
            Sheet sheet;
            CellStyle headerStyle = null;
            CellStyle columnStyle = null;
            CellStyle titleStyle = null;

            T data0 = data.iterator().next();
            // Set Excel header
            Iterator<T> iterator = data.iterator();
            Map<String, Integer> writeFieldNames = ExcelUtils
                    .getFieldNameAndColMap(data0.getClass());

            int startRow = exporter.startRow();

            if (StringUtils.isNotBlank(exporter.getTemplatePath())) {
                sheet = workbook.getSheetAt(0);
                columnStyle = this.defaultColumnStyle(workbook);
            } else {
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
                    this.writeTitleRow(titleStyle, sheet, headerTitle,
                            writeFieldNames.keySet().size());
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

    default <T> Workbook createWork(Exporter<T> exporter)
            throws IOException, InvalidFormatException {
        if (StringUtils.isNotBlank(exporter.getTemplatePath())) {
            InputStream in = ExcelWriter.class.getClassLoader()
                    .getResourceAsStream(exporter.getTemplatePath());
            if (null==in){
                in = new FileInputStream(exporter.getTemplatePath());
            }
            return WorkbookFactory.create(in);
        } else {
            return exporter.getExcelType().equals(ExcelType.XLSX) ? new XSSFWorkbook()
                    : new HSSFWorkbook();
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

    default void writeColumnNames(int rowIndex, CellStyle headerStyle, Sheet sheet,
            Map<String, Integer> columnNames) {
        Row rowHead = sheet.createRow(rowIndex);
        columnNames.forEach((k, v) -> {
            Cell cell = rowHead.createCell(v);
            if (null != headerStyle) {
                cell.setCellStyle(headerStyle);
            }
            cell.setCellValue(k);
        });
    }

    default <T> void writeRows(Sheet sheet, CellStyle columnStyle, Iterator<T> iterator,
            Map<String, Integer> writeFieldNames, int startRow) {
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

    default <T> void exportBySpel(Exporter<T> exporter, OutputStream outputStream)
            throws ExcelException {
        InputStream in = ExcelWriter.class.getClassLoader()
                .getResourceAsStream(exporter.getTemplatePath());
        try (Workbook workbook = createWork(exporter)) {
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
                                        String.format(value, j).substring(value.indexOf(POUND)))
                                .getValue(context, String.class));
                    }
                    i += list.size() - 1;
                    specialNum += list.size() - 1;
                    specialRow = Integer.valueOf(value.substring(value.indexOf(LEFT_BRACE) + 1,
                            value.indexOf(RIGHT_BRACE))) + 1;
                }

                if (value.contains(EXCEL_VERTICAL_LIST_ANNOTATION)) {
                    List list = getListByReflect(cell, value, obj);
                    for (int j = 0; j < list.size(); j++) {
                        cell = getVerticalValueByList(sheet, row, rowMap, i, j);
                        cell.setCellValue(parser
                                .parseExpression(
                                        String.format(value, j).substring(value.indexOf(POUND)))
                                .getValue(context, String.class));
                    }
                }
                if (-1 != specialRow && row.getRowNum() >= specialRow && specialIndex <= i) {
                    specialValue = specialValue.equals(Constant.EMPTY_STRING) ? value
                            : specialValue;
                    List list = getListByReflect(cell, specialValue, obj);
                    for (int j = 0; j < list.size(); j++) {
                        cell = getVerticalValueByList(sheet, row, rowMap, i, j);
                        Cell titleCell = sheet.getRow(specialRow - 1).getCell(i);
                        String titleValue = ExcelUtils.getCellValue(titleCell);
                        cell.setCellValue(String.valueOf(parser
                                .parseExpression(String.format(specialValue, 0, titleValue)
                                        .substring(specialValue.indexOf(POUND)))
                                .getValue(context)));
                    }
                }
            }
        }
    }

    default List getListByReflect(Cell cell, String val, Object obj) {
        if (StringUtils.isBlank(val)) {
            return Collections.emptyList();
        }
        String value = val.substring(val.indexOf(LEFT_BRACKET) + 1, val.indexOf(RIGHT_BRACKET));
        List list = LIST_CACHE.get(value);
        list = Optional.ofNullable(list).orElse(new ArrayList());
        if (CollectionUtils.isEmpty(list)) {
            list = (List) ReflectUtils.getFieldValueByName(value, obj);
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

    default void getSheetAtSplit(Cell cell, ExpressionParser parser, EvaluationContext context,
            String value, String modelLogo) {
        if (null != cell && value.contains(modelLogo)) {
            cell.setCellValue(parser.parseExpression(value.substring(value.indexOf(POUND)))
                    .getValue(context, String.class));
        }
    }

    default <T> void exportByResult(Exporter<T> exporter, OutputStream outputStream)
            throws ExcelException {
        try (Workbook workbook = createWork(exporter)) {
            // 定义异常单元格前景色
            Map<String, Object> properties = new HashMap<>();
            properties.put(CellUtil.FILL_FOREGROUND_COLOR, IndexedColors.RED.index);
            properties.put(CellUtil.FILL_PATTERN, FillPatternType.SOLID_FOREGROUND);

            // 标识异常单元格
            Sheet sheet = workbook.getSheetAt(0);
            for (ValidResult item : exporter.getResults()) {
                Integer rowNum = null == item.getRowNum() ? 0 : item.getRowNum();
                Row row = sheet.getRow(rowNum);
                Integer colNum = null == item.getColNum() ? row.getLastCellNum() + 1
                        : item.getColNum();
                Cell cell = row.getCell(colNum);
                if (null == cell) {
                    cell = row.createCell(colNum);
                }
                // 改变样式，避免影响其他单元格样式，使用CellUtil
                CellUtil.setCellStyleProperties(cell, properties);
                if (null == item.getRowNum() || null == item.getColNum()) {
                    // 增加错误说明
                    cell.setCellValue(item.getMsg());
                } else {
                    // 增加批注错误信息提示
                    if (ExcelType.XLS.equals(exporter.getExcelType())) {
                        HSSFComment comment = getHSSFComment(sheet, item);
                        if (null == cell.getCellComment()) {
                            cell.setCellComment(comment);
                        }
                    } else {
                        XSSFComment comment = getXSSFDrawing(sheet, item);
                        if (null == cell.getCellComment()) {
                            cell.setCellComment(comment);
                        }
                    }
                }
            }
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw new ExcelException(e);
        }
    }

    default HSSFComment getHSSFComment(Sheet sheet, ValidResult item) {
        // 增加批注错误信息提示
        HSSFPatriarch patriarch = (HSSFPatriarch) sheet.createDrawingPatriarch();
        // 创建批注位置
        HSSFClientAnchor anchor = patriarch.createAnchor(0, 0, 0, 0, item.getColNum() + 3,
                item.getRowNum(), item.getColNum() + 6, item.getRowNum());
        // 创建批注
        HSSFComment comment = patriarch.createCellComment(anchor);
        // 设置批注内容
        comment.setString(new HSSFRichTextString(item.getMsg()));
        comment.setVisible(false);
        return comment;
    }

    default XSSFComment getXSSFDrawing(Sheet sheet, ValidResult item) {
        // 增加批注错误信息提示
        XSSFDrawing patriarch = (XSSFDrawing) sheet.createDrawingPatriarch();
        // 创建批注位置
        XSSFClientAnchor anchor = patriarch.createAnchor(0, 0, 0, 0, item.getColNum() + 3,
                item.getRowNum(), item.getColNum() + 6, item.getRowNum());
        // 创建批注
        XSSFComment comment = patriarch.createCellComment(anchor);
        // 设置批注内容
        comment.setString(new XSSFRichTextString(item.getMsg()));
        comment.setVisible(false);
        return comment;
    }

    <T> void export(Exporter<T> exporter) throws ExcelException;

    <T> void exportBySpel(Exporter<T> exporter) throws ExcelException;

    <T> void exportByResult(Exporter<T> exporter) throws ExcelException;
}
