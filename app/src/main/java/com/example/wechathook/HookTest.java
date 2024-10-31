package com.example.wechathook;

import android.util.Log;
import android.widget.ListAdapter;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class HookTest implements IXposedHookLoadPackage {
    final static String TAG = "HOOK_TEST";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.tencent.mm")) {
            return;
        }

        try {
            // 获取d类的Class对象
            Class<?> dClass = XposedHelpers.findClass("com.tencent.mm.protocal.d", lpparam.classLoader);

            // 修改静态字段uGg的值
            XposedHelpers.setStaticIntField(dClass, "uGg", 0x28003533);
        } catch (Exception e) {

        }
    }
}