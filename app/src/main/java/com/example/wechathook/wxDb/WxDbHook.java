package com.example.wechathook.wxDb;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.wechathook.global.Wx;
import com.example.wechathook.global.Xposed;

import java.lang.reflect.Method;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class WxDbHook {

//    public void replyTextMessage(XC_LoadPackage.LoadPackageParam loadPackageParam,
//                                        String strContent, final String strChatroomId) {
//        XposedBridge.log("准备回复消息内容：content:" + strContent + ",chatroomId:" + strChatroomId);
//
//        if (strContent == null || strChatroomId == null
//                || strContent.length() == 0 || strChatroomId.length() == 0) {
//            return;
//        }
//
//        //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
//        Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.i", loadPackageParam.classLoader);
//        Object objectiVar = XposedHelpers.newInstance(classiVar,
//                new Class[]{String.class, String.class, int.class, int.class, Object.class},
//                strChatroomId, strContent, 1, 1, new HashMap<String, String>() {{
//                    put(strChatroomId, strChatroomId);
//                }});
//        Object[] objectParamiVar = new Object[]{objectiVar, 0};
//
//        //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
//        Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
//        Object objectG = XposedHelpers.callStaticMethod(classG, "Eh");
//        Object objectdpP = XposedHelpers.getObjectField(objectG, "dpP");
//
//
//        //查找au.DF().a()方法
//        Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.ab.o", loadPackageParam.classLoader);
//        Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.ab.l", loadPackageParam.classLoader);
//        Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);
//
//        //调用发消息方法
//        try {
//            XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
//            XposedBridge.log("invokeOriginalMethod()执行成功");
//        } catch (Exception e) {
//            XposedBridge.log("调用微信消息回复方法异常");
//            XposedBridge.log(e);
//        }
//    }

    public String select(String sql) {
        return sql;
    }
}
