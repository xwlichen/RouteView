package com.smart.view.routeview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * @date : 2019-05-27 11:06
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class RouteView extends ViewGroup {


    private Context context;

    private int colorRoute = Color.GRAY;
    private Paint paintRoute;

    private int width;
    private int height;

    private int screenHeight;
    private int screenWidth;
    private int totalHeight;

    private int itemWidth = 200;
    int itemHeight = 150;
    private int itemPadding = 50;

    private int routeSmallWidth = 50;
    private int routeSmallHeight = 25;
    private int routeBigWidth = 100;
    private int routeBigHeight = 50;
    private int routePadding = 25;


    private List<ImageView> items;


    private List<RouteBean> itemList;
    private List<RouteBean> routeList;


    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;

    private float mInitialMotionY, mInitialMotionX;

    protected int mActivePointerId = INVALID_POINTER;
    private static final int INVALID_POINTER = -1;

    private boolean mIsBeingDragged = false;
    boolean mScrollToEnd = false;//标记View是否已经完全消失，OvershootInterpolator回弹时间忽略

    Scroller scroller;
    private GestureDetector mGestureDetector;
    private float mOffset;

    private Bitmap bitmap;


    private OnScrollerListener onScrollerListener;

    public RouteView(Context context) {
        super(context);
        init(context);
    }

    public RouteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init(context);
    }

    public RouteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init(context);
    }


    public OnScrollerListener getOnScrollerListener() {
        return onScrollerListener;
    }

    public void setOnScrollerListener(OnScrollerListener onScrollerListener) {
        this.onScrollerListener = onScrollerListener;
    }

    protected void initAttrs(AttributeSet attrs) {
    }

    protected void init(Context context) {

        this.context = context;

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(context);


        setWillNotDraw(false);
        screenHeight = TDevice.getScreenHeight(context);
        screenWidth = TDevice.getScreenWidth(context);
        items = new ArrayList<>();
        initPaint();


        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.timg);
    }

    protected void initPaint() {
        paintRoute = new Paint();
        paintRoute.setAntiAlias(true);
        paintRoute.setStrokeCap(Paint.Cap.ROUND);
        paintRoute.setColor(colorRoute);


    }


    /**
     * 根据Model返回值
     *
     * @param value
     * @return
     */
    private int measure(int value) {
        int result = 0;
        int specMode = MeasureSpec.getMode(value);
        int specSize = MeasureSpec.getSize(value);
        switch (specMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                result = specSize;
                break;
            default:
                result = specSize;
                break;
        }

        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        width = measure(widthMeasureSpec);
        height = measure(heightMeasureSpec);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("xw", "width:" + width + "height:" + height);

        if (height > 0) {
            initItemPosition();

        }


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count == 0) {
            if (itemList != null && itemList.size() > 0) {
                for (int i = 0; i < itemList.size(); i++) {
                    final ImageView imageView = new ImageView(context);
                    imageView.setImageResource(R.mipmap.ic_launcher);
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                    addView(imageView);
                    int left = itemList.get(i).getLeft();
                    int top = itemList.get(i).getTop();
                    int right = itemList.get(i).getRight();
                    int bottom = itemList.get(i).getBottom();

                    imageView.layout(left, top, right, bottom);

                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "点击" + imageView.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    if (i == (itemList.size() - 1)) {
                        totalHeight = bottom + 50;
                    }
                }
            }
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (width <= 0) {
            return;
        }

//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色

//        canvas.save();
        if (mOffset < 0) {
            canvas.translate(0, mOffset);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);

        canvas.drawLine(width / 2, 0, width / 2, height, paintRoute);

        drawRoute(canvas);

//        canvas.restore();


    }

    private void drawRoute(Canvas canvas) {
//
        RectF rectF2 = new RectF(160, height / 2 + 25 + 10, 260, height / 2 + 25 + 10 + 50);
        canvas.drawOval(rectF2, paintRoute);

        if (routeList != null && routeList.size() > 0) {
            for (int i = 0; i < routeList.size(); i++) {

                int left = routeList.get(i).getLeft();
                int top = routeList.get(i).getTop();
                int right = routeList.get(i).getRight();
                int bottom = routeList.get(i).getBottom();

//                Log.e("xw","left:"+left+" ,top:"+right+" ,right:"+right+" ,bottom:"+bottom);

//                RectF rectF = new RectF(left, top, right, bottom);
//                canvas.drawOval(rectF, paintRoute);

            }
        }

    }


    private void initItemPosition() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.clear();
        int lRight = width / 2;
        int lLeft = lRight - itemWidth;
        int rLeft = width / 2;
        int rRight = rLeft + itemWidth;


        int route1UpBottom = height / 2;
        int route1UpTop = route1UpBottom - routeSmallHeight;
        int route1DownTop = height / 2;
        int route1DownBottom = route1DownTop + routeSmallHeight;


        int route2UpBottom = route1UpTop;
        int route2UpTop = route2UpBottom - routeBigHeight;
        int route2DownTop = route1DownBottom;
        int route2DownBottom = route2DownTop + routeBigHeight;


        if (routeList == null) {
            routeList = new ArrayList<>();
        }
        routeList.clear();


        for (int i = 0; i < 30; i++) {
            RouteBean itemBean = new RouteBean();
            itemBean.setTop(2 * i * itemPadding + i * itemHeight);
            itemBean.setBottom(itemBean.getTop() + itemHeight);

//            RouteBean routeBean1 = new RouteBean();
//            routeBean1.setLeft(itemBean.getRight());
//            routeBean1.setRight(routeBean1.getLeft() + routeSmallWidth);
//
//            RouteBean routeBean2 = new RouteBean();
//            routeBean2.setLeft(routeBean1.getLeft() + routePadding);
//            routeBean2.setRight(routeBean2.getLeft() + routeBigWidth);

            if (i % 2 == 0) {
                itemBean.setLeft(lLeft);
                itemBean.setRight(lRight);

//                routeBean1.setTop(route1DownTop);
//                routeBean1.setBottom(route1DownBottom);

//                routeBean2.setTop(route2DownTop);
//                routeBean2.setBottom(route2DownBottom);


            } else {
                itemBean.setLeft(rLeft);
                itemBean.setRight(rRight);

//                routeBean1.setTop(route1UpTop);
//                routeBean1.setBottom(route1UpBottom);
//
//                routeBean2.setTop(route2UpTop);
//                routeBean2.setBottom(route2UpBottom);


            }

            itemList.add(itemBean);
//            routeList.add(routeBean1);
//            routeList.add(routeBean2);

        }

    }

    private void initRoutPosition() {
        if (routeList == null) {
            routeList = new ArrayList<>();
        }
        routeList.clear();
        for (int i = 0; i < 10; i++) {
            RouteBean bean = new RouteBean();

        }

    }


    int lastX;
    int lastY;
    int y;
    int x;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);

    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();

        switch (action & MotionEventCompat.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                // Remember where the motion event started
                int index = MotionEventCompat.getActionIndex(ev);
                mActivePointerId = MotionEventCompat.getPointerId(ev, index);
                if (mActivePointerId == INVALID_POINTER) {
                    break;
                }
                mInitialMotionX = MotionEventCompat.getX(ev, index);
                mLastMotionX = mInitialMotionX;
                mLastMotionY = MotionEventCompat.getY(ev, index);
                mInitialMotionY = mLastMotionY;

                mIsBeingDragged = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == INVALID_POINTER) {
                    break;
                }
                determineDrag(ev);
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        if (mIsBeingDragged) {
            return mIsBeingDragged;

        } else {
            return super.onInterceptTouchEvent(ev);
        }
    }

    private int getPointerIndex(MotionEvent ev, int id) {
        int activePointerIndex = MotionEventCompat.findPointerIndex(ev, id);
        if (activePointerIndex == -1) {
            mActivePointerId = INVALID_POINTER;
        }
        return activePointerIndex;
    }


    private void determineDrag(MotionEvent ev) {
        final int activePointerId = mActivePointerId;
        final int pointerIndex = getPointerIndex(ev, activePointerId);
        if (activePointerId == INVALID_POINTER || pointerIndex == INVALID_POINTER) {
            return;
        }
        final float x = MotionEventCompat.getX(ev, pointerIndex);
        final float dx = x - mLastMotionX;
        final float xDiff = Math.abs(dx);
        final float y = MotionEventCompat.getY(ev, pointerIndex);
        final float dy = y - mLastMotionY;
        final float yDiff = Math.abs(dy);
        if (yDiff > 25) {
            startDrag();
            mLastMotionX = x;
            mLastMotionY = y;
//            Log.e("lihcen", "mIsUnableToDrag: true");

        } else {
            mIsBeingDragged = false;
//            Log.e("lihcen", "mIsUnableToDrag: false");

        }

        lastY = (int) ev.getRawY();
        lastX = (int) ev.getRawX();

    }

    // 开始滑动
    private void startDrag() {
        mIsBeingDragged = true;
        mScrollToEnd = false;
    }


    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            mOffset = scroller.getCurrY();
            postInvalidate();
        }
    }


    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            y = (int) e.getRawY();
            x = (int) e.getRawX();
            scroller.forceFinished(true);
            invalidate();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            int distance = lastY - y;  //上滑 dy<0  distaceY>0
            if (distance * distanceY <= 0) {
                mOffset += -distanceY;
                mOffset = Math.min(mOffset, 0);
                mOffset = Math.max(mOffset, -totalHeight);
                Log.e("smooth", "distanceY:" + distanceY);
                Log.e("smooth", "dY:" + distance);
                Log.e("smooth", "mOffset:" + mOffset);
                int x = (int) e2.getRawX();
                int y = (int) e2.getRawY();
                if (onScrollerListener != null) {
                    onScrollerListener.onScroll(x, y, lastX, lastY);
                }

                invalidate();
            }
            lastY = y;
            lastX = x;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 300, -totalHeight, 0);
            int x = (int) e2.getRawX();
            int y = (int) e2.getRawY();
            if (onScrollerListener != null) {
                onScrollerListener.onScroll(x, y, lastX, lastY);
            }
            return true;
        }


    };


}
