package com.westudio.java.util;

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
