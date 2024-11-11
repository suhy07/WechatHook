package com.example.wechathook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject; // 导入JSON库
import com.example.wechathook.global.Wx;
import com.example.wechathook.global.Xposed;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookTest implements IXposedHookLoadPackage {
    ListAdapter NmA, NpN;
    ImageView Nmz, NoX, NoY, NoZ, NmE ;
    ListView.FixedViewInfo fixedViewInfo;
    Set<View> views = new HashSet<>();
    Object sqliteDatabase;
    static Set<Object> sqliteDatabases = new HashSet<>();
    Cursor queryCursor, rawQueryCursor;
    XC_MethodHook.MethodHookParam queryParam, rawQueryParam;
    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        String pkgName = lpparam.packageName;
        LoadPackageParam loadPackageParam = lpparam;
        String tmpTableName = "rcontact";
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
//                        Wx.showToast(context.getPackageCodePath() + " Toast 注入成功");
                    },
                    param -> {},
                    Context.class,
                    long.class,
                    long.class
            );

            // Hook setAdapter method
//            Xposed.getInstance().hookMethod(
//                    "com.tencent.mm.ui.chatting.view.MMChattingListView",
//                    lpparam.classLoader,
//                    "setAdapter",
//                    param -> {
//                        ListAdapter adapter = (ListAdapter) param.args[0];
//                        Xposed.getInstance().hookMethod(
//                                "com.tencent.mm.ui.chatting.ChattingUIFragment",
//                                lpparam.classLoader,
//                                "doResume",
//                                param1 -> {
//                                    // 在这里处理doResume的逻辑
//                                    int count = adapter.getCount();
//                                    Wx.showToast("listview has " + count + " child");
//                                    for (int i = 0; i < count; i++) {
//                                        Object item = adapter.getItem(i);
////                                        Wx.showToast("item data -> " + JSONObject.toJSONString(item));
//                                        XposedBridge.log("item data -> " + JSONObject.toJSONString(item));
//                                    }
//                                },
//                                param1 -> {}
//                        );
//                    },
//                    param -> {},
//                    BaseAdapter.class
//            );
//            Class<?> classDb = XposedHelpers.findClassIfExists(Wx.DB_PKG_NAME, lpparam.classLoader);
//            Xposed.getInstance().hookMethod(
//                    classDb,
//                    "insertWithOnConflict",
//                    param -> {
////                        Wx.showToast("\"insertWithOnConflict\" 数据库注入成功");
//                    },
//                    param -> {
//                        String tableName = (String) param.args[0];
//                        ContentValues contentValues = (ContentValues) param.args[2];
//                        if (tableName == null || tableName.length() == 0 || contentValues == null) {
//                            Wx.showToast("tableName == null || tableName.length() == 0 || contentValues == null");
//                            return;
//                        }
//                        //过滤掉非聊天消息
//                        if (!tableName.equals("message")) {
////                            Wx.showToast("!tableName.equals(\"message\")");
////                            Wx.showToast("tableName:" + tableName);
//                            return;
//                        }
//                        //打印出日志
////                        printInsertLog(tableName, (String) param.args[1], contentValues, (Integer) param.args[3]);
//
//                        //提取消息内容
//                        //1：表示是自己发送的消息
//                        int isSend = contentValues.getAsInteger("isSend");
//                        //消息内容
//                        String strContent = contentValues.getAsString("content");
//                        //说话人ID
//                        String strTalker = contentValues.getAsString("talker");
//                        //收到消息，进行回复（要判断不是自己发送的、不是群消息、不是公众号消息，才回复）
//                        if (isSend != 1 && !strTalker.endsWith("@chatroom") && !strTalker.startsWith("gh_")) {
////                            WechatUtils.replyTextMessage(loadPackageParam, "回复：" + strContent, strTalker);
//                            Wx.showToast("回复：" + strContent + strTalker);
//                            final String strChatroomId = strTalker;
//                            Wx.showToast("准备回复消息内容：content:" + strContent + ",chatroomId:" + strChatroomId);
//
//                            if (strContent == null || strChatroomId == null
//                                    || strContent.length() == 0 || strChatroomId.length() == 0) {
//                                return;
//                            }
//
//                            //构造new里面的参数：l iVar = new i(aao, str, hQ, i2, mVar.cvb().fD(talkerUserName, str));
//                            Class<?> classiVar = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.i", loadPackageParam.classLoader);
//                            Object objectiVar = XposedHelpers.newInstance(classiVar,
//                                    new Class[]{String.class, String.class, int.class, int.class, Object.class},
//                                    strChatroomId, strContent, 1, 1, new HashMap<String, String>() {{
//                                        put(strChatroomId, strChatroomId);
//                                    }});
//                            Object[] objectParamiVar = new Object[]{objectiVar, 0};
//
//                            //创建静态实例对象au.DF()，转换为com.tencent.mm.ab.o对象
//                            Class<?> classG = XposedHelpers.findClassIfExists("com.tencent.mm.kernel.g", loadPackageParam.classLoader);
//                            Object objectG = XposedHelpers.callStaticMethod(classG, "Eh");
//                            Object objectdpP = XposedHelpers.getObjectField(objectG, "dpP");
//
//
//                            //查找au.DF().a()方法
//                            Class<?> classDF = XposedHelpers.findClassIfExists("com.tencent.mm.ab.o", loadPackageParam.classLoader);
//                            Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.ab.l", loadPackageParam.classLoader);
//                            Method methodA = XposedHelpers.findMethodExactIfExists(classDF, "a", classI, int.class);
//
//                            //调用发消息方法
//                            try {
//                                XposedBridge.invokeOriginalMethod(methodA, objectdpP, objectParamiVar);
//                                Wx.showToast("invokeOriginalMethod()执行成功");
//                            } catch (Exception e) {
//                                Wx.showToast("调用微信消息回复方法异常");
//                                Wx.showToast(e.getMessage());
//                            }
//                        }
//                    },
//                    String.class,
//                    String.class,
//                    ContentValues.class,
//                    int.class
//            );
//            Xposed.getInstance().hookConstructor(
//                    "com.tencent.mm.ui.conversation.MainUI",
//                    lpparam.classLoader,
//                    param->{},
//                    param->{
//                        Wx.showToast("After MainUI init()");
//                        Object mainUI = param.thisObject;
//                        NpN = (ListAdapter) XposedHelpers.getObjectField(mainUI, "NpN");
//                    }
//            );
//            Xposed.getInstance().hookMethod(
//                    "com.tencent.mm.ui.conversation.MainUI",
//                    lpparam.classLoader,
//                    "fZI",
//                    param->{},
//                    param->{
//                        Wx.showToast("After fZI()");
//                        Object mainUI = param.thisObject;
//                        NmA = (ListAdapter) XposedHelpers.getObjectField(mainUI, "NmA");
//                        NpN = ((ListView) XposedHelpers.getObjectField(mainUI, "NpN")).getAdapter();
//                        Xposed.getInstance().hookMethod(
//                                "com.tencent.mm.ui.conversation.MainUI",
//                                lpparam.classLoader,
//                                "onResume",
//                                param1->{
//                                    int count = NmA.getCount();
//                                    Wx.showToast("NmA listview has " + count + " child");
//                                    for (int i = 0; i < count; i++) {
//                                        Object item = NmA.getItem(i);
////                                        Wx.showToast("NmA item data -> " + JSONObject.toJSONString(item));
//                                        XposedBridge.log("NmA item data -> " + JSONObject.toJSONString(item));
//                                    }
////                                    count = NpN.getCount();
////                                    Wx.showToast("NpN listview has " + count + " child");
////                                    for (int i = 0; i < count; i++) {
////                                        Object item = NpN.getItem(i);
//////                                        Wx.showToast("NpN item data -> " + JSONObject.toJSONString(item));
////                                        XposedBridge.log("NpN item data -> " + JSONObject.toJSONString(item));
////                                    }
//                                },
//                                param1->{}
//                        );
//                    }
//            );

            Class<?> sqliteDatabaseClass = XposedHelpers.findClass("android.database.sqlite.SQLiteDatabase", lpparam.classLoader);
            Class<?> cursorFactory = XposedHelpers.findClass("android.database.sqlite.SQLiteDatabase.CursorFactory", lpparam.classLoader);

            // Hook openDatabase 方法，签名为 (String, CursorFactory, int)
            XposedHelpers.findAndHookMethod(
                    "android.database.sqlite.SQLiteDatabase",
                    lpparam.classLoader,
                    "openDatabase",
                    String.class, cursorFactory, int.class,
                    new XC_MethodHook() {

                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            // 获取 openDatabase 方法的参数
                            String dbPath = (String) param.args[0];
                            int flags = (int) param.args[2];
                            XposedBridge.log("openDatabase called with dbPath: " + dbPath + " flags: " + flags);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            // 获取返回的 SQLiteDatabase 对象
                            SQLiteDatabase db = (SQLiteDatabase) param.getResult();
                            if (db != null) {
                                // 将打开的 SQLiteDatabase 对象添加到集合中
                                sqliteDatabases.add(db);
                                XposedBridge.log("SQLiteDatabase object added to set: " + db);
                            }
                        }
                    }
            );

