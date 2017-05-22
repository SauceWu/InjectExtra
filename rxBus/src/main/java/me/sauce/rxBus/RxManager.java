package me.sauce.rxBus;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by sauce on 2017/5/22.
 */

public class RxManager {
    private static final Map<Class<?>, Constructor> BINDINGS = new LinkedHashMap<>();
    private static final Map<Class<?>, Method> UNBINDINGS = new LinkedHashMap<>();

    public static void init(Object o) {
        try {
            Constructor constructor = findBindingConstructorForClass(o.getClass());
            if (constructor != null)
                constructor.newInstance(o);
        } catch (NullPointerException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void unBind(Object o) {
        try {
            Method constructor = findUnBindingForClass(o.getClass());
            if (constructor != null)
                constructor.invoke(null, new Object[0]);
        } catch (NullPointerException | IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private static Constructor findBindingConstructorForClass(Class<?> cls) {
        Constructor bindingConstructor = BINDINGS.get(cls);
        if (bindingConstructor == null) {
            String clsName = cls.getName();
            if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
                return null;
            }
            try {
                Class<?> bindingClass = Class.forName(clsName + "_BusManager");
                //noinspection unchecked
                bindingConstructor = bindingClass.getConstructor(cls);
            } catch (ClassNotFoundException e) {
                bindingConstructor = findBindingConstructorForClass(cls.getSuperclass());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
            }
            BINDINGS.put(cls, bindingConstructor);
        }
        return bindingConstructor;
    }

    private static Method findUnBindingForClass(Class<?> cls) {
        Method unbindingMethod = UNBINDINGS.get(cls);
        if (unbindingMethod == null) {
            String clsName = cls.getName();
            if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
                return null;
            }
            try {
                Class<?> bindingClass = Class.forName(clsName + "_BusManager");
                //noinspection unchecked
                unbindingMethod = bindingClass.getDeclaredMethod("unBind", bindingClass);


            } catch (ClassNotFoundException e) {
                unbindingMethod = findUnBindingForClass(cls.getSuperclass());
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
            }
            UNBINDINGS.put(cls, unbindingMethod);
        }
        return unbindingMethod;
    }
}
