package com.mao.bloompoi.utils;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author bloom
 * @date 2018/2/25
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }
}
