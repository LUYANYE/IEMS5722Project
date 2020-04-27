package hk.edu.cuhk.ie.iems5722.group21.activity;

import androidx.appcompat.app.AppCompatActivity;

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

import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.connect.Common;
import hk.edu.cuhk.ie.iems5722.group21.connect.FetchUsernameTask;
import hk.edu.cuhk.ie.iems5722.group21.entity.User;


//add chatroom
public class AddroomActivity extends AppCompatActivity {

    private ArrayList<User> UserList = new ArrayList<>();
    private UserAdapter adapter;
    ListView listView;
    private String url = "http://18.188.52.141/api/a3/get_users";
    private String url2= "http://18.188.52.141/api/a3/add_room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroom);
        Toolbar toolbar = findViewById(R.id.add_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        adapter = new UserAdapter(AddroomActivity.this, R.layout.users, UserList);
        // Attach the adapter to a ListView
        listView =  findViewById(R.id.username_list);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                adapter.notifyDataSetChanged();
            }
        });

        // get user list
        FetchUsernameTask fetchUsernameTask =
                new FetchUsernameTask(this,url,UserList,adapter);
        fetchUsernameTask.execute();
        final EditText editText = findViewById(R.id.add_name);

        Button bt_confirm = findViewById(R.id.add_confirm);
        bt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //List<Integer> select_userid = new ArrayList<>();
                StringBuilder menberlist = new StringBuilder();
                SparseBooleanArray booleanArray = listView.getCheckedItemPositions();
                for (int j = 0; j < booleanArray.size(); j++) {
                    int key = booleanArray.keyAt(j);
                    //放入SparseBooleanArray，未必选中
                    if (booleanArray.get(key)) {
                        //这样mAdapter.getItem(key)就是选中的项
                        //select_userid.add(UserList.get(key).getUser_id());
                        menberlist.append(UserList.get(key).getUser_id());
                        menberlist.append(",");
                    } else {
                        //这里是用户刚开始选中，后取消选中的项
                    }
                }
                if(menberlist.toString().equals("")) {
                    Toast t = Toast.makeText(AddroomActivity.this,
                            "Contain at least one other member！", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                menberlist.append(User.current_user.getUser_id());
                String mlist = menberlist.toString();
                String ChatroomName = editText.getText()+"";
                if("".equals(ChatroomName)){
                    Toast t = Toast.makeText(AddroomActivity.this,
                            "empty ChatroomName！", Toast.LENGTH_LONG);
                    t.show();
                    return;
                }
                AddroomTask addroomTask =
                        new AddroomTask(AddroomActivity.this, url2, ChatroomName,
                                User.current_user.getUsername(), mlist);
                addroomTask.execute();
                Log.d("selected_userid", mlist);
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    //Adapter
    public class UserAdapter extends ArrayAdapter<User> {

        private int resourceId;

        public UserAdapter(Context context, int textViewResourceId, List<User> Userlst) {
            super(context, textViewResourceId, Userlst);
            resourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final User user = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
            }
            TextView user_name =  convertView.findViewById(R.id.user_name);
            // Populate the data into the template view using the data object
            user_name.setText(user.getUsername());
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
    class AddroomTask extends AsyncTask<Void,Integer,String> {

        private Context context;
        private String url;
        private String ChatroomName;
        private String owner;
        private String memberlist;

        public AddroomTask(Context context ,String url, String ChatroomName, String owner, String memberlist){
            this.context = context;
            this.url = url;
            this.ChatroomName = ChatroomName;
            this.owner = owner;
            this.memberlist = memberlist;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(Void... params) {

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("ChatroomName=");
            stringBuilder.append(ChatroomName);
            stringBuilder.append("&owner=");
            stringBuilder.append(owner);
            stringBuilder.append("&members=");
            stringBuilder.append(memberlist);
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
                        Toast t = Toast.makeText(AddroomActivity.this,"Add successful", Toast.LENGTH_LONG);
                        t.show();
                    }
                    else{
                        String reason = jsonObject.getString("message");
                        Log.v("reason", reason);
                        if(reason.equals("RoomExisted")){
                            Toast t = Toast.makeText(AddroomActivity.this,"This Room already exists", Toast.LENGTH_LONG);
                            t.show();
                        }
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
