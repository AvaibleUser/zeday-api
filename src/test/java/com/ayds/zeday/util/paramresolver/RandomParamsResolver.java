package com.ayds.zeday.util.paramresolver;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import com.ayds.zeday.util.RandomUtils;

public abstract class RandomParamsResolver implements ParameterResolver {

    protected final RandomUtils random = new RandomUtils();

    private final List<Class<?>> types;

    private final List<Class<?>> listTypes;

    protected RandomParamsResolver(List<Class<?>> types) {
        this(types, List.of());
    }

    protected RandomParamsResolver(List<Class<?>> types, List<Class<?>> listTypes) {
        this.types = types;
        this.listTypes = listTypes;
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        Parameter parameter = parameterContext.getParameter();
        Class<?> paramType = parameter.getType();
        Type genericType = parameter.getParameterizedType();
        if (paramType == List.class && genericType instanceof ParameterizedType type) {
            return Arrays.stream(type.getActualTypeArguments()).allMatch(listTypes::contains);
        }
        return types.contains(paramType);
    }
}
