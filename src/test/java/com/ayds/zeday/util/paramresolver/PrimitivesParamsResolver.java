package com.ayds.zeday.util.paramresolver;

import java.lang.reflect.Parameter;
import java.util.List;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

public class PrimitivesParamsResolver extends RandomParamsResolver {

    public PrimitivesParamsResolver() {
        super(List.of(long.class, boolean.class, String.class));
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
        return null;
    }
}
