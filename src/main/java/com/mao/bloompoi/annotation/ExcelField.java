package com.mao.bloompoi.annotation;

import com.mao.bloompoi.Constant;
import com.mao.bloompoi.conterver.Converter;
import com.mao.bloompoi.conterver.EmptyConverter;
import com.mao.bloompoi.enums.RegexType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.mao.bloompoi.Constant.EMPTY_STRING;
import static com.mao.bloompoi.Constant.SHEET_ZERO;

/**
 * 模型excel字段绑定
 * @author bloom
 * @date 2018/225
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {

    /**
     * Excel列号顺序(必填,从0开始)
     * @return 列号
     */
    int order() default Constant.DEFAULT_ORDER;

    /**
     * Excel列名称(必填,但可以跟excel列名名称不对应)
     * @return 列名称
     */
    String columnName() default EMPTY_STRING;

    /**
     * Excel字段是否为空(默认可以为空)
     * @return true/false
     */
    boolean nullable() default true;

    /**
     * Excel字段最大长度
     * @return 长度
     */
    int maxLength() default 0;

    /**
     * Excel字段最小长度
     * @return 长度
     */
    int minLength() default 0;

    /**
     * 提供正则表达式进行字段校验
     * @return 正则表达式
     */
    String regexExpression() default "";

    /**
     * Excel字段日期格式化
     * @return 字段格式化后的字符串
     */
    String datePattern() default EMPTY_STRING;

    /**
     * 所提供的的一些Excel字段校验注解
     * @return 校验类型
     */
    RegexType regexType() default RegexType.NONE;

    /**
     * Excel字段类型转换器
     * @return 类型转换器
     */
    Class<? extends Converter> convertType() default EmptyConverter.class;

    /**
     * 是否为特殊字段(默认false)
     * @return 特殊注解
     */
    Special special() default @Special();
}
