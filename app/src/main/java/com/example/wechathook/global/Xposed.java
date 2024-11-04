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
                           Class<?>... parameterTypes){
        try {
            XC_MethodHook callback = new XC_MethodHook() {
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
            Class clazz = classLoader.loadClass(className);
            XposedHelpers.findAndHookMethod(
                    clazz,
                    methodName,
                    parameterTypes,
                    callback
            );
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    public void hookMethods(String className, ClassLoader classLoader, String methodName, XC_MethodHook xmh){
        try {
            Class<?> clazz = XposedHelpers.findClass(className, classLoader);
            for (Method method : clazz.getDeclaredMethods())
                if (method.getName().equals(methodName)
                        && !Modifier.isAbstract(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers())) {
                    XposedBridge.hookMethod(method, xmh);
                }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}