//            Xposed.getInstance().hookMethod(
//                    "com.tencent.mm.ui.conversation.MainUI",
//                    lpparam.classLoader,
//                    "onResume",
//                    param->{},
//                    param->{
//                        Wx.showToast("After MainUI onResume");
//                        Wx.MAIN_UI = (Activity) XposedHelpers.callMethod(param.thisObject, "getActivity");
//                        if (Wx.MAIN_UI != null) {
//                            Wx.MAIN_UI.runOnUiThread(() -> {
//                                if (sqliteDatabase != null && rawQueryParam != null) {
////                                    // 首先查询表结构，获取列名
////                                    String tableInfoQuery = "PRAGMA table_info(" + tmpTableName + ")";
////
////                                    // 执行表结构查询
////                                    Cursor tableInfoCursor = (Cursor) XposedHelpers.callMethod(sqliteDatabase, "rawQuery", tableInfoQuery, null);
////                                    String[] columnNames = getColumnNames(tableInfoCursor);
////                                    Xposed.getInstance().log(Arrays.toString(columnNames));
////                                    // 然后查询整个表的数据
////                                    String dataQuery = "SELECT * FROM " + tmpTableName;
////
////                                    // 执行数据查询
////                                    Cursor dataCursor = (Cursor) XposedHelpers.callMethod(sqliteDatabase, "rawQuery", dataQuery, null);
////                                    queryTable(columnNames, dataCursor); // 使用获取的列名来查询数据
////                                    tableInfoCursor.close();
////                                    dataCursor.close();
////                                    queryParam.args[0] = dataQuery;
////                                    Cursor dataCursor1 = (Cursor) XposedHelpers.callMethod(sqliteDatabase, "query", queryParam.args);
////                                    queryTable(columnNames, dataCursor1); // 使用获取的列名来查询数据
////                                    dataCursor1.close();
////                            // 构造查询数据库表名的SQL语句
////                            String sql = "SELECT name FROM sqlite_master";
//                            // 使用XposedHelpers.callMethod主动调用SQLiteDatabase的rawQuery方法执行SQL语句
//                            Cursor cursor = (Cursor) XposedHelpers.callMethod(sqliteDatabase, "rawQuery", sql, null);
//                            // 打印或处理查询结果
//                            while (cursor.moveToNext()) {
//                                String tableName = cursor.getString(0); // 假设表名在第一列
//                                XposedBridge.log("Found table: " + tableName);
//                            }
//                            cursor.close(); // 关闭Cursor
//                                }
//                            });
//                        }
//                    }
//            );

