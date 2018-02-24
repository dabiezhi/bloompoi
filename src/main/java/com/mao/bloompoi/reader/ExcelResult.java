package com.mao.bloompoi.reader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mao on 2018/2/16.
 */
public class ExcelResult<T> {

    private List<ValidResult> validResults = new ArrayList<>();

    private List<T> rows = new ArrayList<>();

    void addValidResult(ValidResult result) {
        validResults.add(result);
    }

    public List<T> rows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public List<ValidResult> errors() {
        return validResults;
    }

}
