package org.delusion.elgame.utils;

import java.util.function.Consumer;
import java.util.function.Function;

public class Toggleable<T> {

    private T value;
    private boolean enabled;

    private Toggleable(T value, boolean enabled) {
        this.value = value;
        this.enabled = enabled;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void toggle() {
        enabled = !enabled;
    }

    public void off() {
        enabled = false;
    }

    public void on() {
        enabled = true;
    }


    public static <U> Toggleable<U> off(U value) {
        return new Toggleable<>(value, false);
    }

    public static <U> Toggleable<U> on(U value) {
        return new Toggleable<>(value, true);
    }

    public <U> U ifApply(Function<T, U> f, U defaultv) {
        if (enabled) {
            return f.apply(value);
        }
        return defaultv;
    }

    public void ifAccept(Consumer<T> f) {
        if (enabled) {
            f.accept(value);
        }
    }



}