//            Xposed.getInstance().hookMethod(
//                    "com.tencent.mm.ui.conversation.i",
//                    lpparam.classLoader,
//                    "getView",
//                    param->{},
//                    param-> {
////                        Wx.showToast("After getView");
////                        View view = (View) param.getResult();
////
////                        // 假设f实例被设置为View的tag
////                        Object fVar = view.getTag();
////
////                        // 获取fVar中的Nmz, NoX, NoY, NoZ字段
////                        // 我们需要知道这些字段的名称和类型
////                        Nmz = (ImageView) XposedHelpers.getObjectField(fVar, "Nmz");
////                        NoX = (ImageView) XposedHelpers.getObjectField(fVar, "NoX");
////                        NoY = (ImageView) XposedHelpers.getObjectField(fVar, "NoY");
////                        NoZ = (ImageView) XposedHelpers.getObjectField(fVar, "NoZ");
////                        Wx.showImageView(Nmz);
////                        Wx.showImageView(NoX);
////                        Wx.showImageView(NoY);
////                        Wx.showImageView(NoZ);
//                    },
//                    int.class,
//                    View.class,
//                    ViewGroup.class
//            );

//            Xposed.getInstance().hookMethod(
//                    "com.tencent.mm.ui.conversation.ConversationListView",
//                    lpparam.classLoader,
//                    "addHeaderView",
//                    param->{
////                        Wx.showToast("Before addHeaderView");
//                        View view = (View) param.args[0];
////                        Wx.showToast("View: " + view.toString());
//                        Xposed.getInstance().log("View: " + view.toString());
//                        views.add(view);
////                        try {
////                            Wx.showViewInToast(Wx.WX_APP_CONTEXT, view);
////                        } catch (Exception e) {
////                            Xposed.getInstance().log(e.getMessage());
////                        }
//                        // 获取Context对象
//                        Object conversationListView = param.thisObject;
//                        Context context = (Context) XposedHelpers.callMethod(conversationListView, "getContext");
//                        // 打印Context信息，您可以在这里进行其他操作
////                        Wx.showToast("获取到Context: " + context);
//                        Xposed.getInstance().log("获取到Context: " + context);
//                        try {
////                            Wx.showToast("show view");
//                            Xposed.getInstance().log("show view");
////                            Wx.showViewInDialog(context, view);
////                            Wx.showViewInToast(context, view);
//                        } catch (Exception e) {
//                            Xposed.getInstance().log(e.getMessage());
//                        }
//                    },
//                    param-> {
////                        Wx.showToast("After addHeaderView");
////                        ListView.FixedViewInfo fixedViewInfo = ((ListView.FixedViewInfo) XposedHelpers.
////                                getObjectField(param.thisObject, "fixedViewInfo")).getLast();
////                        // 现在可以对fixedViewInfo对象进行操作
////                        View view = fixedViewInfo.view;
////                        Object data = fixedViewInfo.data;
////                        boolean isSelectable = fixedViewInfo.isSelectable;
////                        // 例如，打印出view的信息
////                        Wx.showToast("HeaderView added: " + data.toString());
////                        Wx.showViewInToast(view);
//                    },
//                    View.class,
//                    Object.class,
//                    boolean.class
//            );
//
//            Xposed.getInstance().hookConstructor(
//                    "com.tencent.mm.boot.svg.code.drawable.default_avatar",
//                    lpparam.classLoader,
//                    param->{
//                        Wx.showToast("Before avatar.c create");
//                    },
//                    param-> {
//                        Wx.showToast("After avatar.c create");
//                        ImageView imageView = (ImageView) param.thisObject;
//                        Xposed.getInstance().hookMethod(
//                                "com.tencent.mm.boot.svg.code.drawable.default_avatar",
//                                lpparam.classLoader,
//                                "doCommand",
//                                param1-> {
//                                    Wx.showToast("Before avatar.c doCommand");
//                                },
//                                param2-> {
//                                    Wx.showToast("After avatar.c doCommand");
//                                },
//                                int.class,
//                                Object[].class
//                        );
//
//                    }
//            );
//            // 找到 SQLiteDatabase 类
//            Class<?> sqliteDatabaseClass = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader);
//
//            // Hook query 方法
//            XposedHelpers.findAndHookMethod(
//                    sqliteDatabaseClass,
//                    "query",
//                    boolean.class,
//                    String.class,
//                    String[].class,
//                    String.class,
//                    Object[].class,
//                    String.class,
//                    String.class,
//                    String.class,
//                    String.class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            // 获取原始 SQL 查询语句
//                            String originalQuery = (String) param.args[0];  // SQL 查询字符串
//
//                            // 在这里替换您的 SQL 查询
//                            String newQuery = "SELECT * FROM your_table WHERE your_condition = 1"; // 自定义 SQL
//
//                            // 替换原始 SQL 查询
//                            param.args[0] = newQuery;
//
//                            // 输出替换的 SQL
//                            XposedBridge.log("Original SQL: " + originalQuery);
//                            XposedBridge.log("Modified SQL: " + newQuery);
//                        }
//                    }
//            );

