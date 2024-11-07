package com.example.wechathook.global;

public class GlobalMem {

    private static GlobalMem instance = null;

    private GlobalMem() {}

    public static GlobalMem getInstance(){
        if (instance == null) {
            instance = new GlobalMem();
        }
        return instance;
    }

//    public static final String TAG = "LAB_SUHY_WECHATHOOK";
    public static final String TAG = "LSPosed-Bridge";
}
