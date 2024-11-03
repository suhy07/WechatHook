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

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookTest implements IXposedHookLoadPackage {

    private static String TAG = "HOOK_TEST";
    private static Context context = null;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        String pkgName = loadPackageParam.packageName;
        ClassLoader classLoader = loadPackageParam.classLoader;

        if (pkgName.equals("com.example.wechathook")) {
            XposedHelpers.findAndHookMethod("com.example.wechathook.MainActivity", classLoader,
                    "onCreate", Bundle.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            // 存储 Context 对象
                            context = (Context) param.thisObject;
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            // 在这里可以使用 storedContext 做其他操作
                        }
                    });

            Class<?> clazz = XposedHelpers.findClass("com.example.wechathook.MainActivity", classLoader);
            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log(" has Hooked!");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你已被劫持111");
                    showToast("hello world");
                    XposedBridge.log(" has Hooked!");
                    Log.i("HOOK", "main has Hooked! after");
                }
            });
        } else if (pkgName.equals("com.tencent.mm")) {
            XposedHelpers.findAndHookMethod("com.tencent.mm.app.Application", classLoader,
                    "onBaseContextAttached", Context.class, long.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            // 在这里存储 Context 对象
                            context = (Context) param.args[0];
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            // 在这里可以使用 storedContext 做其他操作
                            showToast(context.getPackageName() + "-Toast 注入成功");
                        }
                    });
            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.view.MMChattingListView", classLoader,
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
                            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.chatting.ChattingUIFragment", classLoader, "doResume", new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    // 在onResume方法执行之前执行的代码
                                    int count = adapter.getCount();
                                    showToast("listview has " + count + " child");
                                    for (int i = 0; i < count; i++) {
                                        Object s = adapter.getItem(i);
                                        showToast("item data -> " + JSONObject.toJSONString(s));
                                        Log.i(TAG, "item data -> " + JSONObject.toJSONString(s);
                                    }
                                }

                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    // 在onResume方法执行之后执行的代码

                                }
                            });
                        }
                    });
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