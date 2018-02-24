package com.mao.bloompoi.reader;

/**
 * Created by mao on 2018/2/16.
 */
public class ValidResult {

    private Integer rowNum;

    private Integer colNum;

    private String msg;

    public ValidResult(Integer rowNum, Integer colNum, String msg) {
        this.rowNum = rowNum;
        this.colNum = colNum;
        this.msg = msg;
    }

    public static ValidResult ok() {
        return new ValidResult(null, null, null);
    }

    public static ValidResult fail(String msg) {
        return new ValidResult(null, null, msg);
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public void setRowNum(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Integer getColNum() {
        return colNum;
    }

    public void setColNum(Integer colNum) {
        this.colNum = colNum;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
