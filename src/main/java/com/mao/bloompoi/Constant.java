package com.mao.bloompoi;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

/**
 * Created by mao on 2018/2/14.
 */
public interface Constant {

    int DEFAULT_ORDER = -1;

    /**
     * 特殊符号
     **/
    String EMPTY_STRING = "";
    String LEFT_BRACKET = "(";
    String RIGHT_BRACKET = ")";
    String ASTERISK = "*";
    String POUND = "#";
    String UNDERLINE = "_";
    String LEFT_BRACE = "{";
    String RIGHT_BRACE = "}";

    /**
     * Excelfu'hao符号
     **/
    String SHEET_ZERO = "sheet0";
    String EXCEL_MODEL_ANNOTATION = "&model";
    String EXCEL_VERTICAL_LIST_ANNOTATION = "&v_list";
    String EXCEL_HORIZONTAL_LIST_ANNOTATION = "&h_list";
    String EXCEL_MAP_ANNOTATION = "&map";

    default CellStyle defaultTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();

        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);

        style.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 18);
        font.setBold(true);
        font.setFontName("Arial");
        style.setFont(font);
        return style;
    }

    /**
     * The default Excel header style.
     *
     * @param workbook Excel workbook
     * @return header row cell style
     */
    default CellStyle defaultHeaderStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();

        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);

        headerStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        font.setFontName("Arial");
        headerStyle.setFont(font);
        return headerStyle;
    }

    /**
     * The default Excel column style.
     *
     * @param workbook Excel workbook
     * @return row column cell style
     */
    default CellStyle defaultColumnStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setWrapText(true);

        Font font = workbook.createFont();
        font.setFontName("Arial");
        cellStyle.setFont(font);
        return cellStyle;
    }
}
