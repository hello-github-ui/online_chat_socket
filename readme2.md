> 用java socket做一个聊天室，实现多人聊天的功能。看了极客学院的视频后跟着敲的。(1DAY)
<hr/>

服务端：
1. 先写服务端的类MyServerSocket，里面放一个监听线程，一启动就好
2. 实现服务端监听类ServerListener.java，用accept来监听，一旦有客户端连上，生成新的socket，
   就新建个线程实例ChatSocket。启动线程后就把线程交给ChatManager管理
3. 在ChatSocket中实现从客户端读取内容，把读取到的内容发给集合内所有的客户端
4. ChatManager里面用vector来管理socket线程实例ChatSocket，并实现发送信息给其他的客户端

客户端：
1. 新建一个继承JFrame的MainWindow.java类，主要实现聊天窗口的UI，以及事件响应。
2. 新建StartClient.java类，把MainWindow中生成MainWindow主方法部分代码拷贝过来，这样就能在主程序中把窗体执行出来了。
3. 新建ChatManager（需要单例化的类）管理socket，实现聊天的输入输出功能。最后记得在1中新建窗口后，
   传一份frame的引用到ChatManager中，才能实现ChatManager对界面的显示。