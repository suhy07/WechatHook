package com.example.wechathook;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject; // 导入JSON库

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookTest implements IXposedHookLoadPackage {

    private static Context context = null;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String pkgName = loadPackageParam.packageName;
        ClassLoader classLoader = loadPackageParam.classLoader;
        if (pkgName.equals("com.tencent.mm")) {
            hook_method("com.tencent.mm.app.Application", classLoader, "onBaseContextAttached",
                    Context.class, long.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            context = (Context) param.args[0];
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            showToast(context.getPackageName() + "-Toast 注入成功");
                        }
                    });
            hook_method("com.tencent.mm.ui.chatting.view.MMChattingListView", classLoader,
                    "setAdapter", BaseAdapter.class, new XC_MethodHook() {
                        ListAdapter adapter;

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            // 获取 ListView 的引用
                            adapter = (ListAdapter) param.args[0];

                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            hook_method("com.tencent.mm.ui.chatting.ChattingUIFragment", classLoader, "doResume", new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    int count = adapter.getCount();
                                    showToast("listview has " + count + " child");
                                    for (int i = 0; i < count; i++) {
                                        Object s = adapter.getItem(i);
                                        showToast("item data -> " + JSONObject.toJSONString(s));
                                        XposedBridge.log("item data -> " + JSONObject.toJSONString(s));
                                    }
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                                }
                            });
                        }
                    });
        }
    }

    private void hook_method(String className, ClassLoader classLoader, String methodName,
                             Object... parameterTypesAndCallback){
        try {
            XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hook_methods(String className, String methodName, XC_MethodHook xmh){
        try {
            Class<?> clazz = Class.forName(className);
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


    private void showToast(String msg) {
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        } else {
            XposedBridge.log("Context is not available");
        }
    }
}