package com.example.wechathook;

import android.content.ContentValues;
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
import java.util.HashMap;
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
        LoadPackageParam loadPackageParam = lpparam;
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
//                                        Wx.showToast("item data -> " + JSONObject.toJSONString(item));
                                        XposedBridge.log("item data -> " + JSONObject.toJSONString(item));
                                    }
                                },
                                param1 -> {}
                        );
                    },
                    param -> {},
                    BaseAdapter.class
            );
            Class<?> classDb = XposedHelpers.findClassIfExists(Wx.DB_PKG_NAME, lpparam.classLoader);
            Xposed.getInstance().hookMethod(
                    classDb,
                    "insertWithOnConflict",
                    param -> {
                        Wx.showToast("\"insertWithOnConflict\" 数据库注入成功");
                    },
                    param -> {
                        String tableName = (String) param.args[0];
                        ContentValues contentValues = (ContentValues) param.args[2];
                        if (tableName == null || tableName.length() == 0 || contentValues == null) {
                            Wx.showToast("tableName == null || tableName.length() == 0 || contentValues == null");
                            return;
                        }
                        //过滤掉非聊天消息
                        if (!tableName.equals("message")) {
                            Wx.showToast("!tableName.equals(\"message\")");
                            return;
                        }
                        //打印出日志
//                        printInsertLog(tableName, (String) param.args[1], contentValues, (Integer) param.args[3]);

                        //提取消息内容
                        //1：表示是自己发送的消息
                        int isSend = contentValues.getAsInteger("isSend");
                        //消息内容
                        String strContent = contentValues.getAsString("content");
                        //说话人ID
                        String strTalker = contentValues.getAsString("talker");
                        //收到消息，进行回复（要判断不是自己发送的、不是群消息、不是公众号消息，才回复）
                        if (isSend != 1 && !strTalker.endsWith("@chatroom") && !strTalker.startsWith("gh_")) {
//                            WechatUtils.replyTextMessage(loadPackageParam, "回复：" + strContent, strTalker);
                            Wx.showToast("回复：" + strContent + strTalker);
                            final String strChatroomId = strTalker;
                            Wx.showToast("准备回复消息内容：content:" + strContent + ",chatroomId:" + strChatroomId);

                            if (strContent == null || strChatroomId == null
                                    || strContent.length() == 0 || strChatroomId.length() == 0) {
                                return;
                            }

                            //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
                            Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.i", loadPackageParam.classLoader);
                            Object objectiVar = XposedHelpers.newInstance(classiVar,
                                    new Class[]{String.class, String.class, int.class, int.class, Object.class},
                                    strChatroomId, strContent, 1, 1, new HashMap<String, String>() {{
                                        put(strChatroomId, strChatroomId);
                                    }});
                            Object[] objectParamiVar = new Object[]{objectiVar, 0};

                            //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
                            Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
                            Object objectG = XposedHelpers.callStaticMethod(classG, "Eh");
                            Object objectdpP = XposedHelpers.getObjectField(objectG, "dpP");


                            //查找au.DF().a()方法
                            Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.ab.o", loadPackageParam.classLoader);
                            Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.ab.l", loadPackageParam.classLoader);
                            Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);

                            //调用发消息方法
                            try {
                                XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
                                Wx.showToast("invokeOriginalMethod()执行成功");
                            } catch (Exception e) {
                                Wx.showToast("调用微信消息回复方法异常");
                                Wx.showToast(e.getMessage());
                            }
                        }
                    },
                    String.class,
                    String.class,
                    ContentValues.class,
                    int.class
            );
        }
    }
}