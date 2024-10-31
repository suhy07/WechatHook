package com.example.wechathook;

import android.util.Log;
import android.widget.ListView;

import com.alibaba.fastjson.JSONObject; // 导入JSON库
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookTest implements IXposedHookLoadPackage {

    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Log.i("Demo: hookListView", loadPackageParam.packageName);
        if (loadPackageParam.packageName.equals("com.tencent.mm")) {
            XposedBridge.log("Demo: hookListView");
            XposedBridge.log("com.tencent.mm");
            Class clazz = loadPackageParam.classLoader.loadClass("com.tencent.mm.ui.conversation.ConversationListView");
            // 确保使用正确的类名
            XposedHelpers.findAndHookMethod(
//                    "com.tencent.mm.ui.conversation.ConversationListView",
//                    loadPackageParam.classLoader,
//                    "setAdapter",
//                    ListView.class,
                    clazz,
                    "smoothScrollToPositionFromTop",
                    int.class, int.class,
                    new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log("找到辣! before");
                    Log.i("HOOK", "找到辣 before");
                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    XposedBridge.log("找到辣! after");
                    Log.i("HOOK", "找到辣 after");
                }
            });
        } else if (loadPackageParam.packageName.equals("com.example.wechathook")) {
            Log.i("HOOK", "main has Hooked!");
            Class clazz = loadPackageParam.classLoader.loadClass("com.example.wechathook.MainActivity");
            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    XposedBridge.log(" has Hooked!");
                }

                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult("你已被劫持111");
                    XposedBridge.log(" has Hooked!");
                    Log.i("HOOK", "main has Hooked! after");
                }
            });

        }
    }
}