package com.mao.bloompoi.conterver;

/**
 * 数据类型转换器接口
 *
 * @author bloom
 * @date 2018/2/25
 */
public interface Converter<T> {

    /**
     * 将数据从Java对象转换为Excel希望输出的特定字符串
     *
     * @param value 值
     * @return 转换后的值
     */
    String write(T value);

    /**
     * 将Excel读取的字段转换为Java对象类型
     *
     * @param value 值
     * @return 转化后的值
     */
    default T read(String value) {
        return null;
    }
}
