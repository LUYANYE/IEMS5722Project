package hk.edu.cuhk.ie.iems5722.group21.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.adapter.ChatroomAdapter;
import hk.edu.cuhk.ie.iems5722.group21.connect.Common;
import hk.edu.cuhk.ie.iems5722.group21.connect.FetchUsernameTask;
import hk.edu.cuhk.ie.iems5722.group21.connect.FetchownchatroomTask;
import hk.edu.cuhk.ie.iems5722.group21.entity.Chatroom;
import hk.edu.cuhk.ie.iems5722.group21.entity.User;


// delete chat room
public class DelroomActivity extends AppCompatActivity {

    private ArrayList<Chatroom> ChatroomList = new ArrayList<>();
    private DelroomAdapter adapter;
    ListView listView;
    private String url = "http://18.188.52.141/api/a3/get_ownchatrooms";
    private String url2= "http://18.188.52.141/api/a3/del_ownchatrooms";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delroom);
        Toolbar toolbar = findViewById(R.id.del_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        adapter = new DelroomAdapter(DelroomActivity.this, R.layout.chatrooms, ChatroomList);
        // Attach the adapter to a ListView
        listView =  findViewById(R.id.delroom_list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
            }
        });
        String init_url = url + "?" + "owner=" + User.current_user.getUsername();
        FetchownchatroomTask fetchownchatroomTask =
                new FetchownchatroomTask(this,init_url ,ChatroomList,adapter);
        fetchownchatroomTask.execute();

        Button btn_del = findViewById(R.id.del_confirm);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder roomlist = new StringBuilder();
                SparseBooleanArray booleanArray = listView.getCheckedItemPositions();
                for (int j = 0; j < booleanArray.size(); j++) {
                    int key = booleanArray.keyAt(j);
                    //放入SparseBooleanArray，未必选中
                    if (booleanArray.get(key)) {
                        //这样mAdapter.getItem(key)就是选中的项
                        //select_userid.add(UserList.get(key).getUser_id());
                        roomlist.append(ChatroomList.get(key).getId());
                        roomlist.append(",");
                    } else {
                        //这里是用户刚开始选中，后取消选中的项
                    }
                }
                String rl = roomlist.toString();
                if(rl.equals("")) {
                    Toast t = Toast.makeText(DelroomActivity.this,
                            "No item selected！", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                String roomlst = rl.substring(0,rl.length()-1);
                DelroomTask delroomTask =
                        new DelroomTask(DelroomActivity.this, url2,roomlst);
                delroomTask.execute();
                setResult(RESULT_OK);
                finish();
                Log.d("selected_roomid", roomlst);
            }
        });

    }

    //Adapter
    public class DelroomAdapter extends ArrayAdapter<Chatroom> {

        private int resourceId;

        public DelroomAdapter(Context context, int textViewResourceId, List<Chatroom> Chatroomlst) {
            super(context, textViewResourceId, Chatroomlst);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Chatroom chatroom = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            }
            TextView user_name =  convertView.findViewById(R.id.chatroom_name);
            // Populate the data into the template view using the data object
            user_name.setText(chatroom.getRoom_name());
            // Return the completed view to render on screen

            if(listView.isItemChecked(position)) {
                convertView.setBackgroundColor(Color.parseColor("#4D89A9"));
            } else {
                convertView.setBackgroundColor(Color.TRANSPARENT);
            }

            return convertView;
        }
    }

    //httpTask
    class DelroomTask extends AsyncTask<Void,Integer,String> {

        private Context context;
        private String url;
        private String roomlist;

        public DelroomTask(Context context ,String url, String roomlist){
            this.context = context;
            this.url = url;
            this.roomlist = roomlist;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("rooms=");
            stringBuilder.append(roomlist);
            String api = stringBuilder.toString();
            Log.v("api",api);
            String status = Common.Post_Gen(url,api);
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
                    if(status.equals("OK")){
                        Toast t = Toast.makeText(DelroomActivity.this,"Delete successful", Toast.LENGTH_LONG);
                        t.show();
                    }
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
