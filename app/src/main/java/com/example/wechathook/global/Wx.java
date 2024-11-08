package com.example.wechathook.global;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.wechathook.R;

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

    public static void showImageView(ImageView imageView) {
        if (WX_APP_CONTEXT != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) {
                // 将Drawable转换为Bitmap
                Bitmap bitmap = drawableToBitmap(drawable);
                // 创建一个Toast消息，并显示Drawable
                Context context = WX_APP_CONTEXT;
                Toast toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
                // 设置Toast的位置
                toast.setGravity(Gravity.CENTER, 0, 0);
                // 创建一个ImageView作为Toast的内容
                ImageView toastImageView = new ImageView(context);
                toastImageView.setImageBitmap(bitmap);
                // 设置ImageView的布局参数
                toastImageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
                // 设置Toast的内容视图
                toast.setView(toastImageView);
                // 显示Toast
                toast.show();
            }
        } else {
            Log.i(GlobalMem.TAG, "ERR, APP CONTEXT IS NULL");
        }
    }


    // Drawable转Bitmap的方法
    private static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else {
            // 如果Drawable不是BitmapDrawable类型，尝试其他转换方法
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
    }

    public static void showViewInToast(Context context, View view) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);

        // 创建一个容器布局，用于包含自定义View
        LinearLayout container = new LinearLayout(context);
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        container.setGravity(Gravity.CENTER);

        // 将自定义View添加到容器布局中
        container.addView(view);

        // 将容器布局设置为Toast的内容视图
        toast.setView(container);
        toast.show();
    }

    public static void showViewAsWindow(View view) {
        Context context = WX_APP_CONTEXT;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER;

        windowManager.addView(view, params);
    }

    public static void showViewInDialog(Context context, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
