package com.sagar.util;

import java.util.List;

public class ValidationUtil {

    public static boolean isStringEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isStringNotEmpty(String string) {
        return !isStringEmpty(string);
    }

    public static <T> boolean isListEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isListNotEmpty(List<T> list) {
        return !isListEmpty(list);
    }

    public static boolean isEmailValid(String email) {
        return isStringNotEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
