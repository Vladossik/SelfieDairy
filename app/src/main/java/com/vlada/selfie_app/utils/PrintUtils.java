package com.vlada.selfie_app.utils;

import java.util.List;

public class PrintUtils {
    private PrintUtils() {
    }
    
    public static <T> String joinToString(List<T> list) {
        StringBuilder s = new StringBuilder();
        for (T item : list) {
            s.append(", ").append(item);
        }
        return s.toString();
    }
}
