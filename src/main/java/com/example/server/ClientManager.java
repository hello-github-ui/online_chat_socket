package com.example.server;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ClientManager {

    public ArrayList<ChatSocket> chatSockets = new ArrayList<>();

    public void addClientSocket(Socket socket) {
        final ChatSocket chatSocket = new ChatSocket(socket, new ChatSocket.Callback() {

            @Override
            public void onReadSocket(ChatSocket cs, String msg) {
                // TODO Auto-generated method stub
                sendAll(cs, msg);
            }

            @Override
            public void onError(ChatSocket cs, String error) {
                synchronized (chatSockets) {
                    chatSockets.remove(cs);
                }
            }
        }); //新客户端连接

        synchronized (chatSockets) {
            chatSockets.add(chatSocket); //往客户端管理器里添加客户
        }

        chatSocket.start();
    }

    //向其他客户端发送数据
    public void sendAll(ChatSocket chatSocket, String msg) {
        synchronized (chatSockets) {
            for (ChatSocket cs : chatSockets) {
                if (!cs.equals(chatSocket)) {
                    cs.send(msg);
                }
            }
        }
    }

    public void close() throws IOException {
        synchronized (chatSockets) { //关闭各个连接
            for (ChatSocket socket : chatSockets) {
                socket.close();
            }
            chatSockets.clear();
        }
    }
}
