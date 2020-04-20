package hk.edu.cuhk.ie.iems5722.group21.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.activity.ChatroomActivity;
import hk.edu.cuhk.ie.iems5722.group21.entity.Chatroom;

public class ChatroomAdapter extends ArrayAdapter<Chatroom> {

    private int resourceId;

    public ChatroomAdapter(Context context, int textViewResourceId, List<Chatroom> Msglst) {
        super(context, textViewResourceId, Msglst);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Chatroom chatroom = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resourceId, null);
        }
        TextView chatroom_name = (TextView) convertView.findViewById(R.id.chatroom_name);
        chatroom_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = getContext();
                Intent intent_chatroom = new Intent(context, ChatroomActivity.class);
                //把对应的chatroom数据传到下一个界面
                Bundle b = new Bundle();
                b.putInt("id", (Integer)chatroom.getId());
                b.putString("name",chatroom.getRoom_name());
                intent_chatroom.putExtras(b);
                context.startActivity(intent_chatroom);

            }
        });
        // Populate the data into the template view using the data object
        chatroom_name.setText(chatroom.getRoom_name());
        // Return the completed view to render on screen
        return convertView;
    }


}
