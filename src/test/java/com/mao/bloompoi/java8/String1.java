package com.mao.bloompoi.java8;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author tsy
 * @Description
 * @date 20:02 2018/3/1
 */
public class String1 {

    /**
     * 生成一个字符串的所有字符（包含重复）。
     */
    public static List<String> anagrams(String input) {
        if (input.length() <= 2) {
            return input.length() == 2
                    ? Arrays.asList(input, input.substring(1) + input.substring(0, 1))
                    : Collections.singletonList(input);
        }
        return IntStream.range(0, input.length())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(i, input.substring(i, i + 1)))
                .flatMap(entry -> anagrams(
                        input.substring(0, entry.getKey()) + input.substring(entry.getKey() + 1))
                                .stream().map(s -> entry.getValue() + s))
                .collect(Collectors.toList());
    }

    /**
     * 检查字符串是否为小写。
     */
    public static boolean isLowerCase(String input) {
        return Objects.equals(input, input.toLowerCase());
    }

    /**
     * 检查字符串是否为数字。
     */
    public static boolean isNumeric(final String input) {
        return IntStream.range(0, input.length()).allMatch(i -> Character.isDigit(input.charAt(i)));
    }

    /**
     * 反转字符串。
     */
    public static String reverseString(String input) {
        return new StringBuilder(input).reverse().toString();
    }

    /**
     * 正则匹配。
     */
    public static List<String> match(String input, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(input);
        List<String> matchedParts = new ArrayList<>();
        while (matcher.find()) {
            matchedParts.add(matcher.group(0));
        }
        return matchedParts;
    }
}
