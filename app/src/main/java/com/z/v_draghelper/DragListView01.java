package com.z.v_draghelper;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

/*
效果分析
    1. 后面(第一个View, 注意层级关系)不能拖动, 即第二个View可以拖动

    2. 列表只能垂直拖动

    3. 垂直拖动的范围只能是后面菜单View的高度

    4. 手指松开的时候两者选其一，要么打开要么关闭

    5. 事件的分发和拦截
        先将TextView换成ListView
 */
public class DragListView01 extends FrameLayout {

    private static final String TAG = "DragListView01";

    /*
        拖动辅助类
     */
    private final ViewDragHelper mDragHelper;

    /*
        被拖动的列表
     */
    private View mDragListView;

    /*
        菜单的高度
     */
    private int mMenuHeight;

    /*
        菜单是否处于打开状态
     */
    private boolean mMenuOpened = false;


    public DragListView01(@NonNull Context context) {
        this(context, null);
    }

    public DragListView01(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragListView01(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //mDragHelper = new ViewDragHelper();
        mDragHelper = ViewDragHelper.create(this, mDragHelperCallback);
    }

    /*
        View的绘制流程
            onMesure
            onLayout
            onDraw

        onFinishInflate是在setContentView布局解析完成之后调用的,
        而View的测量是在onResume之后才开始的

        获取控件的宽高一定是要在测量(onMeasure)完毕之后才能获取
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // 获取取ListView
        int childCount = getChildCount();
        if (childCount != 2) {
            throw new RuntimeException("DragListView01只能包含两个直接子View");
        }
        mDragListView = getChildAt(1); // 注意层级关系，我们现在是FrameLayout

        // 拿不到高度呀
        mMenuHeight = getChildAt(0).getMeasuredHeight();
        Log.e(TAG, "onFinishInflate: " + mMenuHeight);
    }

    /*
        什么情况下onMeasure会调用多次?
            addView
            setVisibility
            ...
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // 可以拿到高度
        mMenuHeight = getChildAt(0).getMeasuredHeight();
        Log.e(TAG, "onMeasure: " + mMenuHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // 可以拿到高度
        if (changed && mMenuHeight == 0) {
            mMenuHeight = getChildAt(0).getMeasuredHeight();
            Log.e(TAG, "onLayout: " + mMenuHeight);
        }
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*
            将事件交由mDragHelper来处理

            注意: 此处必须是一个完整的事件
                ACTION_DOWN
                ACTION_MOVE
                ACTION_UP
         */
        mDragHelper.processTouchEvent(event);

        /*
            return super.onTouchEvent(event);
            必须返回true，否则不生效
         */
        return true;
    }

    private final ViewDragHelper.Callback mDragHelperCallback = new ViewDragHelper.Callback() {
        /*
            指定child对应的子View是否可以拖动
         */
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            /*
                1. 后面(第一个View, 注意层级关系)不能拖动, 即第二个View可以拖动
             */
            return mDragListView == child;
        }

        /*
            返回垂直拖动移动的位置
         */
        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {

            // 3. 垂直拖动的范围只能是后面菜单View的高度
            if (top < 0) {
                top = 0;
            } else if (top > mMenuHeight) {
                top = mMenuHeight;
            }

            // 2. 列表只能垂直拖动
            return top;
        }

        /*
            返回水平拖动移动的位置
         */
//        @Override
//        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
//            return left;
//        }


        /*
            4. 手指松开的时候两者选其一，要么打开要么关闭
         */
        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            if (releasedChild == mDragListView) {
                Log.e(TAG, "yvel: " + yvel + ", mMenuHeight: " + mMenuHeight);
                Log.e(TAG, "mDragListView.top: " + mDragListView.getTop());
                if (mDragListView.getTop() >= mMenuHeight / 2) { // 滚动菜单的高度，打开
                    mDragHelper.settleCapturedViewAt(0, mMenuHeight);
                    mMenuOpened = true;
                } else { // 滚动到top==0的位置，关闭
                    mDragHelper.settleCapturedViewAt(0, 0);
                    mMenuOpened = false;
                }
                // 4.1
                invalidate();
            }
        }

    };

    /*
        4.2 响应滚动

        在移动平台中，要明确知道 "滑动" 与 "滚动" 的不同，滑动和滚动的方向总是相反的

            Scroller只是个计算器，提供插值计算，让滚动过程具有动画属性

            滑动时松手后以一定速率继续自动滑动下去并逐渐停止，类似于扔东西
            或者 松手后自动滑动到指定位置，需要实现自定义ViewGroup的computeScroll()方法
     */
    @Override
    public void computeScroll() {
        // scrollTo, scrollBy 都会调用invalidate，而invalidate触发draw, 从而computeScroll被连续调用
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        }
    }

    /*
        5. 事件的分发和拦截
            先将TextView换成ListView

        现象: ListView可以滑动，但是在ListView第一个元素在最顶部时，无法再向下滑动
            android.view.ViewGroup.requestDisallowInterceptTouchEvent
                让父布局不要调用onInterceptTouchEvent
                ListView -> AbsListView -> requestDisallowInterceptTouchEvent(true)
                    请父布局不要拦截，即让父布局不要调用onInterceptTouchEvent

        E/ViewDragHelper: Ignoring pointerId=0
        because ACTION_DOWN was not received for this pointer before ACTION_MOVE.
        It likely happened because ViewDragHelper did not receive all the events in the event stream.

            com.z.v_draghelper.DragListView01.onInterceptTouchEvent(ACTION_DOWN)
                -> ListView.onTouch()
            com.z.v_draghelper.DragListView01.onInterceptTouchEvent(ACTION_MOVE)
                拦截ListView的onTouchEvent
                去处理自己的com.z.v_draghelper.DragListView01.onTouchEvent
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        /*
            菜单处于打开状态, 拦截ListView的所有事件
         */
        if (mMenuOpened) {
            return true;
        }

        /*
            向下水里去拦截，不要给ListView处理

            父View拦截子View
         */
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                // 先接收ACTION_DOWN, 再处理ACTION_MOVE, ACTION_UP, 让人mDragHelper获取一个完整的事件
                mDragHelper.processTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = ev.getY();
                if (moveY > mDownY && !canChildScrollUp()) {
                    /*
                        向下滑动 并且 ListView滚动到最顶部，拦截ListView的事件，我们负责处理
                            SwipeRefreshLayout
                                判断一个ListView是否滚动到最顶部

                        此处返回true会走到我们的onTouchEvent
                     */
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    private float mDownY;

    /*
        判断一个ListView是否滚动到最顶部

        scroll up 向上滚动，滚动条向上滚动，内容向下滚动???
        scroll down 向下滚动，滚动条向下滚动，内容向上滚动???

        Whether it is possible for the child view of this layout to scroll up.
        Override this if the child view is a custom view.
     */
    public boolean canChildScrollUp() {
        return mDragListView.canScrollVertically(-1);
    }
}
