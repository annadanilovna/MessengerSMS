package io.fantastix.messengersms;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import io.fantastix.messengersms.adapter.SmsRecyclerViewAdapter;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private SmsRecyclerViewAdapter mAdapter;
    private Drawable icon;
    private ColorDrawable background;

    /**
     * Creates a Callback for the given drag and swipe allowance. These values serve as
     * defaults
     * and if you want to customize behavior per ViewHolder, you can override
     * {@link #getSwipeDirs(RecyclerView, ViewHolder)}
     * and / or {@link #getDragDirs(RecyclerView, ViewHolder)}.
     *
     * @param dragDirs  Binary OR of direction flags in which the Views can be dragged. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     * @param swipeDirs Binary OR of direction flags in which the Views can be swiped. Must be
     *                  composed of {@link #LEFT}, {@link #RIGHT}, {@link #START}, {@link
     *                  #END},
     *                  {@link #UP} and {@link #DOWN}.
     */
//    public SwipeToDeleteCallback(int dragDirs, int swipeDirs) {
//        super(dragDirs, swipeDirs);
//    }

    public SwipeToDeleteCallback(SmsRecyclerViewAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
//        icon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_delete2);
//        if (ItemTouchHelper.RIGHT) {
//        background = new ColorDrawable(Color.parseColor("#388E3C"));
//        }
//        else {
//            background = new ColorDrawable(Color.parseColor("#D32F2F"));
//        }
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();

        switch (direction) {
            case ItemTouchHelper.LEFT:
//                mAdapter.deleteItem(position);
                Toast.makeText(mAdapter.getContext(), "Swiping left is working", Toast.LENGTH_SHORT).show();
                break;
            case ItemTouchHelper.RIGHT:
//                mAdapter.callContact(position);
                Toast.makeText(mAdapter.getContext(), "Swiping right is working", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//        View itemView = viewHolder.itemView;
//        int backgroundCornerOffset = 20;

//        int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//        int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//        int iconBottom = iconTop + icon.getIntrinsicHeight();

//        if (dX > 0) { // Swiping to the right
//            background = new ColorDrawable(Color.parseColor("#388E3C"));
//            icon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_edit);
//
//            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//            int iconBottom = iconTop + icon.getIntrinsicHeight();
//
////            int iconLeft = itemView.getLeft() + iconMargin;
////            int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
//            int iconLeft = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
//            int iconRight = itemView.getLeft() + iconMargin;
//            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//
//            background.setBounds(itemView.getLeft(), itemView.getTop(),
//                    itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
//            background.draw(c);
//            icon.draw(c);
//        }
//        else if (dX < 0) { // Swiping to the left
//            background = new ColorDrawable(Color.parseColor("#D32F2F"));
//            icon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_delete2);
//
//            int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//            int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
//            int iconBottom = iconTop + icon.getIntrinsicHeight();
//
//            int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
//            int iconRight = itemView.getRight() - iconMargin;
//            icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//
//            background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
//                    itemView.getTop(), itemView.getRight(), itemView.getBottom());
//            background.draw(c);
//            icon.draw(c);
//        }
//        else { // view is unSwiped
//            background = new ColorDrawable(Color.parseColor("#388E3C"));
//            icon = ContextCompat.getDrawable(mAdapter.getContext(), R.drawable.ic_edit);
//
//            icon.setBounds(0, 0, 0, 0);     // ADD THIS LINE
//            background.setBounds(0, 0, 0, 0);
//            background.draw(c);
//            icon.draw(c);
//        }
//        background.draw(c);
//        icon.draw(c);

        Bitmap icon;
        Paint p = new Paint();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

            View itemView = viewHolder.itemView;
            float height = (float) itemView.getBottom() - (float) itemView.getTop();
            float width = height / 3;

            if (dX > 0) {
                // swiping to the right
                p.setColor(Color.parseColor("#388E3C"));
                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(mAdapter.getContext().getResources(), R.drawable.ic_edit);
                RectF icon_dest = new RectF((float) itemView.getLeft() + width,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            } else {
                // swiping to the left
                p.setColor(Color.parseColor("#D32F2F"));
                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                c.drawRect(background, p);
                icon = BitmapFactory.decodeResource(mAdapter.getContext().getResources(), R.drawable.ic_delete2);
                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                c.drawBitmap(icon, null, icon_dest, p);
            }
        }
        background.draw(c);

    }
}
