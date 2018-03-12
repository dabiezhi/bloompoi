package com.mao.bloompoi.java8;

import java.lang.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author tsy
 * @Description
 * @date 19:33 2018/3/1
 */
public class Array {

    /**
     * 将数组分割成特定大小的小数组。
     */
    public static int[][] chunk(int[] numbers, int size) {
        return IntStream.iterate(0, i -> i + size)
                .limit((long) java.lang.Math.ceil((double) numbers.length / size))
                .mapToObj(cur -> Arrays.copyOfRange(numbers, cur,
                        cur + size > numbers.length ? numbers.length : cur + size))
                .toArray(int[][]::new);
    }

    /**
     * concat
     */
    public static <T> T[] concat(T[] first, T[] second) {
        return Stream.concat(Stream.of(first), Stream.of(second))
                .toArray(i -> (T[]) Arrays.copyOf(new Object[0], i, first.getClass()));
    }

    /**
     * 计算数组中某个值出现的次数。
     */
    public static long countOccurrences(int[] numbers, int value) {
        return Arrays.stream(numbers).filter(number -> number == value).count();
    }

    /**
     * deepFlatten 数组扁平化。 使用递归实现，Arrays.stream().flatMapToInt()
     */
    public static int[] deepFlatten(Object[] input) {
        return Arrays.stream(input).flatMapToInt(o -> {
            if (o instanceof Object[]) {
                return Arrays.stream(deepFlatten((Object[]) o));
            }
            return IntStream.of((Integer) o);
        }).toArray();
    }

    /**
     * 返回两个数组之间的差异。 从 b 中创建一个集合，然后在 a 上使用 Arrays.stream().filter() 只保留 b 中不包含的值。
     */
    public static int[] difference(int[] first, int[] second) {
        Set<Integer> set = Arrays.stream(second).boxed().collect(Collectors.toSet());
        return Arrays.stream(first).filter(v -> !set.contains(v)).toArray();
    }

    /**
     * 使用 Arrays.stream().distinct() 去除所有重复的值。
     */
    public static int[] distinctValuesOfArray(int[] elements) {
        return Arrays.stream(elements).distinct().toArray();
    }

    /**
     * 移除数组中的元素，直到传递的函数返回true为止。返回数组中的其余元素。
     * 使用数组循环遍历数组，将数组的第一个元素删除，直到函数返回的值为真为止。返回其余的元素。
     */
    public static int[] dropElements(int[] elements, IntPredicate condition) {
        while (elements.length > 0 && !condition.test(elements[0])) {
            elements = Arrays.copyOfRange(elements, 1, elements.length);
        }
        return elements;
    }

    /**
     * 查找数组中元素的索引，在不存在元素的情况下返回-1。 使用 IntStream.range().filter() 查找数组中元素的索引。
     */
    public static int indexOf(int[] elements, int el) {
        return IntStream.range(0, elements.length).filter(idx -> elements[idx] == el).findFirst()
                .orElse(-1);
    }

    /**
     * 查找数组中元素的最后索引，在不存在元素的情况下返回-1。 使用 IntStream.iterate().limit().filter()
     * 查找数组中元素的索引。
     */
    public static int lastIndexOf(int[] elements, int el) {
        return IntStream.iterate(elements.length - 1, i -> i - 1).limit(elements.length)
                .filter(idx -> elements[idx] == el).findFirst().orElse(-1);
    }

    /**
     * 使数组扁平。 使用 Arrays.stream().flatMapToInt().toArray() 创建一个新数组。
     */
    public static int[] flatten(Object[] elements) {
        return Arrays.stream(elements).flatMapToInt(
                el -> el instanceof int[] ? Arrays.stream((int[]) el) : IntStream.of((int) el))
                .toArray();
    }

    /**
     * 根据给定函数对数组元素进行分组。 使用 Arrays.stream().collect(Collectors.groupingBy()) 分组。
     */
    public static <T, R> Map<R, List<T>> groupBy(T[] elements, Function<T, R> func) {
        return Arrays.stream(elements).collect(Collectors.groupingBy(func));
    }

}
