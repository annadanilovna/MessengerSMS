package io.fantastix.messengersms;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ShortCodesListDialogFragment extends BottomSheetDialogFragment {

    private static final String ARG_ITEM_COUNT = "item_count";
    private OnItemClickListener listener;

    String[] digicelShortCodes = {
            "*10#",
            "*11#",
            "*24#",
            "*32#",
            "*43#",
            "*60#",
            "*64#",
            "*70#",
            "*72#",
            "*99#"
//            "*10# - Thank you",
//            "*11# - One by one",
//            "*24# - ID 24",
//            "*32# - Trick u",
//            "*43# - I love u",
//            "*64# - Too late to apologize",
//            "*70# - Coming",
//            "*72# - Same to u",
//            "*99# - Night night",
//            "*60# - Hurry up"
    };
    private View mBottomSheet;

    public static ShortCodesListDialogFragment newInstance() {
        return new ShortCodesListDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_item_list_dialog_list_dialog, container, false);
        mBottomSheet = v.findViewById(R.id.bottom_sheet);

//        initialize();

        return v;
    }

    private void initialize() {
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // Called every time when the bottom sheet changes its state.
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (isAdded()) {
                    transitionBottomSheetBackgroundColor(slideOffset);
                }
            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void transitionBottomSheetBackgroundColor(float slideOffset) {
        int colorFrom = getResources().getColor(R.color.secondaryColor);
        int colorTo = getResources().getColor(R.color.colorAccent);
        mBottomSheet.setBackgroundColor(interpolateColor(slideOffset,
                colorFrom, colorTo));
    }

    /**
     * This function returns the calculated in-between value for a color
     * given integers that represent the start and end values in the four
     * bytes of the 32-bit int. Each channel is separately linearly interpolated
     * and the resulting calculated values are recombined into the return value.
     *
     * @param fraction The fraction from the starting to the ending values
     * @param startValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @param endValue A 32-bit int value representing colors in the
     * separate bytes of the parameter
     * @return A value that is calculated to be the linearly interpolated
     * result, derived by separating the start and end values into separate
     * color channels and interpolating each one separately, recombining the
     * resulting values in the same way.
     */
    private int interpolateColor(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;
        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;
        return ((startA + (int) (fraction * (endA - startA))) << 24) |
                ((startR + (int) (fraction * (endR - startR))) << 16) |
                ((startG + (int) (fraction * (endG - startG))) << 8) |
                ((startB + (int) (fraction * (endB - startB))));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list);
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

        private ItemAdapter(String[] items) {
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