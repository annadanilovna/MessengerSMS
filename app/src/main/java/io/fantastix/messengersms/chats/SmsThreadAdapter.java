package io.fantastix.messengersms.chats;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.fantastix.messengersms.R;

public class SmsThreadAdapter extends RecyclerView.Adapter<SmsThreadAdapter.PersonViewHolder> {
    private final List<Sms> mSmsList;

    public SmsThreadAdapter(List<Sms> smsList) {
        mSmsList = smsList;
    }

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public class PersonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //        public final TextView mIdView;
        public final TextView mNicknameView;
        public final TextView mMessageView;
        public final TextView mTimeView;
        //        public final CardView cv;
        public Sms mItem;
        private Context context;

        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mView = itemView;
//            mIdView = (TextView) itemView.findViewById(R.id.message_id);
            mNicknameView = (TextView) itemView.findViewById(R.id.nickname);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mTimeView = (TextView) itemView.findViewById(R.id.date);
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
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sms_chats_item, parent, false);
        return new PersonViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Sms m = mSmsList.get(position);
        holder.mItem = mSmsList.get(position);
//        holder.mIdView.setText(m.getId());
        holder.mTimeView.setText(m.getTime());
        holder.mNicknameView.setText(m.getSender());
        holder.mMessageView.setText(m.getMsg());
    }

    @Override
    public int getItemCount() {
        return mSmsList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
