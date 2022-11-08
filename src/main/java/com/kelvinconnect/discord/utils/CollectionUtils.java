package com.kelvinconnect.discord.utils;

import java.util.List;
import java.util.Objects;

public class CollectionUtils {

    private CollectionUtils() {
        throw new UnsupportedOperationException("do not instantiate");
    }

    @SafeVarargs
    public static <E> boolean listStartsWith(List<E> list, E... args) {
        if (list.size() < args.length) {
            return false;
        }

        for (int i = 0; i < args.length; ++i) {
            if (!Objects.equals(list.get(i), args[i])) {
                return false;
            }
        }

        return true;
    }
}
