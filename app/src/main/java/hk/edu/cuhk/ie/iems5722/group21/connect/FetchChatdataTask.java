package hk.edu.cuhk.ie.iems5722.group21.connect;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

import hk.edu.cuhk.ie.iems5722.group21.Interface.AsyncResponse;
import hk.edu.cuhk.ie.iems5722.group21.adapter.MessageAdapter;
import hk.edu.cuhk.ie.iems5722.group21.entity.Message;
import hk.edu.cuhk.ie.iems5722.group21.entity.User;

//取回聊天消息数据
public class FetchChatdataTask extends AsyncTask<Void,Integer,String> {

    private Context context;
    private String url;
    private LinkedList<Message> MessageList;
    private MessageAdapter adapter;
    public AsyncResponse asyncResponse;

    //接口回调
    public void setOnAsyncResponse(AsyncResponse asyncResponse)
    {
        this.asyncResponse = asyncResponse;
    }

    public FetchChatdataTask(Context context , String url , LinkedList<Message> MessageList,
                             MessageAdapter adapter){
        this.context = context;
        this.url = url;
        this.MessageList = MessageList;
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
            try {
                String status = jsonObject.getString("status");
                if (status.equals("OK")) {
                    JSONObject JSONdata = new JSONObject(jsonObject.getString("data"));
                    int current_page = JSONdata.optInt("current_page");
                    int total_pages = JSONdata.optInt("total_pages");
                    //接口回调
                    HashMap<String,Integer> data = new HashMap<>();
                    data.put("current_page",current_page);
                    data.put("total_pages",total_pages);
                    asyncResponse.onDataReceivedSuccess(data);
                    JSONArray jsonArray = new JSONArray(JSONdata.optString("messages"));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectData = jsonArray.getJSONObject(i);
                        if (jsonObjectData != null) {
                            int user_id = jsonObjectData.optInt("user_id");
                            String name = jsonObjectData.optString("name");
                            String message = jsonObjectData.getString("message");
                            String message_time = jsonObjectData.getString("message_time");
                            if(user_id != User.current_user.getUser_id()) {
                                MessageList.addFirst(new Message(message, message_time.substring(11,16),
                                        Message.TYPE_RECEIVED, name, user_id));
                            }
                            else{
                                MessageList.addFirst(new Message(message, message_time.substring(11,16),
                                    Message.TYPE_SENT, name, user_id));
                            }
                        }
                    }
                }
            } catch(JSONException e){
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            //Log.v("MessageList",MessageList.toString());
        }
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
    }
}
