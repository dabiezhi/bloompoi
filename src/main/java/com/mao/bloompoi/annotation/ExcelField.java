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
 * Created by mao on 2018/2/14.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExcelField {

    int order() default Constant.DEFAULT_ORDER;

    String columnName() default EMPTY_STRING;

    String datePattern() default EMPTY_STRING;

    Class<? extends Converter> convertType() default EmptyConverter.class;

    boolean nullable() default false;

    int maxLength() default 0;

    int minLength() default 0;

    RegexType regexType() default RegexType.NONE;

    String regexExpression() default "";

    Special special() default @Special();
}
