package io.fantastix.messengersms.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import io.fantastix.messengersms.R;
import io.fantastix.messengersms.Utils;
import io.fantastix.messengersms.model.Contact;
import io.fantastix.messengersms.model.Message;
import io.fantastix.messengersms.model.Sms;

public class SmsRecyclerViewAdapter extends RecyclerView.Adapter<SmsRecyclerViewAdapter.SmsViewHolder> implements Filterable {
    private final List<Sms> mSmsList;
    private List<Sms> mSmsListFiltered;
    private final Activity mActivity;
    private Sms mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private SmsAdapterListener listener;
    private Context context;

    public SmsRecyclerViewAdapter(Activity activity, List<Sms> smsList) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.mSmsList = smsList;
        this.mSmsListFiltered = smsList;
    }

    public SmsRecyclerViewAdapter(Activity activity, List<Sms> smsList, SmsAdapterListener listener) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.mSmsList = smsList;
        this.mSmsListFiltered = smsList;
        this.listener = listener;
    }

    public class SmsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final View mView;
        //        public final TextView mIdView;
        public final TextView mNicknameView;
        public final TextView mMessageView;
        public final TextView mTimeView;
        public final ImageView mPhotoView;
        //        public final CardView cv;
        public Sms mItem;
        private Context context;

        public SmsViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            mView = itemView;
//            mIdView = (TextView) itemView.findViewById(R.id.message_id);
            mNicknameView = (TextView) itemView.findViewById(R.id.contact_name);
            mMessageView = (TextView) itemView.findViewById(R.id.message);
            mTimeView = (TextView) itemView.findViewById(R.id.time);
            mPhotoView = (ImageView) itemView.findViewById(R.id.photo);
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_sms_chats_item, parent, false);
        return new SmsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SmsViewHolder holder, int position) {
        Sms sms = mSmsListFiltered.get(position);
        holder.mItem = mSmsList.get(position);
//        holder.mIdView.setText(sms.getId());
        holder.mNicknameView.setText(sms.getContact().getName() != null ? sms.getContact().getName() : sms.getContact().getPhoneNumber());
        holder.mPhotoView.setImageURI(Uri.parse(sms.getContact().getPhotoUri()));
//        holder.mPhotoView.setText(sms.getContact().getName() != null ? sms.getContact().getName() : sms.getContact().getPhoneNumber());
//        sms.getContact().getName().charAt(0)
//        holder.mNicknameView.setText(sms.getReadState() == 0 ? "400" : "600");
        if (sms.getReadState() >= 1) {
            holder.mNicknameView.setTypeface(null, Typeface.NORMAL);
            holder.mMessageView.setTypeface(null, Typeface.NORMAL);
            holder.mMessageView.setAlpha(0.7f);
        } else {
            holder.mNicknameView.setTypeface(null, Typeface.BOLD);
            holder.mMessageView.setTypeface(null, Typeface.BOLD);
            holder.mMessageView.setAlpha(1f);
        }

        holder.mTimeView.setText(Utils.getDate(sms.getTime()));
        holder.mMessageView.setText(sms.getMsg());

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
                    List<Sms> filteredList = new ArrayList<>();
                    for (Sms row : mSmsList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getMsg().toLowerCase().contains(charString.toLowerCase()) || row.getContact().getPhoneNumber().contains(charSequence)) {
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
                mSmsListFiltered = (ArrayList<Sms>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public Context getContext() {
        return context;
    }

    public interface SmsAdapterListener {
        void onItemClick(View itemView, int position);
        void onSelectedSms(Sms sms);
        void onLongClick(View view, int position);
    }

//    public void setOnItemClickListener(SmsAdapterListener listener) {
//        this.listener = listener;
//    }

    public void addItem(Sms country) {
        mSmsList.add(country);
        notifyItemInserted(mSmsList.size());
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = mSmsList.get(position);
        mRecentlyDeletedItemPosition = position;
        mSmsList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mSmsList.size());
        showUndoSnackbar();
    }

    private void showUndoSnackbar() {
        View view = mActivity.findViewById(R.id.sms_roll);
        Snackbar snackbar = Snackbar.make(view, "Undo", Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        mSmsList.add(mRecentlyDeletedItemPosition, mRecentlyDeletedItem);
        notifyItemInserted(mRecentlyDeletedItemPosition);
    }
}
