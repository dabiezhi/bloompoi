package com.mao.bloompoi.enums;

/**
 * Created by mao on 2018/2/14.
 */
public enum ExcelType {

    XLS, XLSX;

    public static ExcelType getExcelType(String fileName) {
        if (fileName.toUpperCase().endsWith(XLS.toString())) {
            return ExcelType.XLS;
        }
        if (fileName.toUpperCase().endsWith(XLSX.toString())) {
            return ExcelType.XLSX;
        }
        return ExcelType.XLS;
    }
}
