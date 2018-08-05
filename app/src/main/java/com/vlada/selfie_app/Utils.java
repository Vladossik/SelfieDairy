package com.vlada.selfie_app;

import java.util.List;

public class Utils {
    private Utils() {
    }
    
    public static <T> String joinToString(List<T> list) {
        StringBuilder s = new StringBuilder();
        for (T item : list) {
            s.append(", ").append(item);
        }
        return s.toString();
    }
}
