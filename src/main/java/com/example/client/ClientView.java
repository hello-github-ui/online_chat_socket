package com.example.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * @author 030
 * @date 20:01 2021/11/10
 * @description 客户端程序的UI视图
 */
public class ClientView extends JFrame implements ActionListener, KeyListener {

    /*成员变量*/
    private JTextArea taChatList;   // 聊天内容区
    private JTextField tfMessage;   // 聊天输入框
    private JTextField tfName;      // 用户名输入框
    private JButton btnSend;        // 发送按钮
    private JLabel labelNick;
    private JPanel jp1, jp2;

    private JScrollPane scrollPane;
    private JLabel labelHost;
    private JLabel labelPort;
    private JTextField tfHost;      // 服务器地址输入框
    private JTextField tfPort;      // 服务器端口输入框
    private JButton btnConnect;     // 连接/断开服务器按钮

    // 为 ClientView 类增加 NetworkService 模块并实现回调
    private NetworkService networkService;

    public ClientView(){
        initView();
        initNetworkService();
    }

    private void initView() {
        taChatList = new JTextArea(20, 20);
        taChatList.setEditable(false);

        scrollPane = new JScrollPane(taChatList);
        tfMessage = new JTextField(15);
        btnSend = new JButton("发送");

        jp1 = new JPanel();
        labelHost = new JLabel("主机地址");
        tfHost = new JTextField(15);
        tfHost.setText("localhost");
        labelPort = new JLabel("端口号");
        tfPort = new JTextField(4);
        tfPort.setText("8765");
        btnConnect = new JButton("连接");

        jp1.add(labelHost);
        jp1.add(tfHost);
        jp1.add(labelPort);
        jp1.add(tfPort);
        jp1.add(btnConnect);

        labelNick = new JLabel("昵称：");
        tfName = new JTextField(8);
        jp2 = new JPanel();
        jp2.add(labelNick);
        jp2.add(tfName);
        tfName.setText("用户0");
        jp1.setLayout(new FlowLayout(FlowLayout.CENTER));
        jp2.add(tfMessage);
        jp2.add(btnSend);
        jp2.setLayout(new FlowLayout(FlowLayout.CENTER));

        add(jp1, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(jp2, BorderLayout.SOUTH);
        setTitle("聊天室");
        setSize(500, 500);
        setLocation(450, 150);
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 当光标定位在聊天输入框时监听回车键按下事件
        tfMessage.addKeyListener(this);
        // 为发送按钮增加鼠标点击事件监听
        btnSend.addActionListener(this);
        // 为连接按钮增加鼠标点击事件监听
        btnConnect.addActionListener(this);
        // 当窗口关闭时触发
        addWindowListener(new WindowAdapter() { // 窗口关闭后断开连接
            @Override
            public void windowClosing(WindowEvent e) {
                networkService.disconnect();
            }
        });
    }

    /*处理鼠标点击事件*/
    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO
        if (e.getSource() == btnSend){
            // 响应发送数据
            sendMessage();
        }else if (e.getSource() == btnConnect) {
            // 响应连接/断开按钮
            if (!networkService.isConnected()){
                // 未连接状态下，执行连接服务器操作
                String host = tfHost.getText();
                int port = Integer.valueOf(tfPort.getText());
                networkService.connect(host, port);
            }else {
                // 已连接状态下，执行断开连接操作
                networkService.disconnect();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /*
    * 处理键盘按下事件
    * */
    @Override
    public void keyPressed(KeyEvent e) {
        // TODO
        if (e.getKeyCode() == KeyEvent.VK_ENTER){
            // 发送聊天消息
            sendMessage();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }


    /**
     * 专门写一个函数 initNetworkService 来初始化这个 networkService 对象，
     * 这个函数主要做两件事
     * 创建 networkService 对象(new)
     * 设置回调接口以处理 NetworkService 中各个网络操作发来的通知
     */
    private void initNetworkService(){
        networkService = new NetworkService();
        networkService.setCallback(new NetworkService.Callback() {
            @Override
            public void onConnected(String host, int port) {
               // 连接成功时，弹出对话框提示，并将按钮文字改为 “断开”
                alert("连接", "成功连接到[" + host + ":" + port + "]");
                btnConnect.setText("断开");
            }

            @Override
            public void onConnectFailed(String host, int port) {
                // 连接失败时，弹出对话框提示，并将按钮文字设置为“连接”
                alert("连接", "无法连接到[" + host + ":" + port + "]");
                btnConnect.setText("连接");
            }

            @Override
            public void onDisconnected() {
                // 断开连接时，弹出对话框提示，并将按钮文字设置为“连接”
                alert("连接", "连接已断开");
                btnConnect.setText("连接");
            }

            @Override
            public void onMessageSent(String name, String msg) {
                // 发出消息时，清空消息输入框，并将消息显示在消息区
                tfMessage.setText("");
                taChatList.append("我(" + name + "):\r\n" + msg + "\r\n");
            }

            @Override
            public void onMessageReceived(String name, String msg) {
                // 收到消息时，将消息显示在消息区
                taChatList.append(name + ":\r\n" + msg + "\r\n");
            }
        });
    }


    // 显示标题为 title，内容为 message 的对话框
    private void alert(String title, String message){
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
    }


    private void sendMessage(){
        // 响应发送按钮
        String name = tfName.getText();
        String msg = tfMessage.getText();
        // 检查参数合法化
        if (name == null || msg == null || "".equals(name) || "".equals(msg)){
            return;
        }
        // 发送消息
        networkService.sendMessage(name, msg);
    }


    public static void main(String[] args) {

        ClientView view = new ClientView();
    }

}
