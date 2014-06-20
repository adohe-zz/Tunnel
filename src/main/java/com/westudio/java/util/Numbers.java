package com.westudio.java.util;

/**
 * Created with IntelliJ IDEA.
 * User: tonyhe
 * Date: 14-6-20
 * Time: 下午1:25
 * To change this template use File | Settings | File Templates.
 */
public class Numbers {

    public static int parseInt(String num) {
        return parseInt(num, 0);
    }

    public static int parseInt(String num, int i) {
        if (num == null) {
            return i;
        }

        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return i;
        }
    }
}
