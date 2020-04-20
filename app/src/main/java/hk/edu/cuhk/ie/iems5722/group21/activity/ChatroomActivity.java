package hk.edu.cuhk.ie.iems5722.group21.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedList;

import hk.edu.cuhk.ie.iems5722.group21.Interface.AsyncResponse;
import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.adapter.MessageAdapter;
import hk.edu.cuhk.ie.iems5722.group21.connect.Common;
import hk.edu.cuhk.ie.iems5722.group21.connect.FetchChatdataTask;
import hk.edu.cuhk.ie.iems5722.group21.entity.Message;
import hk.edu.cuhk.ie.iems5722.group21.entity.User;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatroomActivity extends AppCompatActivity implements AbsListView.OnScrollListener {

    private LinkedList<Message> MessageList = new LinkedList<Message>();
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private MessageAdapter adapter;
    private ListView listView;
    private int total_pages;
    private int current_page;
    private String url = "http://18.188.52.141/api/a3/get_messages";
    private String post_url = "http://18.188.52.141/api/a3/send_message";
    private boolean isLoad = false;
    private int chatroom_id;
    private Socket socket;
    private String channeId = "ChatroomActivity";

    // join the room
    private Emitter.Listener onConnectSuccess = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject json = new JSONObject();
                    try {
                        json.put("chatroom_id", chatroom_id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.v("join the room", String.valueOf(chatroom_id));
                    socket.emit("join", json);
                }
            });
        }
    };

    // 接受广播刷新消息
    private Emitter.Listener Message_Broadcast = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                JSONObject data = (JSONObject) args[0];
                final String chatroomid = data.getString("chatroom_id");
                //if(!chatroomid.equals(String.valueOf(chatroom_id))) return;
                Log.v("before the message", chatroomid);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v("get the message from ", chatroomid);
