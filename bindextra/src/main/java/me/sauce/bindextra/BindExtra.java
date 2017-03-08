package me.sauce.bindextra;

import android.app.Activity;
import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.annotation.VisibleForTesting;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by sauce on 2017/3/8.
 * Version 1.0.0
 */
public class BindExtra {

    @VisibleForTesting
    private static final Map<Class<?>, Constructor> BINDINGS = new LinkedHashMap<>();


    public static void inject(Activity activity) {

        try {
            Constructor constructor = findBindingConstructorForClass(activity.getClass());
            constructor.newInstance(activity);
        } catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @CheckResult
    @UiThread
    private static Constructor findBindingConstructorForClass(Class<?> cls) {
        Constructor bindingConstructor = BINDINGS.get(cls);
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            return null;
        }
        try {
            Class<?> bindingClass = Class.forName(clsName + "_ExtraBinding");
            //noinspection unchecked
            bindingConstructor = bindingClass.getConstructor(cls);
        } catch (ClassNotFoundException e) {
            bindingConstructor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        BINDINGS.put(cls, bindingConstructor);
        return bindingConstructor;
    }

}
