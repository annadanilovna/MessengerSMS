package io.fantastix.messengersms.viewholder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.Utils;
import io.fantastix.messengersms.adapter.SmsRecyclerViewAdapter;
import io.fantastix.messengersms.model.Message;

public class TextViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    //        public final TextView mIdView;
    public final TextView mMessageView;
    public final TextView mTimeView;
    //        public final TextView mNicknameView;
    //        public final CardView cv;
    public Message mItem;
    private Context context;

    public TextViewHolder(@NonNull View itemView) {
        super(itemView);
        context = itemView.getContext();
        mView = itemView;
//            mIdView = (TextView) mView.findViewById(R.id.message_id);
        mMessageView = (TextView) mView.findViewById(R.id.msg);
//        mNicknameView = (TextView) mView.findViewById(R.id.nickname);
        mTimeView = (TextView) mView.findViewById(R.id.time);
//            cv = (CardView) mView.findViewById(R.id.cv);

//        itemView.setOnClickListener(this);
    }

    public void bind(final Message message, int position, final SmsRecyclerViewAdapter.SmsAdapterListener listener) {
//        textViewName.setText(message.getContact().getName() != null ? message.getContact().getName() : message.getContact().getNumber());
        mMessageView.setText(message.getSnippet());
        mTimeView.setText(Utils.getDate(message.getTime()));
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                listener.onItemClick(message);
            }
        });
    }

}
