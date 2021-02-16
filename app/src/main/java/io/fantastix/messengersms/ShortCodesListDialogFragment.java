package io.fantastix.messengersms;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ShortCodesListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_ITEM_COUNT = "item_count";
    private OnItemClickListener listener;

    public String[] digicelShortCodes = {
            "*99#",
            "*60#",
            "*24#",
            "*11#",
            "*64#",
            "*34#"
    };

    // TODO: Customize parameters
    public static ShortCodesListDialogFragment newInstance() {
        return new ShortCodesListDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ItemAdapter adapter = new ItemAdapter(digicelShortCodes);
        recyclerView.setAdapter(adapter);
    }

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final TextView text;

        ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog_item, parent, false));
            text = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(this::onClick);
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

    private class ItemAdapter extends RecyclerView.Adapter<ViewHolder> {

        private final String[] mItems;

        ItemAdapter(String[] items) {
            mItems = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.text.setText("Send ".concat(mItems[position]));
        }

        @Override
        public int getItemCount() {
            return mItems.length;
        }

    }

}