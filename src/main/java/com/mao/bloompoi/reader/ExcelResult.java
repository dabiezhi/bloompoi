package com.mao.bloompoi.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2018/2/16.
 */
public class ExcelResult<T> {

    private List<ValidResult> validResults = new ArrayList<>();

    private List<T> rows = new ArrayList<>();

    private Map<String,Integer> colMap= new HashMap<>();

    private Map<String,Integer> specRowMap=new HashMap<>();

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

    public Map<String, Integer> getColMap() {
        return colMap;
    }

    public void setColMap(Map<String, Integer> colMap) {
        this.colMap = colMap;
    }

    public Map<String, Integer> getSpecRowMap() {
        return specRowMap;
    }

    public void setSpecRowMap(Map<String, Integer> specRowMap) {
        this.specRowMap = specRowMap;
    }
}
