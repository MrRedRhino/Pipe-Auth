package org.pipeman.pa;

public class Utils {
    public static <T> T orElse(T input, T def) {
        return input == null ? def : input;
    }
}
