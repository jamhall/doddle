package dev.doddle.core.support;

import dev.doddle.core.exceptions.DoddleValidationException;

public class Objects {
    public static <T> T requireNonNull(final T object) {
        if (object == null) {
            throw new DoddleValidationException();
        }
        return object;
    }

    public static <T> T requireNonNullElse(final T object, final T defaultObject) {
        if (object == null) {
            return requireNonNull(defaultObject, "defaultObject");
        }
        return object;
    }

    public static <T> T requireNonNull(final T object, final String message) {
        if (object == null) {
            throw new DoddleValidationException(message);
        }
        return object;
    }

    public static boolean isNull(final Object object) {
        return object == null;
    }

}
