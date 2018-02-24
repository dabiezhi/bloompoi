package com.mao.bloompoi.writer;

import com.mao.bloompoi.enums.ExcelType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by mao on 2018/2/14.
 */
public class Exporter<T> {

    private String headerTitle;
    private String templatePath;
    private ExcelType excelType;
    private Collection<T> data;
    private Map<Integer, Object> dataMap;
    private int startRow = 1;

    private Function<Workbook, CellStyle> titleStyle;
    private Function<Workbook, CellStyle> headerStyle;
    private Function<Workbook, CellStyle> columnStyle;

    public static <T> Exporter<T> create(Collection<T> data) {
        Exporter<T> exporter = new Exporter<>();
        exporter.setData(data);
        return exporter;
    }

    public static <T> Exporter<T> create(Map<Integer, Object> dataMap) {
        Exporter<T> exporter = new Exporter<>();
        exporter.setDataMap(dataMap);
        return exporter;
    }

    public Exporter<T> title(String title) {
        this.headerTitle = title;
        return this;
    }

    public Exporter<T> byTemplate(String templatePath) {
        this.templatePath = templatePath;
        return this;
    }

    public Exporter<T> startRow(int startRow) {
        this.startRow = startRow;
        return this;
    }

    public int startRow() {
        return this.startRow;
    }

    public Function<Workbook, CellStyle> getTitleStyle() {
        return titleStyle;
    }

    public Function<Workbook, CellStyle> getHeaderStyle() {
        return headerStyle;
    }

    public Function<Workbook, CellStyle> getColumnStyle() {
        return columnStyle;
    }

    public Collection<T> getData() {
        return data;
    }

    public void setData(Collection<T> data) {
        this.data = data;
    }

    public ExcelType getExcelType() {
        return excelType;
    }

    public void setExcelType(ExcelType excelType) {
        this.excelType = excelType;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public Map<Integer, Object> getDataMap() {
        return dataMap;
    }

    public void setDataMap(Map<Integer, Object> dataMap) {
        this.dataMap = dataMap;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public void setTitleStyle(Function<Workbook, CellStyle> titleStyle) {
        this.titleStyle = titleStyle;
    }

    public void setHeaderStyle(Function<Workbook, CellStyle> headerStyle) {
        this.headerStyle = headerStyle;
    }

    public void setColumnStyle(Function<Workbook, CellStyle> columnStyle) {
        this.columnStyle = columnStyle;
    }
}
