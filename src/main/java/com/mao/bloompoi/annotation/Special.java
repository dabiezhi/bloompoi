package com.mao.bloompoi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 模型excel特殊字段绑定(针对水平顺序读取的字段)
 * 例: name:bloom address:hangzhou
 *
 * @author bloom
 * @date 2018/225
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Special {

    /**
     * 是否为特殊字段,默认false
     * @return true/false
     */
    boolean isSpecial() default false;

    /**
     * 特殊字段列号
     * @return 列号
     */
    int specialColNum() default -1;

    /**
     * 特殊字段行号
     * @return 行号
     */
    int specialRowNum() default -1;
}
