package com.mao.bloompoi.conterver;

/**
 * Created by mao on 2018/2/14.
 */
public interface Converter<T> {

    String write(T value);

    default T read(String value) {
        return null;
    }
}
