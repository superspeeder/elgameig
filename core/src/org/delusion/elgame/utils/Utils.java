package org.delusion.elgame.utils;

import java.util.function.Supplier;

public class Utils {

    public static <T> T evaluate(Supplier<T> f) {
        return f.get();
    }
}
