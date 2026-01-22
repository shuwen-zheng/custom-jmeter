package com.shuwen.jmeter.plugins.functions.utils;

public class StringUtils {

    public static boolean isNonBlank(String string){
        return string != null && string.trim().length() > 0;
    }

}
