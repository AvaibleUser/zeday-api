package com.ayds.zeday.util.paramresolver;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

public class PrimitivesParamsResolver extends RandomParamsResolver {

    public PrimitivesParamsResolver() {
        super(List.of(long.class, boolean.class, String.class, LocalDate.class), List.of(Long.class, String.class));
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> type = parameter.getType();

        if (type == long.class) {
            return random.nextPositiveLong();
        }
        if (type == boolean.class) {
            return random.nextBoolean();
        }
        if (type == String.class) {
            return random.nextString();
        }
        if (type == LocalDate.class) {
            return random.nextDate();
        }
        if (type == List.class) {
            Type genericType = parameter.getParameterizedType();
            if (genericType instanceof ParameterizedType genType) {
                Type genClass = Arrays.stream(genType.getActualTypeArguments()).findFirst().get();
                if (genClass == String.class) {
                    return random.nextStrings();
                }
                if (genClass == Long.class) {
                    return random.nextObjects(random::nextPositiveLong, toList());
                }
            }
        }
        return null;
    }
}
