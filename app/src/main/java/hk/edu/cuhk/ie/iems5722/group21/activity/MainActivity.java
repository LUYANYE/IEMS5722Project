package hk.edu.cuhk.ie.iems5722.group21.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.adapter.ChatroomAdapter;
import hk.edu.cuhk.ie.iems5722.group21.connect.FetchChatroomnameTask;
import hk.edu.cuhk.ie.iems5722.group21.entity.Chatroom;
import hk.edu.cuhk.ie.iems5722.group21.entity.User;


public class MainActivity extends AppCompatActivity {

    private ArrayList<Chatroom> ChatroomList = new ArrayList<>();
    private ChatroomAdapter adapter;
    private String url = "http://18.188.52.141/api/a3/get_chatrooms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btn_add = findViewById(R.id.left_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = MainActivity.this;
                Intent intent = new Intent(context, AddroomActivity.class);
                //context.startActivity(intent);
                startActivityForResult(intent,1);
            }
        });

        Bundle b = getIntent().getExtras();
        String username;
        String userid;
        if(b == null) {
            username = User.current_user.getUsername();
            userid = String.valueOf(User.current_user.getUser_id());
        }
        else {
             username = b.getString("name");
             userid = b.getString("user_id");
        }

        User.current_user = new User(username,Integer.valueOf(userid));
        StringBuilder stringBuilder = new StringBuilder();

        //判断当前时间
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH");
        String str = df.format(date);
        int a = Integer.parseInt(str);
        if (a >= 4 && a <= 11) {
            stringBuilder.append("Good Morning, ");
        }
        if (a > 11 && a <= 13) {
            stringBuilder.append("Good Noon, ");
        }
        if (a > 13 && a <= 18) {
            stringBuilder.append("Good Afternoon, ");
        }
        if (a > 18 && a <= 24 || a > 0 && a < 4) {
            stringBuilder.append("Good Evening, ");
        }

        stringBuilder.append(User.current_user.getUsername());
        String title = stringBuilder.toString();
        toolbar.setTitle(title);

        //final MessageAdapter adapter = new MessageAdapter(this, MessageList);
        adapter = new ChatroomAdapter(MainActivity.this, R.layout.chatrooms, ChatroomList);
        // Attach the adapter to a ListView
        final ListView listView = (ListView) findViewById(R.id.chatroom_list);
        listView.setAdapter(adapter);

        String init_url = url + "?" + "user=" + User.current_user.getUser_id();
        FetchChatroomnameTask fetchChatroomnameTask =
                new FetchChatroomnameTask(this,init_url,ChatroomList,adapter);
        fetchChatroomnameTask.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("Enter","onActivity");
      if(resultCode == RESULT_OK)
        {
            Log.v("Enter","onResult");
            ChatroomList.clear();
            String init_url = url + "?" + "user=" + User.current_user.getUser_id();
            FetchChatroomnameTask fetchChatroomnameTask =
                    new FetchChatroomnameTask(this,init_url,ChatroomList,adapter);
            fetchChatroomnameTask.execute();
        }
    }

}
