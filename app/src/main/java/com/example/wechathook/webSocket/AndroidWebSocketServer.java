package com.example.wechathook.webSocket;

import com.example.wechathook.global.Wx;

import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;

import java.net.InetSocketAddress;

public class AndroidWebSocketServer extends WebSocketServer {

    public AndroidWebSocketServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Wx.showToast("New connection from: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        Wx.showToast("Closed connection from: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        Wx.showToast("Message from " + conn.getRemoteSocketAddress() + ": " + message);
        // 处理消息或广播给其他客户端
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        Wx.showToast("Server started successfully");
    }

    public void broadcast(String message) {
        for (WebSocket conn : this.getConnections()) {
            Wx.showToast("send: " + message);
            conn.send(message);
            Wx.showToast("send finish! " + message);
        }
    }
}