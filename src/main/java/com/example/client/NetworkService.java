package com.example.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * @author 030
 * @date 20:11 2021/11/10
 * @description 网络服务模块
 * NetworkService模块需要提供的功能包括：
 *
 * connnect()：连接到服务器
 * disconnect()：断开与服务器的连接
 * isConnected()：判断当前是否已经连接到服务器
 * sendMessage()：发送聊天消息
 */
public class NetworkService {

    /*定义回调接口*/
    public interface Callback{
        void onConnected(String host, int port);    // 连接成功
        void onConnectFailed(String host, int port);    // 连接失败
        void onDisconnected();  // 已经断开连接
        void onMessageSent(String name, String msg);    // 消息已经发出
        void onMessageReceived(String name, String msg);    // 收到消息
    }

    private Callback callback;

    public void setCallback(Callback callback){
        this.callback = callback;
    }

    /*添加网络通信相关的成员变量*/
    // 套接字对象
    private Socket socket = null;
    // 套接字输入流对象，从这里读取收到的消息
    private DataInputStream inputStream = null;
    // 套接字输出流对象，从这里发送聊天消息
    private DataOutputStream outputStream = null;
    // 当前连接状态的标记变量
    private boolean isConnected = false;

    /**
     * 连接到服务器
     * @param host 服务器地址
     * @param port 服务器端口
     */
    public void connect(String host, int port){
        try {
            // 创建套接字对象，与服务器建立连接
            socket = new Socket(host, port);
            isConnected = true;
            // 通知外界已经连接
            if (callback != null){
                callback.onConnected(host, port);
            }
            // 开始侦听是否有聊天消息的到来
            beginListening();
        }catch (IOException e){
            // 连接服务器失败
            isConnected = false;
            // 通知外界连接失败
            if (callback != null){
                callback.onConnectFailed(host, port);
            }
            e.printStackTrace();
        }
    }


    /**
     * 断开连接
     */
    public void disconnect(){
        try {
            if (socket != null){
                socket.close();
            }
            if (inputStream != null){
                inputStream.close();
            }
            if (outputStream != null){
                outputStream.close();
            }
            isConnected = false;
            // 通知外界连接断开
            if (callback != null){
                callback.onDisconnected();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    /**
     * 是否已经连接到服务器
     * @return true 已连接，false 未连接
     */
    public boolean isConnected(){
        return isConnected;
    }


    /**
     * 发送聊天消息
     * @param name 用户名
     * @param msg 消息内容
     */
    public void sendMessage(String name, String msg){
        // 检查参数合法化
        if (name == null || "".equals(name) || msg == null || "".equals(msg)){
            return;
        }
        if (socket == null) { // 套接字对象必须已创建
            return;
        }
        try {
            // 将消息写入套接字的输出流
            outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(name + "#" + msg);
            outputStream.flush();
            // 通知外界消息已发送
            if (callback != null){
                callback.onMessageSent(name, msg);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void beginListening(){
       new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    inputStream = new DataInputStream(socket.getInputStream());
                    while (true){
                        String[] s = inputStream.readUTF().split("#");
                        if (callback != null){
                            callback.onMessageReceived(s[0], s[1]);
                        }
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start(); // 这里一定不要忘记调用 start 方法来开启线程
    }
}
