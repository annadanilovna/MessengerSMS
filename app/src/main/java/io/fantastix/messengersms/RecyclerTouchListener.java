package io.fantastix.messengersms;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.fantastix.messengersms.adapter.SmsChatRecyclerViewAdapter;
import io.fantastix.messengersms.adapter.SmsRecyclerViewAdapter;

public class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
    SmsRecyclerViewAdapter.SmsAdapterListener listener;
    GestureDetector gestureDetector;

    public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final SmsRecyclerViewAdapter.SmsAdapterListener listener) {
        this.listener = listener;
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View child = recyclerView.findChildViewUnder(e.getX(),e.getY());
                if(child != null && listener != null){
                    listener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        View child = rv.findChildViewUnder(e.getX(), e.getY());
        if(child != null && listener != null && gestureDetector.onTouchEvent(e)){
            listener.onItemClick(child, rv.getChildAdapterPosition(child));
        }

        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}