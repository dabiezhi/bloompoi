package com.mao.bloompoi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mao on 2018/2/16.
 */
public class User {

    private String name;
    private Integer age;

    private String school;

    private List<CardSecret> list = new ArrayList<>();

    private List<String> titleList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public List<CardSecret> getList() {
        return list;
    }

    public void setList(List<CardSecret> list) {
        this.list = list;
    }

    public List<String> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<String> titleList) {
        this.titleList = titleList;
    }
}
