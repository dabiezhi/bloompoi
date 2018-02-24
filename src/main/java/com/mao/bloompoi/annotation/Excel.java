package com.mao.bloompoi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.mao.bloompoi.Constant.SHEET_ZERO;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Excel {

    String sheetName() default SHEET_ZERO;

}
