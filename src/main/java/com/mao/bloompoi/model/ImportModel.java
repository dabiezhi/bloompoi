package com.mao.bloompoi.model;

import com.mao.bloompoi.annotation.ExcelField;

/**
 * @author tsy
 * @Description
 * @date 17:13 2018/2/24
 */
public class ImportModel {

    @ExcelField(order = -999,columnName = "行号")
    private Integer rowNum;

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }
}
