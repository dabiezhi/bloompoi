package com.mao.bloompoi.enums;

/**
 * Excel扩展类型
 *
 * @author bloom
 * @date 2018/2/25
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