//            // Hook rawQuery 方法
//            XposedHelpers.findAndHookMethod(
//                    sqliteDatabaseClass,
//                    "rawQuery",
//                    String.class,
//                    Object[].class,
//                    new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            // 获取原始 SQL 查询语句
//                            String originalQuery = (String) param.args[0];  // SQL 查询字符串
//
//                            // 在这里替换您的 SQL 查询
//                            String newQuery = "SELECT * FROM your_table WHERE your_condition = 1"; // 自定义 SQL
//
//                            // 替换原始 SQL 查询
//                            param.args[0] = newQuery;
//
//                            // 输出替换的 SQL
//                            XposedBridge.log("Original SQL: " + originalQuery);
//                            XposedBridge.log("Modified SQL: " + newQuery);
//                        }
//                    }
//            );

//// Hook 微信的 `MainUI` 类中的一个方法
//            XposedHelpers.findAndHookMethod(
//                    "com.tencent.mm.ui.conversation.MainUI",
//                    lpparam.classLoader,
//                    "fZI",  // 假设该方法存在并会被触发
//                    new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            // 使用 MMApplicationContext.getContext() 获取全局 Context
//                            Class<?> mmAppContextClass = XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.MMApplicationContext", lpparam.classLoader);
//                            Context context = (Context) XposedHelpers.callStaticMethod(mmAppContextClass, "getContext");
//
//                            if (context == null) {
//                                XposedBridge.log("获取 Context 失败");
//                                return;
//                            }
//
//                            // 调用 `getWXUin`
//                            String uin = (String) XposedHelpers.callStaticMethod(
//                                    XposedHelpers.findClass("com.tencent.recovery.wx.util.WXUtil", lpparam.classLoader),
//                                    "getWXUin",
//                                    context
//                            );
//                            Wx.showToast("微信 UIN (主动触发): " + uin);
//
//                            // 调用 `getWXUserName`
//                            String username = (String) XposedHelpers.callStaticMethod(
//                                    XposedHelpers.findClass("com.tencent.recovery.wx.util.WXUtil", lpparam.classLoader),
//                                    "getWXUserName",
//                                    context
//                            );
//                            Wx.showToast("微信用户名 (主动触发): " + username);
//                        }
//                    }
//            );
//
//            Class<?> sqliteDatabaseClass = XposedHelpers.findClass("com.tencent.wcdb.database.SQLiteDatabase", lpparam.classLoader);
            // Hook query 方法
            XposedHelpers.findAndHookMethod(
                    sqliteDatabaseClass,
                    "query",
                    boolean.class,
                    String.class,
                    String[].class,
                    String.class,
                    Object[].class,
                    String.class,
                    String.class,
                    String.class,
                    String.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            // 获取原始 SQL 查询语句
                            String originalQuery = (String) param.args[0];  // SQL 查询字符串
                            queryParam = param;
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            // 可以在此处处理返回结果
                            Cursor cursor = (Cursor) param.getResult();
                        }
                    }
            );

            // Hook rawQuery 方法
            XposedHelpers.findAndHookMethod(
                    sqliteDatabaseClass,
                    "rawQuery",
                    String.class,
                    Object[].class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            // 获取原始 SQL 查询语句
                            String originalQuery = (String) param.args[0];  // SQL 查询字符串
                            rawQueryParam = param;
                            sqliteDatabase = param.thisObject;
//                            Xposed.getInstance().log(originalQuery);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            // 可以在此处处理返回结果
                            rawQueryCursor = (Cursor) param.getResult();
                        }
                    }
            );
        }
    }
    // 从 Cursor 中提取列名
    private String[] getColumnNames(Cursor cursor) {
        if (cursor == null || !cursor.moveToFirst()) {
            return new String[0];
        }

        int columnCount = cursor.getColumnCount();
        String[] columnNames = new String[columnCount];

        // 获取所有列名
        for (int i = 0; i < columnCount; i++) {
            columnNames[i] = cursor.getColumnName(i);
        }
        return columnNames;
    }

    // 提取的查询方法，用于处理查询结果
    private void queryTable(String[] columnNames, Cursor cursor) {
        Xposed.getInstance().log("queryTable Cursor: " + cursor);
        if (cursor != null && cursor.moveToFirst()) {
            Xposed.getInstance().log("cursor != null && cursor.moveToFirst()");
            do {
                // 根据列名动态读取每一行的数据
//                StringBuilder row = new StringBuilder();
                for (String columnName : columnNames) {
                    int columnIndex = cursor.getColumnIndex(columnName);
                    Xposed.getInstance().log("columnName: " + columnName);
                    if (columnIndex != -1) {
                        String value = cursor.getString(columnIndex);  // 获取每列的值
                        Xposed.getInstance().log("columnName: " + columnName + " value: " + value);
//                        row.append(columnName).append(": ").append(value).append(" | ");
                    }
                }
//                Xposed.getInstance().log("Row: " + row.toString());  // 打印每行数据

            } while (cursor.moveToNext());
        }
    }
}