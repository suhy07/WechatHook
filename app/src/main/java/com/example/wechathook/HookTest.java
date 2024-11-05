package com.example.wechathook;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject; // 导入JSON库
import com.example.wechathook.global.Wx;
import com.example.wechathook.global.Xposed;

import java.lang.reflect.Method;
import java.util.function.Consumer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookTest implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        String pkgName = lpparam.packageName;
        if (pkgName.equals("com.tencent.mm")) {
            Xposed.getInstance().hookMethod(
                    "com.tencent.mm.app.Application",
                    lpparam.classLoader,
                    "onBaseContextAttached",
                    param -> {
                        Context context = (Context) param.args[0];
                        if (Wx.WX_APP_CONTEXT == null) {
                            Wx.WX_APP_CONTEXT = context;
                        }
                        Wx.showToast(context.getPackageCodePath() + " Toast 注入成功");
                    },
                    param -> {},
                    Context.class,
                    long.class,
                    long.class
            );

            // Hook setAdapter method
            Xposed.getInstance().hookMethod(
                    "com.tencent.mm.ui.chatting.view.MMChattingListView",
                    lpparam.classLoader,
                    "setAdapter",
                    param -> {
                        ListAdapter adapter = (ListAdapter) param.args[0];
                        Xposed.getInstance().hookMethod(
                                "com.tencent.mm.ui.chatting.ChattingUIFragment",
                                lpparam.classLoader,
                                "doResume",
                                param1 -> {
                                    // 在这里处理doResume的逻辑
                                    int count = adapter.getCount();
                                    Wx.showToast("listview has " + count + " child");
                                    for (int i = 0; i < count; i++) {
                                        Object item = adapter.getItem(i);
                                        Wx.showToast("item data -> " + JSONObject.toJSONString(item));
                                        XposedBridge.log("item data -> " + JSONObject.toJSONString(item));
                                    }
                                },
                                param1 -> {}
                        );
                    },
                    param -> {},
                    BaseAdapter.class
            );
        }
    }
}