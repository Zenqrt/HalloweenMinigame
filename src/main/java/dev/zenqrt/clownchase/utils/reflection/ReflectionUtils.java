package dev.zenqrt.clownchase.utils.reflection;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public final class ReflectionUtils {

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T getStaticDeclaredField(Class<?> clazz, String fieldName) {
        try {
            Field field = getAccessibleField(clazz, fieldName);
            return (T) field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static <T> Field getAccessibleField(Class<T> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }
}
