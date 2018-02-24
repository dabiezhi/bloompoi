package com.mao.bloompoi.utils;

import java.lang.reflect.Method;

/**
 * 常用的正则表达式
 *
 * @author tsy
 */
public class ReflectUtils {

    private ReflectUtils() {
    }

    /**
     * @param fieldName 属性名
     * @param o         对象 根据属性名获取属性值
     * @return 对象
     */
    public static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Class[] classes = new Class[]{};
            Method method = o.getClass().getMethod(getter, classes);
            Object[] obj = new Object[]{};
            return method.invoke(o, obj);
        } catch (Exception e) {
            return null;
        }
    }
}