//                        Toast t = Toast.makeText(ChatroomActivity.this,"message from chatroom_id : " + chatroomid, Toast.LENGTH_LONG);
//                        t.show();
                        String chatroom_name = chatroom_name(Integer.valueOf(chatroomid));
                        // 设置通知
                        NotificationManager notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        Notification notification = new NotificationCompat.Builder(ChatroomActivity.this,channeId)
                                .setContentTitle("Messages Received")  //设置标题
                                .setContentText("message from : " + chatroom_name) //设置内容
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .setWhen(System.currentTimeMillis())  //设置时间
                                .setSmallIcon(R.drawable.cuhk)  //设置小图标
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.cuhk))//设置大图标
                                .build();
                        notifyManager.notify(1, notification);
                        current_page = 1;
                        String init_url = url + "?" + "chatroom_id=" + chatroom_id + "&" + "page=" + current_page;
                        //Log.v("url", init_url);
                        //清空原链表
                        MessageList.clear();
                        FetchChatdataTask fetchChatdataTask =
                                new FetchChatdataTask(ChatroomActivity.this,init_url,MessageList,adapter);
                        fetchChatdataTask.execute();
                        //接口回调获取get回来的page数据
                        fetchChatdataTask.setOnAsyncResponse(new AsyncResponse(){
                            //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                            @Override
                            public void onDataReceivedSuccess(HashMap<String,Integer> Data) {

                                total_pages = Data.get("total_pages");
                                current_page = Data.get("current_page");
                            }
                            @Override
                            public void onDataReceivedFailed() {}
                        });
                        listView.setSelection(MessageList.size());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    //return chatroom name accord to chatroomid
    String chatroom_name(int chatroom_id){
        String name = "";
        switch(chatroom_id) {
            case 1:
                name =  "Public chatroom"; break;
            case 2:
                name = "General chatroom";break;
            case 3:
                name = "real-time chatroom";break;
        }
        return name;
    }

    //获得url = "http://18.188.52.141/api/a3/get_messages"
    public String getUrl() {
        return url;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        Bundle b = getIntent().getExtras();
        String room_name = b.getString("name");
        toolbar.setTitle(room_name);
        total_pages = 1;
        current_page = 1;
        adapter = new MessageAdapter(ChatroomActivity.this, R.layout.message, MessageList);
        // Attach the adapter to a ListView
        listView = (ListView) findViewById(R.id.list_view);

        listView.setAdapter(adapter);
        listView.setOnScrollListener(this); //设置下拉加载其他页面

        ImageButton SendButton=(ImageButton)findViewById(R.id.SendButton);
        ImageButton RefreshButton = (ImageButton)findViewById(R.id.RefreshButton);
        final EditText editText=(EditText)findViewById(R.id.chat_bottom_edittext);

        //初始化数据 包括chatroom_id
        getMsgs(b,current_page);

        try {
            socket = IO.socket("http://18.188.52.141:8001/");
            socket.on(Socket.EVENT_CONNECT,  onConnectSuccess);
            socket.on("messages", Message_Broadcast);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }


        //发送按钮
        SendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String message = editText.getText()+"";
                if(message.length()==0){
                    Toast t = Toast.makeText(ChatroomActivity.this,"empty content！", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                //重置文本区域
                editText.setText("");
                if(message.length() < 200) {
                    SendMsgTask sendMsgTask = new SendMsgTask(ChatroomActivity.this, post_url,
                            chatroom_id, User.current_user.getUser_id(), User.current_user.getUsername(), message, MessageList, adapter);
                    sendMsgTask.execute();
                }
                //Log.v("===MessageList===",MessageList.toString());
            }
        });

        //刷新按钮
        RefreshButton.setOnClickListener(new View.OnClickListener(){
                @Override
                 public void onClick(View v) {
                 current_page = 1;
                 String init_url = url + "?" + "chatroom_id=" + chatroom_id + "&" + "page=" + current_page;
                    //Log.v("url", init_url);
                    //清空原链表
                    MessageList.clear();
                    FetchChatdataTask fetchChatdataTask =
                            new FetchChatdataTask(ChatroomActivity.this,init_url,MessageList,adapter);
                    fetchChatdataTask.execute();
                    //接口回调获取get回来的page数据
                    fetchChatdataTask.setOnAsyncResponse(new AsyncResponse(){
                        //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                        @Override
                        public void onDataReceivedSuccess(HashMap<String,Integer> Data) {

                            total_pages = Data.get("total_pages");
                            current_page = Data.get("current_page");
                        }
                        @Override
                        public void onDataReceivedFailed() {}
                    });
//                    Log.v("===MessageList===",MessageList.toString());
//                    Log.v("===total_pages===",Integer.valueOf(total_pages).toString());
//                    Log.v("===current_page===",Integer.valueOf(current_page).toString());
                }
        });
    }

    @Override
    protected void onDestroy() {
        JSONObject json = new JSONObject();
        try {
            json.put("chatroom_id", chatroom_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.v("leave the room", String.valueOf(chatroom_id));
        //先退出房间再断开连接
        socket.emit("leave", json);
        if (socket != null) {
            socket.disconnect();
            socket.off();
            Log.v("after socket status ", String.valueOf(socket.connected()));
        }
        super.onDestroy();
    }

    //页面数据初始化
    private void getMsgs(Bundle b, final int Current_page) {
            final int chatroom_id = b.getInt("id");
            this.chatroom_id = chatroom_id;
            String init_url = url + "?" + "chatroom_id=" + chatroom_id + "&" + "page=" + Current_page;
            FetchChatdataTask fetchChatdataTask =
                    new FetchChatdataTask(this,init_url,MessageList,adapter);
            fetchChatdataTask.execute();
            fetchChatdataTask.setOnAsyncResponse(new AsyncResponse(){
                 //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                 @Override
                 public void onDataReceivedSuccess(HashMap<String,Integer> Data) {

                          total_pages = Data.get("total_pages");
                          current_page = Data.get("current_page");
                 }
                 @Override
                 public void onDataReceivedFailed() {}
              });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //下拉加载其他页面
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // listview滑动到顶部，并且listview处于静止的状态
        if (scrollState == SCROLL_STATE_IDLE && isLoad) {
            isLoad = false;
            // 重新请求网络数据
            int Current_page = this.current_page + 1;
            if (Current_page <= total_pages) {
                String init_url = url + "?" + "chatroom_id=" + this.chatroom_id + "&" + "page=" + Current_page;
//                Log.v("url", init_url);
                FetchChatdataTask fetchChatdataTask =
                        new FetchChatdataTask(this, init_url, MessageList, adapter);
                fetchChatdataTask.execute();
                fetchChatdataTask.setOnAsyncResponse(new AsyncResponse() {
                    //通过自定义的接口回调获取AsyncTask中onPostExecute返回的结果变量
                    @Override
                    public void onDataReceivedSuccess(HashMap<String, Integer> Data) {

                        total_pages = Data.get("total_pages");
                        current_page = Data.get("current_page");
                    }

                    @Override
                    public void onDataReceivedFailed() {
                    }
                });
            }else {
                Toast.makeText(this,"Last Page Already",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        // 判断是否需要加载另一页数据
        if ((visibleItemCount > 0) && (firstVisibleItem == 0)) {
            if (view.getChildAt(0).getTop() >= 0) {
                // 当listview滑动到顶部时 在这做处理
                isLoad = true;
            }
        }
    }

    //httpTask
     class SendMsgTask extends AsyncTask<Void,Integer,String> {

        private Context context;
        private String url;
        private int chatroom_id;
        private int user_id;
        private String name;
        private String message;
        private LinkedList<Message> MessageList;
        private MessageAdapter adapter;

        public SendMsgTask(Context context ,String url, int chatroom_id, int user_id, String name, String message,
                           LinkedList<Message> MessageList, MessageAdapter adapter){
            this.context = context;
            this.url = url;
            this.MessageList = MessageList;
            this.adapter = adapter;
            this.chatroom_id = chatroom_id;
            this.message = message;
            this.user_id = user_id;
            this.name = name;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {

            String status = Common.Post_Json(url,chatroom_id,user_id,name,message);
            //第三个参数为String 所以此处return一个String类型的数据
            return status;
        }

        @Override
        protected void onPostExecute(String JsonString) {

            //@test
            //Log.v("fetch",JsonString);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(JsonString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(jsonObject != null) {
                try {
                    String status = jsonObject.getString("status");
                    Log.v("status",status);
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
        }
    }
}
