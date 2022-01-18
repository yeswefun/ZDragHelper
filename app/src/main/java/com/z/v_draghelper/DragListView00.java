package com.z.v_draghelper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

public class DragListView00 extends FrameLayout {

    private ViewDragHelper mDragHelper;

    public DragListView00(@NonNull Context context) {
        this(context, null);
    }

    public DragListView00(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView00(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //mDragHelper = new ViewDragHelper();
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 将事件交由mDragHelper来处理
        mDragHelper.processTouchEvent(event);

        /*
            return super.onTouchEvent(event);
            必须返回true，否则不生效
         */
        return true;
    }

    private ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        /*
            指定child对应的子View是否可以拖动
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            /*
                true表示所有子View都可以拖动
             */
            return true;
        }

        /*
            返回垂直拖动移动的位置
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            return top;
        }

        /*
            返回水平拖动移动的位置
         */
        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            return left;
        }
    };
}
