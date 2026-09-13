package com.fooze.fancyitems.util;

import java.lang.reflect.Method;

public class IrisAPI {
    private static Object api;
    private static Method shadersActive;

    // Gets Iris API
    static {
        try {
            Class<?> irisApi = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
            api = irisApi.getMethod("getInstance").invoke(null);
            shadersActive = irisApi.getMethod("isShaderPackInUse");
        } catch (ReflectiveOperationException | LinkageError ignored) {
        }
    }

    // Checks if a shader pack is active
    public static boolean shadersActive() {
        try {
            return api != null && (boolean) shadersActive.invoke(api);
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }
}