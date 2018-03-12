package com.mao.bloompoi.java8;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author tsy
 * @Description
 * @date 20:17 2018/3/1
 */
public class class1 {

    /**
     * 此方法返回由给定类及其超类实现的所有接口。 该方法通过连接两个Stream来工作。
     * 第一个Stream是通过创建带有接口的流和接口实现的所有接口来递归构建的。
     * 第二个Stream对超类也是如此。其结果是删除重复项后将两个Stream连接起来。
     */
    public static List<Class<?>> getAllInterfaces(Class<?> cls) {
        return Stream.concat(
                Arrays.stream(cls.getInterfaces()).flatMap(intf ->
                        Stream.concat(Stream.of(intf), getAllInterfaces(intf).stream())),
                cls.getSuperclass() == null ? Stream.empty() : getAllInterfaces(cls.getSuperclass()).stream()
        ).distinct().collect(Collectors.toList());
    }
}
