package com.mao.bloompoi.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel返回结果
 *
 * @author bloom
 * @date 2018/2/25
 */
public class ExcelResult<T> {

    /**
     * Excel表中数据校验结果(行-列-校验信息)
     */
    private List<ValidResult> validResults = new ArrayList<>();

    /**
     * Excel数据转成对象集合
     */
    private List<T> rows = new ArrayList<>();

    /**
     * Excel表列名-列号映射关系
     */
    private Map<String,Integer> colMap= new HashMap<>();

    /**
     * Excel表特殊列名-行号映射关系
     */
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
