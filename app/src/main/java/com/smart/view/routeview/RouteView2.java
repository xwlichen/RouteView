package com.smart.view.routeview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
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
public class RouteView2 extends ViewGroup {

    private static final int DEFALUT_DISTANCE = 1000;


    private Context context;

    private int colorRoute = Color.GRAY;
    private Paint paintRoute;

    private int width;
    private int height;

    private int screenWidth;
    private int totalWidth;

    private int itemWidth = 200;
    private int itemHeight = 150;
    private int itemPadding = 100;

    private int routeSmallWidth = 50;
    private int routeSmallHeight = 25;
    private int routeBigWidth = 100;
    private int routeBigHeight = 50;
    private int routePadding = 25;


    private List<ImageView> items;


    private List<RouteBean> itemList;
    private List<RouteBean> routeList;


    private static final int MIN_DISTANCE_FOR_FLING = 25; // dips


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
    private VelocityTracker velocityTracker;
    private int maxVelocity;



    public RouteView2(Context context) {
        super(context);
        init(context);
    }

    public RouteView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init(context);
    }

    public RouteView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init(context);
    }


    protected void initAttrs(AttributeSet attrs) {
    }

    protected void init(Context context) {

        this.context = context;
        scroller = new Scroller(context);
        ViewConfiguration config = ViewConfiguration.get(context);
        maxVelocity = config.getScaledMinimumFlingVelocity();

        setWillNotDraw(false);
        screenWidth = TDevice.getScreenWidth(context);
        items = new ArrayList<>();
        initPaint();

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
                    int left = itemList.get(i).getItemLeft();
                    int top = itemList.get(i).getItemTop();
                    int right = itemList.get(i).getItemRight();
                    int bottom = itemList.get(i).getItemBottom();

                    imageView.layout(left, top, right, bottom);

                    imageView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "点击" + imageView.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    if (i == (itemList.size() - 1)) {
                        totalWidth = right + 50;
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

        canvas.drawLine(0, height / 2, width, height / 2, paintRoute);

        drawRoute(canvas);
    }

    private void drawRoute(Canvas canvas) {
//        RectF rectF1 = new RectF(100, height / 2, 150, height / 2 + 25);
//        canvas.drawOval(rectF1, paintRoute);
//
//        RectF rectF2 = new RectF(160, height / 2 + 25 + 10, 260, height / 2 + 25 + 10 + 50);
//        canvas.drawOval(rectF2, paintRoute);

        if (routeList != null && routeList.size() > 0) {
            for (int i = 0; i < routeList.size(); i++) {

                int left = routeList.get(i).getItemLeft();
                int top = routeList.get(i).getItemTop();
                int right = routeList.get(i).getItemRight();
                int bottom = routeList.get(i).getItemBottom();

//                Log.e("xw","left:"+left+" ,top:"+right+" ,right:"+right+" ,bottom:"+bottom);

                RectF rectF = new RectF(left, top, right, bottom);
                canvas.drawOval(rectF, paintRoute);

            }
        }

    }


    private void initItemPosition() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        itemList.clear();
        int upBottom = height / 2;
        int upTop = upBottom - itemHeight;
        int downTop = height / 2;
        int downBottom = downTop + itemHeight;


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
            itemBean.setItemLeft(2 * i * itemPadding + i * itemWidth);
            itemBean.setItemRight(itemBean.getItemLeft() + itemWidth);

            RouteBean routeBean1 = new RouteBean();
            routeBean1.setItemLeft(itemBean.getItemRight());
            routeBean1.setItemRight(routeBean1.getItemLeft() + routeSmallWidth);

            RouteBean routeBean2 = new RouteBean();
            routeBean2.setItemLeft(routeBean1.getItemLeft() + routePadding);
            routeBean2.setItemRight(routeBean2.getItemLeft() + routeBigWidth);

            if (i % 2 == 0) {
                itemBean.setItemTop(upTop);
                itemBean.setItemBottom(upBottom);

                routeBean1.setItemTop(route1DownTop);
                routeBean1.setItemBottom(route1DownBottom);

                routeBean2.setItemTop(route2DownTop);
                routeBean2.setItemBottom(route2DownBottom);


            } else {
                itemBean.setItemTop(downTop);
                itemBean.setItemBottom(downBottom);

                routeBean1.setItemTop(route1UpTop);
                routeBean1.setItemBottom(route1UpBottom);

                routeBean2.setItemTop(route2UpTop);
                routeBean2.setItemBottom(route2UpBottom);


            }

            itemList.add(itemBean);
            routeList.add(routeBean1);
            routeList.add(routeBean2);

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
    int x;
    int dx;
    int scrollX;
    int upDistance;
    int bottomLimit;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        initVelocityTrackerIfNotExists();
        velocityTracker.addMovement(event);
        x = (int) event.getRawX();
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                lastX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (totalWidth > 0) {
                    dx = lastX - x;//本次手势滑动了多大距离
                    int oldScrollX = getScrollX();//先计算之前已经偏移了多少距离
                    scrollX = oldScrollX + (int) (dx * 1.3f);//本次需要偏移的距离=之前已经偏移的距离+本次手势滑动了多大距离
                    if (scrollX < 0) {
                        scrollX = 0;
                    }
                    if (scrollX > totalWidth - screenWidth) {
                        scrollX = totalWidth - screenWidth;
                    }
                    scrollTo(scrollX, getScrollY());
//                    smoothScrollBy(scrollX,0);

                    Log.e("xw", "x:" + x + "   ,lastX:" + lastX + "   ,dx:" + dx);
                    lastX = x;

                }
                break;

            case MotionEvent.ACTION_UP:
                final VelocityTracker mVelocityTracker = velocityTracker;
                mVelocityTracker.computeCurrentVelocity(1000);
                int initVelocity = (int) mVelocityTracker.getXVelocity();
                if (Math.abs(initVelocity)>maxVelocity){
                    Log.e("lichen","scrollX:"+scrollX);
                    Log.e("lichen","dx     :"+dx);

                    Log.e("lichen","initVelocity     :"+initVelocity);
                    Log.e("lichen","maxVelocity     :"+maxVelocity);


                    upDistance = DEFALUT_DISTANCE;
                    bottomLimit=totalWidth - screenWidth;
                    int distance = 0;
                    if (scrollX < upDistance) {
                        if (dx > 0) {
                            distance = upDistance;
                        } else if (dx < 0) {
                            distance = -scrollX;

                        }
                    } else if (scrollX > bottomLimit-upDistance) {
                        if (dx > 0) {
                            distance =bottomLimit-scrollX;
                        } else if (dx < 0) {
                            distance = -upDistance;
                        }
                    } else {
                        if (dx > 0) {
                            distance = upDistance;
                        } else if (dx < 0) {
                            distance = -upDistance;
                        }
                    }

                    smoothScrollBy(distance, 0);
                }
                recycleVelocityTracker();
                lastX = x;
                break;
        }
        return true;
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
//        if (xDiff > mTouchSlop && xDiff > yDiff) {
        if (xDiff > 25) {
            startDrag();
            mLastMotionX = x;
            mLastMotionY = y;
//            Log.e("lihcen", "mIsUnableToDrag: true");

        } else {
            mIsBeingDragged = false;
//            Log.e("lihcen", "mIsUnableToDrag: false");

        }

        lastX = (int) ev.getRawX();
    }

    // 开始滑动
    private void startDrag() {
        mIsBeingDragged = true;
        mScrollToEnd = false;
    }



    //调用此方法滚动到目标位置
    public void smoothScrollTo(int fx, int fy) {
        int dx = fx - scroller.getFinalX();
        int dy = fy - scroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBy(int dx, int dy) {

        //设置scroller的滚动偏移量
        scroller.startScroll(getScrollX(), getScrollY(), dx, dy, 800);
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }






    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }


    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    private void initVelocityTrackerIfNotExists() {
        if(velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }
    }


}
