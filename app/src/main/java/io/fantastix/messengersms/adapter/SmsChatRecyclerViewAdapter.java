package io.fantastix.messengersms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.Utils;
import io.fantastix.messengersms.model.Message;

public class SmsChatRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_COUNT = 2;    // msg sent by me, or all incoming msgs
    //    private final SimpleDateFormat mFormatter;
    private LayoutInflater mInflater;
    private final List<Message> messageList;
    private final Context context;

    // in this adapter constructor we add the list of messages as a parameter so that
    // we will passe it when making an instance of the adapter object in our activity
    public SmsChatRecyclerViewAdapter(Context context, List<Message> messagesList) {
        this.context = context;
        this.messageList = messagesList;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageViewHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageViewHolder) holder).bind(message);
                break;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return messageList != null ? messageList.size() : 0;
    }

//    @Override
//    public int getViewTypeCount() {
//        return VIEW_TYPE_COUNT;
//    }

    @Override
    public int getItemViewType(int position) {
        Message item = messageList.get(position);
        String tempNo = item.getAddress().replace("+675", "");

        if ( item.getType() == 2 ){
            return VIEW_TYPE_MESSAGE_SENT;
        }
        return VIEW_TYPE_MESSAGE_RECEIVED;
    }

    private class SentMessageViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        public final View mView;
        //        public final TextView mIdView;
        public final TextView mMessageView;
        public final TextView mTimeView;
        //        public final TextView mNicknameView;
        //        public final CardView cv;
        public Message mItem;
        private Context context;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mView = itemView;
//            mIdView = (TextView) itemView.findViewById(R.id.message_id);
            mMessageView = (TextView) itemView.findViewById(R.id.msg);
//            mNicknameView = (TextView) itemView.findViewById(R.id.nickname);
            mTimeView = (TextView) itemView.findViewById(R.id.time);
//            cv = (CardView) itemView.findViewById(R.id.cv);

//            Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//            result.startAnimation(animation);
//            lastPosition = position;

//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View v) {
//            if (listener != null) {
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onItemClick(itemView, position);
//                }
//            }
//        }

        @Override
        public String toString() {
            return super.toString() + " '" + mMessageView.getText() + "'";
        }

        public void bind(Message message) {
            mMessageView.setText(message.getMessage().trim());
            mTimeView.setText(Utils.pretifier(message.getTime()).trim());
//            mTimeView.setText(Utils.convertToTime(message.getTime()));
        }
    }

    private class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.msg);
            timeText = (TextView) itemView.findViewById(R.id.time);
//            nameText = (TextView) itemView.findViewById(R.id.nickname);
            profileImage = (ImageView) itemView.findViewById(R.id.photo);
        }

        void bind(Message message) {
            messageText.setText(message.getMessage().trim());
            // Format the stored timestamp into a readable String using method.
            timeText.setText(Utils.pretifier(message.getTime()));
//            nameText.setText(message.getSender().getNickname());

            // Insert the profile image from the URL into the ImageView.
//            Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
            profileImage.setImageResource(R.drawable.bg_doodle);
        }
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View view = convertView;  // old view to re-use if possible. Useful for Heterogeneous list with diff item view type.
//        Message item = messageList.get(position);
//        boolean mymsg = false;
//
//        if ( getItemViewType(position) == VIEW_TYPE_MYMSG ) {
//            if( view == null ) {
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
//        if ( mymsg ) {
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
