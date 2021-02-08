package io.fantastix.messengersms.chats;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import android.util.Log;
import android.util.Pair;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.fantastix.messengersms.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {
    public static final int VIEW_TYPE_MYMSG = 0;
    public static final int VIEW_TYPE_INMSG = 1;
    public static final int VIEW_TYPE_COUNT = 2;    // msg sent by me, or all incoming msgs
//    private final SimpleDateFormat mFormatter;
    private LayoutInflater mInflater;
    private final List<Message> messageList;

    // in this adapter constructor we add the list of messages as a parameter so that
    // we will passe it when making an instance of the adapter object in our activity
    public MessageAdapter(List<Message> messagesList) {
//        mFormatter = new SimpleDateFormat("HH:mm");
        this.messageList = messagesList;
//        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //        public final TextView mIdView;
        public final TextView mMessageView;
        public final TextView mTimeView;
//        public final TextView mNicknameView;
        //        public final CardView cv;
        public Message mItem;
        private Context context;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mView = itemView;
//            mIdView = (TextView) itemView.findViewById(R.id.message_id);
            mMessageView = (TextView) itemView.findViewById(R.id.msg);
//            mNicknameView = (TextView) itemView.findViewById(R.id.nickname);
            mTimeView = (TextView) itemView.findViewById(R.id.time);
//            cv = (CardView) itemView.findViewById(R.id.cv);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(itemView, position);
                }
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }
    }

    @NonNull
    @Override
    public MessageAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        /*
        now with everything setup we can implement the socket client
        in our ChatBoxActivity.java so this is how we are going to
        proceed :
        1.Get the nickname of the user from the intent extra
        2.call and implement all the methods relative to recycler view including the adapter instantiation
        */
        View itemView;
//        int v = 0;
//        if (v == 1) {
//            v = 0;
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_sms_chat_room_item_in, parent, false);
//        }
//        else {
//            v = 1;
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.activity_sms_chat_room_item_out, parent, false);
//        }
        return new MessageAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageAdapter.MyViewHolder holder, int position) {
        // binding the data from our ArrayList of object to the item.xml using the view
        Message m = messageList.get(position);
        holder.mItem = messageList.get(position);
//          holder.mIdView.setText(m.getId());
        holder.mTimeView.setText(m.getTime());
//        holder.mNicknameView.setText(m.getAddress());
//        holder.mNicknameView.setText(m.getNickname());
        holder.mMessageView.setText(m.getMessage());
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
//
//    @Override
//    public int getViewTypeCount() {
//        return VIEW_TYPE_COUNT;
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        Message item = messageList.get(position);
//        if ( item.getAddress().equals( "70523228" )){
//            return VIEW_TYPE_MYMSG;
//        }
//        return VIEW_TYPE_INMSG;
//    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;  // old view to re-use if possible. Useful for Heterogeneous list with diff item view type.
//        Message item = messageList.get(position);
//        boolean mymsg = false;
//
//        if ( getItemViewType(position) == VIEW_TYPE_MYMSG){
//            if( view == null ){
//                view = mInflater.inflate(R.layout.activity_sms_chat_room_item_out, null);  // inflate chat row as list view row.
//            }
//            mymsg = true;
//            // view.setBackgroundResource(R.color.my_msg_background);
//        } else {
//            if( view == null ){
//                view = mInflater.inflate(R.layout.activity_sms_chat_room_item_in, null);  // inflate chat row as list view row.
//            }
//            // view.setBackgroundResource(R.color.in_msg_background);
//        }
//
//        TextView sender = (TextView)view.findViewById(R.id.sender);
//        sender.setText(item.getAddress());
//
//        TextView msgRow = (TextView)view.findViewById(R.id.msg_row);
//        msgRow.setText(item.getMessage());
//        if( mymsg ){
//            msgRow.setBackgroundResource(R.color.my_msg_background);
//        }else{
//            msgRow.setBackgroundResource(R.color.in_msg_background);
//        }
//
//        TextView time = (TextView)view.findViewById(R.id.time);
//        time.setText(item.getTime());
//
//        Log.d(TAG, "getView : " + item.getAddress() + " " + item.getMessage() + " " + item.getTime());
//        return view;
//    }
}

//public class MessageAdapter extends BaseAdapter {
//    public static final int DIRECTION_INCOMING = 0;
//    public static final int DIRECTION_OUTGOING = 1;
//    //    private List<Pair<Message, Integer>> mMessages = null;
//    private List<Message> mMessages = null;
//    private SimpleDateFormat mFormatter = null;
//    private LayoutInflater mInflater = null;
//
//    public MessageAdapter(Activity activity, List<Message> messageList) {
////        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        mInflater = activity.getLayoutInflater();
////        mMessages = new ArrayList<Pair<Message, Integer>>();
//        mMessages = messageList;
//        mFormatter = new SimpleDateFormat("HH:mm");
//    }
//
//    public void addMessage(Message message, int direction) {
////        mMessages.add(new Pair(message, direction));
//        mMessages.add(message);
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return mMessages.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return mMessages.get(i);
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }
//
////    @Override
////    public int getItemViewType(int i) {
////        return mMessages.get(i).second;
////    }
//
//    @Override
//    public View getView(int i, View convertView, ViewGroup viewGroup) {
//        int direction = getItemViewType(i);
//
//        if (convertView == null) {
//            int res = 0;
//            if (direction == DIRECTION_INCOMING) {
//                res = R.layout.activity_sms_chat_room_item_in;
//            } else if (direction == DIRECTION_OUTGOING) {
//                res = R.layout.activity_sms_chat_room_item_out;
//            }
//            convertView = mInflater.inflate(res, viewGroup, false);
//        }
//
////        Message message = mMessages.get(i).first;
//        Message message = mMessages.get(i);
//        String name = message.getAddress();
//
////        TextView txtSender = convertView.findViewById(R.id.sender);
//        TextView txtMessage = convertView.findViewById(R.id.msg);
//        TextView txtDate = convertView.findViewById(R.id.time);
//
////        txtSender.setText(name);
//        txtMessage.setText(message.getMessage());
//        txtDate.setText("");//mFormatter.format(message.getTime()));
//
//        return convertView;
//    }
//}
