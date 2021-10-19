package dev.zenqrt.utils;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Utils {

    public static <K,V> void putOrReplace(Map<K,V> map, K key, V value) {
        if(map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }

    public static <T> T getOrDefault(T value, T defaultValue) {
        return value == null ? defaultValue : value;
    }

    public static <T, X extends Throwable> T getOrThrow(T value, Supplier<X> supplier) throws X {
        if(value == null) {
            throw supplier.get();
        }
        return value;
    }

    public static <T> void runIfExisting(T object, Consumer<T> function) {
        if(object != null) {
            function.accept(object);
        }
    }

}
