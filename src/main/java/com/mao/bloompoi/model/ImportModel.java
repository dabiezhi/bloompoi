package com.mao.bloompoi.model;

import com.mao.bloompoi.annotation.ExcelField;

/**
 * Excel导入model(用于存入一些特殊属性)
 *
 * @author bloom
 * @date 2018/2/25
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
