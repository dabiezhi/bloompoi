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
public @interface Special {

    boolean isSpecial() default false;

    int specialColNum() default -1;

    int specialRowNum() default -1;
}
