package io.fantastix.messengersms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public abstract class SwipeToPerformActionCallback extends ItemTouchHelper.SimpleCallback {
    private Context mContext;
    //    private SmsRecyclerViewAdapter mAdapter;
    private Drawable callDrawable;
    private Drawable deleteDrawable;
    private ColorDrawable background;
    private Paint clearPaint;

    enum ButtonsState {
        GONE,
        LEFT_VISIBLE,
        RIGHT_VISIBLE
    }

    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private static final float buttonWidth = 300;

    private RecyclerView.ViewHolder currentItemViewHolder = null;

//    public SwipeToDeleteCallback(int dragDirs, int swipeDirs) {
//        super(dragDirs, swipeDirs);
//    }

    public SwipeToPerformActionCallback(/*SmsRecyclerViewAdapter adapter*/Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
//        mAdapter = adapter;
        mContext = context;
        clearPaint = new Paint();
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }


    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//        int position = viewHolder.getAdapterPosition();
//
//        switch (direction) {
//            case ItemTouchHelper.LEFT:
////                mAdapter.item(position);
//                Toast.makeText(mAdapter.getContext(), "Swiping left is working", Toast.LENGTH_SHORT).show();
//                break;
//            case ItemTouchHelper.RIGHT:
////                mAdapter.callContact(position);
//                Toast.makeText(mAdapter.getContext(), "Swiping right is working", Toast.LENGTH_SHORT).show();
//                break;
//        }
//
//        val position =
//        if (Math.abs(horizontalTouchPosition) < (viewHolder.itemView.width / 2)) 0 else 1
//        val dragDirection = if (direction == LEFT) RIGHT else LEFT
//        val fallback = if (position == 0) 1 else 0
//        val action = ActionHelper.handleAction(conversationActions, dragDirection, position,
//                fallback)
//        swipeListener.onActionPerformed(viewHolder.adapterPosition, action)
//    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;

//        int backgroundCornerOffset = 0;
//        int itemHeight = itemView.getBottom() - itemView.getTop();
////        float itemHeight = (float) itemView.getBottom() - (float) itemView.getTop();
//        float width = itemHeight / 3;
//        float itemCenter = ((itemView.getBottom() + itemView.getTop()) / 2f);
//        boolean isCancelled = dX == 0 && !isCurrentlyActive;
//
//        if (isCancelled) {
//            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
//            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            return;
//        }
//
////        Bitmap icon;
//
//        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//
//            if (dX > 0) { // Swiping to the right
//                background = new ColorDrawable(Color.parseColor("#388E3C"));
//                callDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_edit);
//                //                clearPaint.setColor(Color.parseColor("#388E3C"));
////                RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
////                c.drawRect(background, clearPaint);
////                icon = BitmapFactory.decodeResource(mAdapter.getContext().getResources(), R.drawable.ic_edit);
////                RectF icon_dest = new RectF((float) itemView.getLeft() + width,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
////                c.drawBitmap(icon, null, icon_dest, clearPaint);
//
//
////                int itemHeight = itemView.getHeight();
//
//                int intrinsicHeight = callDrawable.getIntrinsicHeight();
//                int intrinsicWidth = callDrawable.getIntrinsicWidth();
//
//                int iconMargin = (itemView.getHeight() - intrinsicHeight) / 2;
////                int iconMargin = (itemHeight - intrinsicHeight) / 2;
//                int iconTop = itemView.getTop() + (itemView.getHeight() - intrinsicHeight) / 2;
////                int iconTop = (itemView.getTop() + (itemHeight - intrinsicHeight)) / 2;
//                  int iconBottom = iconTop + intrinsicHeight;
////                int iconLeft = itemView.getLeft() + iconMargin;
//                int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
//                int iconRight = itemView.getLeft() + iconMargin + intrinsicWidth;
////                int iconRight = itemView.getRight() - iconMargin;
////                int iconLeft = itemView.getLeft() + iconMargin + callDrawable.getIntrinsicWidth();
////                int iconRight = itemView.getLeft() + iconMargin;
//
////                callDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//                callDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//                callDrawable.draw(c);
//
//                background.setBounds(itemView.getLeft(), itemView.getTop(),
//                        itemView.getLeft() + ((int) dX) + backgroundCornerOffset, itemView.getBottom());
//                background.draw(c);
//            } else if (dX < 0) { // Swiping to the left
//                clearPaint.setColor(Color.parseColor("#D32F2F"));
////                RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
////                c.drawRect(background, clearPaint);
////                icon = BitmapFactory.decodeResource(mAdapter.getContext().getResources(), R.drawable.ic_delete2);
////                RectF icon_dest = new RectF((float) itemView.getRight() - 2*width,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
////                c.drawBitmap(icon, null, icon_dest, clearPaint);
//
////                int itemHeight = itemView.getHeight();
//
//                background = new ColorDrawable(Color.parseColor("#D32F2F"));
//                deleteDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_delete2);
//                background.setColor(Color.parseColor("#b80f0a"));
//
////                int iconMargin = (itemView.getHeight() - deleteDrawable.getIntrinsicHeight()) / 2;
////                int iconTop = itemView.getTop() + (itemView.getHeight() - deleteDrawable.getIntrinsicHeight()) / 2;
////                int iconBottom = iconTop + deleteDrawable.getIntrinsicHeight();
////
////                int iconLeft = itemView.getRight() - iconMargin - deleteDrawable.getIntrinsicWidth();
////                int iconRight = itemView.getRight() - iconMargin;
////                deleteDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//                int intrinsicHeight = deleteDrawable.getIntrinsicHeight();
//                int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
//
//                int iconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
//                int iconMargin = (itemHeight - intrinsicHeight) / 2;
//                int iconLeft = itemView.getRight() - iconMargin - intrinsicWidth;
//                int iconRight = itemView.getRight() - iconMargin;
//                int iconBottom = iconTop + intrinsicHeight;
//
//                deleteDrawable.setBounds(iconLeft, iconTop, iconRight, iconBottom);
//
////                background.setBounds(itemView.getRight() + ((int) dX) - backgroundCornerOffset,
////                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
//
//                background.setBounds(itemView.getRight() + (int) dX,
//                        itemView.getTop(), itemView.getRight(), itemView.getBottom());
//                background.draw(c);
//
//                deleteDrawable.draw(c);
//            } else { // view is unSwiped
//                background = new ColorDrawable(Color.parseColor("#388E3C"));
//                callDrawable = ContextCompat.getDrawable(mContext, R.drawable.ic_edit);
//                callDrawable.setBounds(0, 0, 0, 0);     // ADD THIS LINE
//                background.setBounds(0, 0, 0, 0);
//                background.draw(c);
//                callDrawable.draw(c);
//            }
//
//        }
//        background.draw(c);

//        c.save();
//
//        if (dX > 0) {
//            c.clipRect(itemView.getLeft(), itemView.getTop(), (int) dX, itemView.getBottom());
//        } else {
//            c.clipRect(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
//        }
//
//        c.restore();
        drawButtons(c, viewHolder);
        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;

//        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void clearCanvas(Canvas c, Float top, Float right, Float left, Float bottom) {
        c.drawRect(left, top, right, bottom, clearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY, int actionState, boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;

                if (swipeBack) {
                    if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                    else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE;

                    if (buttonShowedState != ButtonsState.GONE) {
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c,
                                      final RecyclerView recyclerView,
                                      final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY,
                                      final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c,
                                    final RecyclerView recyclerView,
                                    final RecyclerView.ViewHolder viewHolder,
                                    final float dX, final float dY,
                                    final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeToPerformActionCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;
                    buttonShowedState = ButtonsState.GONE;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean isClickable) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(isClickable);
        }
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {
        float buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF leftButton = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + buttonWidthWithoutPadding, itemView.getBottom());
        p.setColor(Color.BLUE);
        c.drawRoundRect(leftButton, corners, corners, p);
        drawText("Call", c, leftButton, p);

        RectF rightButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("Delete", c, rightButton, p);

        RectF buttonInstance = null;
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        }
        else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 60;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }
}
