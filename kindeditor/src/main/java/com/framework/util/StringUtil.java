package com.framework.util;

/**
 * StringUtil
 *
 * @author D.Yeung
 * @since 2018.06.07
 */
public class StringUtil {

    /**
     * 判断字符串是否为空
     *
     * @param obj 字符串对象
     * @return 空返回true
     */
    public static boolean isNull(Object obj) {
        return null == obj || "".equals(obj) || "null".equals(obj);
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 字符串
     * @return 空返回true
     */
    public static boolean isNull(String str) {
        return null == str || "".equals(str) || "null".equals(str);
    }

    /**
     * 判断字符串是否非空
     *
     * @param obj 字符串对象
     * @return 非空返回true
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 判断字符串是否非空
     *
     * @param str 字符串
     * @return 非空返回true
     */
    public static boolean isNotNull(String str) {
        return !isNull(str);
    }

    /**
     * 判断每个字符串是否为空
     *
     * @param objs 字符串
     * @return 全部为空返回true
     */
    public static boolean everyIsNull(Object... objs) {
        for (Object obj: objs) {
            if (isNotNull(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断每个字符串是否为空
     *
     * @param strs 字符串
     * @return 全部为空返回true
     */
    public static boolean everyIsNull(String... strs) {
        for (String str: strs) {
            if (isNotNull(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断每个字符串是否非空
     *
     * @param objs 字符串对象
     * @return 全部非空返回true
     */
    public static boolean everyIsNotNull(Object... objs) {
        for (Object obj: objs) {
            if (isNull(obj)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断每个字符串是否非空
     *
     * @param strs 字符串
     * @return 全部非空返回true
     */
    public static boolean everyIsNotNull(String... strs) {
        for (String str: strs) {
            if (isNull(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断某些字符串是否为空
     *
     * @param objs 字符串
     * @return 包含空元素返回true
     */
    public static boolean someIsNull(Object... objs) {
        return !everyIsNotNull(objs);
    }

    /**
     * 判断某些字符串是否为空
     *
     * @param strs 字符串
     * @return 包含空元素返回true
     */
    public static boolean someIsNull(String... strs) {
        return !everyIsNotNull(strs);
    }

    /**
     * 判断某些字符串是否非空
     *
     * @param objs 字符串
     * @return 包含非空元素返回true
     */
    public static boolean someIsNotNull(Object... objs) {
        return !everyIsNull(objs);
    }

    /**
     * 判断某些字符串是否非空
     *
     * @param strs 字符串
     * @return 包含非空元素返回true
     */
    public static boolean someIsNotNull(String... strs) {
        return !everyIsNull(strs);
    }

    /**
     * 反转字符串
     *
     * @param str 字符串
     * @return 反转后的字符串
     */
    public static String reverse(String str) {
        StringBuilder res = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            res.append(str.charAt(i));
        }
        return res.toString();
    }
}