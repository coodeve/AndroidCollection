package com.coodev.androidcollection.Utils.system;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * 反射的工具类
 */
public class ReflectHelp {


    /*-------------------MethodHandle的使用------------------------*/
    static class Test {
        public static void enter(Object obj) {

        }

        public static MethodHandles.Lookup lookup() {
            return MethodHandles.lookup();// 方法句柄创建
        }
    }

    /**
     * MethodHandle ,MethodType测试代码
     * MethodHandle同样有权限问题，但它与反射 API 不同，其权限检查是在句柄的创建阶段完成的
     * 注意：
     * MethodHandle的访问权限不取决于方法句柄的创建位置，而是取决于 Lookup 对象的创建位置
     */
    public MethodHandle getMethodHandle1() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = Test.lookup();
        Method entry = Test.class.getDeclaredMethod("enter", Object.class);
        return lookup.unreflect(entry);
    }

    public MethodHandle getMethodHandle2() throws NoSuchMethodException, IllegalAccessException {
        MethodHandles.Lookup lookup = Test.lookup();
        MethodType methodType = MethodType.methodType(void.class, Object.class);
        return lookup.findStatic(Test.class, "enter", methodType);
    }

}
