package com.lagou.edu.utils;

import java.util.function.Function;

public class UtilForData {

    /**
     * 根据码值来寻找枚举
     */
    public static <T extends Enum,R>T  getEnumByCode(T[] enums, Function<T,R> function, R r){

        for (T anEnum : enums) {
            if ( function.apply(anEnum).equals(r) ) {
                return anEnum;
            }
        }
        System.out.println("找不到枚举");
        return null;
    }
}
