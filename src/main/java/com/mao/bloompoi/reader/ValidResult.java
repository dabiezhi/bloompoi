package com.mao.bloompoi.reader;

/**
 * Excel基础数据校验结果
 *
 * @author bloom
 * @date 2018/2/25
 */
public class ValidResult {

    /**
     * 行号
     */
    private Integer rowNum;

    /**
     * 列号
     */
    private Integer colNum;

    /**
     * 基础数据校验信息
     */
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
