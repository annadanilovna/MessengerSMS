package io.fantastix.messengersms.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.Utils;
import io.fantastix.messengersms.model.Message;

public class SmsRecyclerViewAdapter extends RecyclerView.Adapter<SmsRecyclerViewAdapter.SmsViewHolder> implements Filterable {
    private List<Message> mSmsList;
    private List<Message> mSmsListFiltered;
    private Message mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private SmsAdapterListener listener;
    private Context context;

    public SmsRecyclerViewAdapter(Context context) {
        this.context = context;
    };

    public SmsRecyclerViewAdapter(Context context, List<Message> smsList) {
        this.context = context;
        this.mSmsList = smsList;
        this.mSmsListFiltered = smsList;
    }

    public SmsRecyclerViewAdapter(Context context, List<Message> smsList, SmsAdapterListener listener) {
        this.context = context;
        this.mSmsList = smsList;
        this.mSmsListFiltered = smsList;
        this.listener = listener;
    }

    //    public class SmsViewHolder extends SwipeToAction.ViewHolder<Message> implements View.OnClickListener {
    public class SmsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //        public final TextView mIdView;
        public final TextView mNicknameView;
        public final TextView mMessageView;
        public final TextView mTimeView;
        public final TextView mAvatarView;
        public final ImageView mPhotoView;
        public final TextView mCountView;
        //        public final CardView cv;
        public Message mItem;
        private Context context;

        public SmsViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mView = itemView;
//            mIdView = (TextView) itemView.findViewById(R.id.message_id);
            mNicknameView = (TextView) itemView.findViewById(R.id.contact_name);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mTimeView = (TextView) itemView.findViewById(R.id.time);
            mPhotoView = (ImageView) itemView.findViewById(R.id.contact_image);
            mAvatarView = (TextView) itemView.findViewById(R.id.contact_avatar);
            mCountView = (TextView) itemView.findViewById(R.id.msg_count);
//            cv = (CardView) itemView.findViewById(R.id.cv);

            itemView.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onSelectedSms(mSmsListFiltered.get(position));
                    listener.onItemClick(itemView, position);
                }
            }
        }
    }

    @NonNull
    @Override
    public SmsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sms_item, parent, false);
        return new SmsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        Message sms = mSmsListFiltered.get(position);
        holder.mItem = mSmsList.get(position);
//        holder.mIdView.setText(sms.getId());
        holder.mNicknameView.setText(sms.getContact().getName() != null ? sms.getContact().getName() : sms.getContact().getPhoneNumber());

        if (!sms.getContact().getPhotoUri().isEmpty()) {
            holder.mPhotoView.setVisibility(ImageView.VISIBLE);
            holder.mPhotoView.setImageURI(Uri.parse(sms.getContact().getPhotoUri()));
        } else {
            holder.mAvatarView.setVisibility(TextView.VISIBLE);
//            holder.mAvatarView.setText(sms.getContact().getName() != null ? sms.getContact().getName().charAt(0) : sms.getContact().getPhoneNumber().charAt(0));
            holder.mAvatarView.setText(sms.getContact().getName().substring(0, 1));
//            StringBuilder builder = new StringBuilder(3);
//            for(int j = 0; j < getName.split(" ").length; j++) {
//                builder.append(getName.split(" ")[j].substring(0, 1) + ". ");
//            }
//
//            contactName.setText(builder.toString());
//            Random mRandom = new Random();
//            int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
//            ((GradientDrawable) holder.mAvatarView.getBackground()).setColor(color);
        }
//        holder.mNicknameView.setText(sms.getReadState() == 0 ? "400" : "600");
        if (sms.isRead()) {
//            holder.mNicknameView.setTextColor(context.getResources().getColor(R.style.Theme_MessengerSMS));//Color.argb(87, 12,12, 12));
//            holder.mNicknameView.setTypeface(null, Typeface.NORMAL);
//            holder.mMessageView.setTypeface(null, Typeface.NORMAL);
//            holder.mMessageView.setAlpha(0.7f);
//            holder.mCountView.setVisibility(View.VISIBLE);
//            holder.mCountView.setText("3");
        } else {
//            holder.mNicknameView.setTextColor(context.getResources().getColor(R.color.whiteHighEmphasis));
//            holder.mNicknameView.setTypeface(null, Typeface.BOLD);
//            holder.mMessageView.setTypeface(null, Typeface.BOLD);
//            holder.mMessageView.setAlpha(1f);
        }

        holder.mTimeView.setText(Utils.getDate(sms.getTime()));
        if (sms.getFolderName().equals("sent")) {
            holder.mMessageView.setText("You: " + sms.getMessage());
        }
        else if (sms.getFolderName().equals("draft")) {
            holder.mMessageView.setText("draft: " + sms.getMessage());
        }
        else {
            holder.mMessageView.setText(sms.getMessage());
        }

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                showMenu(position);
                Toast.makeText(context, "Long press", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mSmsList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mSmsListFiltered.addAll(mSmsList);
                } else {
                    List<Message> filteredList = new ArrayList<>();
                    for (Message row : mSmsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getMessage().toLowerCase().contains(charString.toLowerCase()) || row.getContact().getPhoneNumber().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    mSmsListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mSmsListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mSmsListFiltered = (ArrayList<Message>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Context getContext() {
        return context;
    }

    public interface SmsAdapterListener {
        void onItemClick(View itemView, int position);
        void onSelectedSms(Message sms);
        void onLongClick(View view, int position);
    }

//    public void setOnItemClickListener(SmsAdapterListener listener) {
//        this.listener = listener;
//    }

    public void addItem(Message country) {
        mSmsList.add(country);
        notifyItemInserted(mSmsList.size());
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = mSmsList.get(position);
        mRecentlyDeletedItemPosition = position;
        mSmsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSmsList.size());
//        showUndoSnackbar();
    }

    public void callContact(int position) {
        String address = mSmsList.get(position).getAddress();
//        Utils.makeCall(address);
    }

//    private void showUndoSnackbar() {
//        View view = context.findViewById(R.id.coordinator);
//        Snackbar snackbar = Snackbar.make(view, "Undo", Snackbar.LENGTH_LONG);
//        snackbar.setAction("Undo", v -> undoDelete());
//        snackbar.setActionTextColor(Color.YELLOW);
//        snackbar.show();
//    }

    private void undoDelete() {
        mSmsList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }

    public void restoreItem(Message message, int position) {
        mSmsList.add(position, message);
        notifyItemInserted(position);
    }

    public void setData(List<Message> newData) {
        this.mSmsList = newData;
        notifyDataSetChanged();
    }
}
