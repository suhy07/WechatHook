package com.example.wechathook.global;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Wx {

    private static Wx instance = null;

    private Wx() {}

    public static Wx getInstance(){
        if (instance == null) {
            instance = new Wx();
        }
        return instance;
    }

    public static final String PKG_NAME = "com.tencent.mm";

    public static final String DB_PKG_NAME = "com.tencent.wcdb.database.SQLiteDatabase";

    public static Context WX_APP_CONTEXT = null;

    public static void showToast(String message) {
        if (WX_APP_CONTEXT != null) {
            Toast.makeText(WX_APP_CONTEXT, message, Toast.LENGTH_SHORT).show();
        } else {
            Log.i(GlobalMem.TAG, "ERR, APP CONTEXT IS NULL");
        }
    }
}
