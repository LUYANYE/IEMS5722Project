package hk.edu.cuhk.ie.iems5722.group21.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import hk.edu.cuhk.ie.iems5722.group21.R;
import hk.edu.cuhk.ie.iems5722.group21.entity.Message;

public class MessageAdapter extends ArrayAdapter<Message> {

    private int resourceId;

    public MessageAdapter(Context context, int textViewResourceId, List<Message> Msglst) {
        super(context, textViewResourceId, Msglst);
        resourceId = textViewResourceId;
    }
    static class ViewHolder  {
        RelativeLayout leftLayout;
        RelativeLayout rightLayout;
        TextView leftMsg;
        TextView rightMsg;
        TextView leftimestamp;
        TextView rightimestamp;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();

            viewHolder.leftLayout = (RelativeLayout) view.findViewById(R.id.left_msg);
            viewHolder.rightLayout = (RelativeLayout) view.findViewById(R.id.right_msg);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.l_message);
            viewHolder.leftimestamp=(TextView) view.findViewById(R.id.l_timestamp);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.r_message);
            viewHolder.rightimestamp=(TextView) view.findViewById(R.id.r_timestamp);
            view.setTag(viewHolder); // 将ViewHolder对象存储到View中
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag(); // 把ViewHolder对象从View中重新取出
        }
        if (msg.getType() == Message.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，隐藏右边的消息布局
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getText());
            viewHolder.leftimestamp.setText("From " + msg.getName() + " at " + msg.getTimestamp());
        }
        if (msg.getType() == Message.TYPE_SENT) {
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getText());
            viewHolder.rightimestamp.setText( "From " + msg.getName() + " at " + msg.getTimestamp());
        }
        return view;
    }

}
