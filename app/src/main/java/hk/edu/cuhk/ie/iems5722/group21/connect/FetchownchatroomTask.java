package hk.edu.cuhk.ie.iems5722.group21.connect;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import hk.edu.cuhk.ie.iems5722.group21.activity.DelroomActivity;
import hk.edu.cuhk.ie.iems5722.group21.entity.Chatroom;

// use DelroomActivity.DelroomAdapter
public class FetchownchatroomTask  extends AsyncTask<Void,Integer,String> {

    private Context context;
    private String url;
    private ArrayList<Chatroom> ChatroomList;
    private DelroomActivity.DelroomAdapter adapter;

    public FetchownchatroomTask(Context context , String url , ArrayList<Chatroom> ChatroomList, DelroomActivity.DelroomAdapter adapter){
        this.context = context;
        this.url = url;
        this.ChatroomList = ChatroomList;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected String doInBackground(Void... params) {

        String result = Common.Get_getJson(url);
        //第三个参数为String 所以此处return一个String类型的数据
        return result;
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
            String status = "";
            try {
                status = jsonObject.getString("status");
                if (status.equals("OK")) {
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                        if (jsonObjectData != null) {
                            int id = jsonObjectData.optInt("id");
                            String name = jsonObjectData.optString("name");
                            ChatroomList.add(new Chatroom(name, id));
                        }
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();

            //Log.v("ChatroomList",ChatroomList.toString());
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
    }
}
