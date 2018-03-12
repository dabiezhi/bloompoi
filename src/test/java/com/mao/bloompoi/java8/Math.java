package com.mao.bloompoi.java8;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntBinaryOperator;
import java.util.stream.IntStream;

/**
 * @author tsy
 * @Description
 * @date 20:00 2018/3/1
 */
public class Math {

    /**
     * average 返回两个或两个以上数字的平均值。
     */
    public static double average(int[] arr) {
        return IntStream.of(arr).average()
                .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
    }

    /**
     * 计算一系列数字的最大公约数(gcd)。 使用 Arrays.stream().reduce() 和
     * GCD（使用递归公式）计算一组数字的最大公约数。
     */
    public static OptionalInt gcd(int[] numbers) {
        return Arrays.stream(numbers).reduce((a, b) -> gcd1(a, b));
    }

    private static int gcd1(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd1(b, a % b);
    }

    /**
     * 计算数字数组的最低公共倍数(LCM)。 使用 Arrays.stream().reduce() 和
     * LCM公式(使用递归)来计算数字数组的最低公共倍数。
     */
    public static OptionalInt lcm(int[] numbers) {
        IntBinaryOperator lcm = (x, y) -> (x * y) / gcd2(x, y);
        return Arrays.stream(numbers).reduce((a, b) -> lcm.applyAsInt(a, b));
    }

    private static int gcd2(int a, int b) {
        if (b == 0) {
            return a;
        }
        return gcd2(b, a % b);
    }

    /**
     * 检查数字是否是偶数。 这个方法使用按位运算符，0b1 是1的二进制表示。 因为Java 7可以通过用 0b 或 0B 作为前缀来编写二进制文字。
     * 数字为偶数时，＆ 运算符将返回0。 例如，IsEven(4) 会导致 100 & 001，＆ 的结果将是 000。
     */
    public static boolean isEven(final int value) {
        return (value & 0b1) == 0;
    }

    /**
     * 检查一个值是2的正幂。 为了理解它是如何工作的，让我们假设我们调用了 IsPowerOfTwo(4)。 当值大于0时，将评估 && 运算符的右侧。
     */
    public static boolean isPowerOfTwo(final int value) {
        return value > 0 && ((value & (~value + 1)) == value);
    }

    /**
     * 生成一个介于 Integer.MIN_VALUE 和 Integer.MAX_VALUE 之间的随机数。
     */
    public static int generateRandomInt() {
        return ThreadLocalRandom.current().nextInt();
    }
}
