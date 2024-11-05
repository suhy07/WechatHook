package com.example.wechathook.global;

import android.content.Context;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Xposed {

    private static Xposed instance = null;

    private Xposed() {}

    public static Xposed getInstance(){
        if (instance == null) {
            instance = new Xposed();
        }
        return instance;
    }

    public void hookMethod(String className,
                           ClassLoader classLoader,
                           String methodName,
                           Consumer<XC_MethodHook.MethodHookParam> before,
                           Consumer<XC_MethodHook.MethodHookParam> after,
                           Object... parameterTypes) {
        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(className);
            hookMethod(clazz, methodName, before, after, parameterTypes);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    public void hookMethod(Class<?> clazz,
                           String methodName,
                           Consumer<XC_MethodHook.MethodHookParam> before,
                           Consumer<XC_MethodHook.MethodHookParam> after,
                           Object... parameterTypes) {
        try {
            Object callback = new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    before.accept(param);
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    after.accept(param);
                }
            };
            hookMethod(clazz, methodName, callback, parameterTypes);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    public void hookMethod(Class<?> clazz,
                           String methodName,
                           Object callback,
                           Object... parameterTypes) {
        try {
            // 创建一个新的数组，长度为 parameterTypes 的长度加 1
            Object[] parameterTypesAndCallback = new Object[parameterTypes.length + 1];

            // 先将原来的 parameterTypes 数组复制到新的数组中
            System.arraycopy(parameterTypes, 0, parameterTypesAndCallback, 0, parameterTypes.length);

            // 将 callback 添加到新数组的最后一个位置
            parameterTypesAndCallback[parameterTypes.length] = callback;

            // 使用新的 parameterTypesAndCallback 调用 XposedHelpers.findAndHookMethod
            XposedHelpers.findAndHookMethod(
                    clazz,
                    methodName,
                    parameterTypesAndCallback
            );
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }


    public void hookMethods(String className, ClassLoader classLoader, String methodName, XC_MethodHook xmh) {
        try {
            Class<?> clazz = XposedHelpers.findClass(className, classLoader);
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)
                        && !Modifier.isAbstract(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers())) {
                    XposedBridge.hookMethod(method, xmh);
                }
            }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}
