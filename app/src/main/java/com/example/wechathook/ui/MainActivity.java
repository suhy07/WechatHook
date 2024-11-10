package com.example.wechathook.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.wechathook.R;
import com.example.wechathook.webSocket.AndroidWebSocketServer;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private Context mContext = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AndroidWebSocketServer androidWebSocketServer = new AndroidWebSocketServer(8888);
        androidWebSocketServer.start();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, toastMessage(), Toast.LENGTH_SHORT).show();
                androidWebSocketServer.broadcast("hello world");
            }
        });
    }

    public String toastMessage() {
        return "我未被劫持";
    }

    public void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}