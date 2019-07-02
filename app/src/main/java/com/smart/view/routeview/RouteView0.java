package com.smart.view.routeview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * @date : 2019-05-27 11:06
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class RouteView0 extends FrameLayout {


    private Context context;

    private int colorRoute = Color.GRAY;
    private Paint paintRoute;

    private int width;
    private int height;

    private int screenHeight;
    private int screenWidth;
    private int totalHeight;

    private int itemWidth = 400;
    private int itemHeight = 300;
    private int itemPadding = 50;
    private int firstTopMargin = 400;


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

    private ViewGroup.LayoutParams layoutParams;


    private boolean isInit = false;

    //标题
    private Paint paintTitle;
    private int colorTitle = Color.BLUE;
    private int titleSize = 30;
    private int titlePadding = 20;


    //标签
    private Paint paintLab;
    private Paint paintLabText;
    private Path tranPath;

    private int colorLab = Color.BLUE;
    private int labSize = 30;
    private int labPadding = 20;

    private int tranSize = 10;
    RequestOptions options;

    public RouteView0(Context context) {
        super(context);
        init(context);
    }

    public RouteView0(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init(context);
    }

    public RouteView0(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        init(context);
    }


    protected void initAttrs(AttributeSet attrs) {
    }

    protected void init(Context context) {

        this.context = context;

        mGestureDetector = new GestureDetector(getContext(), mSimpleOnGestureListener);
        mGestureDetector.setIsLongpressEnabled(false);
        scroller = new Scroller(context);

        mFling=new FlingRunnable(context);


        // 新增部分 start
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        touchSlop = viewConfiguration.getScaledTouchSlop();
        mScroller = new Scroller(context);
        mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();


        setWillNotDraw(false);
        screenHeight = TDevice.getScreenHeight(context);
        screenWidth = TDevice.getScreenWidth(context);
        items = new ArrayList<>();
        initPaint();


        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.timg);

        layoutParams = new ViewGroup.LayoutParams(400, 300);
        options = new RequestOptions()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.DATA);

        valueAnimator = new ValueAnimator();

    }

    protected void initPaint() {
        paintRoute = new Paint();
        paintRoute.setAntiAlias(true);
        paintRoute.setStrokeCap(Paint.Cap.ROUND);
        paintRoute.setColor(colorRoute);


        AssetManager aManager = context.getApplicationContext().getAssets();
        Typeface typeface = Typeface.createFromAsset(aManager, "fonts/font_65s.otf");


        paintTitle = new Paint();
        paintTitle.setAntiAlias(true);
        paintTitle.setStrokeCap(Paint.Cap.ROUND);
        paintTitle.setColor(colorTitle);
        paintTitle.setTextSize(titleSize);
        paintTitle.setTypeface(typeface);
        paintTitle.setTextAlign(Paint.Align.CENTER);

        paintLab = new Paint();
        paintLab.setAntiAlias(true);
        paintLab.setStyle(Paint.Style.FILL);
        paintLab.setStrokeCap(Paint.Cap.ROUND);
        paintLab.setColor(colorLab);
        paintLab.setTextSize(labSize);

        paintLabText = new Paint();
        paintLabText.setAntiAlias(true);
        paintLabText.setStyle(Paint.Style.FILL);
        paintLabText.setStrokeCap(Paint.Cap.ROUND);
        paintLabText.setColor(Color.WHITE);
        paintLabText.setTextSize(labSize);
        paintLabText.setTypeface(typeface);
        paintLabText.setTextAlign(Paint.Align.CENTER);

        tranPath = new Path();


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


    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        if (count == 0) {
            if (itemList != null && itemList.size() > 0) {
                if (!isInit) {
                    initItemPosition();
                }

                for (int i = 0; i < itemList.size(); i++) {
                    final FlowSideView flowSideView = new FlowSideView(context);
//                    final ImageView flowSideView = new ImageView(context);
//                    flowSideView.setImageResource(R.mipmap.ic_launcher);
                    flowSideView.setSize(400, 300, itemList.get(i));
                    flowSideView.setLayoutParams(layoutParams);
                    flowSideView.setVisibility(VISIBLE);


                    addView(flowSideView);

                    int left = itemList.get(i).getItemLeft();
                    int top = itemList.get(i).getItemTop();
                    int right = itemList.get(i).getItemRight();
                    int bottom = itemList.get(i).getItemBottom();

                    flowSideView.layout(left, top, right, bottom);
                    Glide.with(context).load(itemList.get(i).getImgUrl()).apply(options).into(flowSideView);

                    flowSideView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "点击" + flowSideView.getRouteBean().getTitle(), Toast.LENGTH_LONG).show();

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


//        canvas.save();
//        if (mOffset < 0) {
//            canvas.translate(0, mOffset);
//        }

//        if (moveY < 0) {
//            canvas.translate(0, moveY);
//        }


        canvas.drawBitmap(bitmap, 0, 0, null);
        if (totalHeight > bitmap.getHeight()) {
            canvas.drawBitmap(bitmap, 0, bitmap.getHeight(), null);
        }
        canvas.drawLine(width / 2, 0, width / 2, height, paintRoute);

        if (itemList != null && itemList.size() > 0) {
            if (!isInit) {
                initItemPosition();
            }
//            drawRoute(canvas);

            for (int i = 0; i < itemList.size(); i++) {


                //绘制标题
                canvas.drawText(itemList.get(i).getTitle(),
                        itemList.get(i).getTitleLeft(),
                        itemList.get(i).getTitleTop(),
                        paintTitle);


                //绘制label
                if (itemList.get(i).isLearning()) {
                    int rectLeft = itemList.get(i).getLabRectLeft();
                    int rectBottom = itemList.get(i).getLabRectBottom();
                    int rectRight = itemList.get(i).getLabRectRight();
                    int rectTop = itemList.get(i).getLabRectTop();


                    canvas.drawRoundRect(new RectF(rectLeft, rectTop, rectRight, rectBottom), 25, 25, paintLab);
                    tranPath.moveTo(itemList.get(i).getLabTranDot1()[0], itemList.get(i).getLabTranDot1()[1]);
                    tranPath.lineTo(itemList.get(i).getLabTranDot2()[0], itemList.get(i).getLabTranDot2()[1]);
                    tranPath.lineTo(itemList.get(i).getLabTranDot3()[0], itemList.get(i).getLabTranDot3()[1]);
                    canvas.drawPath(tranPath, paintLab);


                    canvas.drawText("学到这",
                            itemList.get(i).getLabTextLeft(),
                            itemList.get(i).getLabTextTop(),
                            paintLabText);


                }

            }
        }

//        canvas.restore();


    }

    private void drawRoute(Canvas canvas) {
//
        RectF rectF2 = new RectF(160, height / 2 + 25 + 10, 260, height / 2 + 25 + 10 + 50);
        canvas.drawOval(rectF2, paintRoute);

        if (routeList != null && routeList.size() > 0) {
            for (int i = 0; i < routeList.size(); i++) {

                int left = routeList.get(i).getItemLeft();
                int top = routeList.get(i).getItemTop();
                int right = routeList.get(i).getItemRight();
                int bottom = routeList.get(i).getItemBottom();


//                RectF rectF = new RectF(left, top, right, bottom);
//                canvas.drawOval(rectF, paintRoute);

            }
        }

    }


    private void initItemPosition() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        int lRight = width / 2 - itemPadding;
        int lLeft = lRight - itemWidth;
        int rLeft = width / 2 + itemPadding;
        int rRight = rLeft + itemWidth;


        for (int i = 0; i < itemList.size(); i++) {
            RouteBean itemBean = itemList.get(i);
            itemBean.setItemTop(2 * i * itemPadding + i * itemHeight + firstTopMargin);
            itemBean.setItemBottom(itemBean.getItemTop() + itemHeight);


            if (i % 2 == 0) {
                itemBean.setItemLeft(lLeft);
                itemBean.setItemRight(lRight);


            } else {
                itemBean.setItemLeft(rLeft);
                itemBean.setItemRight(rRight);


            }
            setTitleXY(itemBean);
            if (itemBean.isLearning()) {
                setLabPosition(itemBean);
            }


        }

        isInit = true;

    }


    protected void setTitleXY(RouteBean itemBean) {
        Rect rect = new Rect();
        paintTitle.getTextBounds(itemBean.getTitle(), 0, itemBean.getTitle().length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        int rectLeft = itemBean.getItemLeft();
        int rectTop = itemBean.getItemBottom();
        int rectRight = itemBean.getItemRight();
        int rectBottom = rectTop + textHeight + titlePadding * 2;


        int rectX = (rectLeft + rectRight) / 2;
        int rectY = (rectTop + rectBottom) / 2;


        //计算baseline
        Paint.FontMetrics fontMetrics = paintTitle.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = rectY + distance;

        itemBean.setTitleLeft(rectX);
        itemBean.setTitleTop((int) baseline);
    }


    protected void setLabPosition(RouteBean itemBean) {


        Rect rect = new Rect();
        paintLabText.getTextBounds("学到这", 0, "学到这".length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        int rectX = (itemBean.getItemLeft() + itemBean.getItemRight()) / 2;

        int rectLeft = rectX - textWidth / 2 - labPadding * 2;
        int rectBottom = itemBean.getItemTop() - tranSize - labPadding;
        int rectRight = rectX + textWidth / 2 + labPadding * 2;
        int rectTop = rectBottom - textHeight - labPadding * 2;


        int rectY = (rectTop + rectBottom) / 2;

        //计算baseline
        Paint.FontMetrics fontMetrics = paintTitle.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        float baseline = rectY + distance;

        itemBean.setLabTextLeft(rectX);
        itemBean.setLabTextTop((int) baseline);


        itemBean.setLabRectLeft(rectLeft);
        itemBean.setLabRectTop(rectTop);
        itemBean.setLabRectRight(rectRight);
        itemBean.setLabRectBottom(rectBottom);

        int[] do1 = {rectX, rectBottom + tranSize};
        int[] do2 = {rectX - tranSize / 2, rectBottom};
        int[] do3 = {rectX + tranSize / 2, rectBottom};


        itemBean.setLabTranDot1(do1);
        itemBean.setLabTranDot2(do2);
        itemBean.setLabTranDot3(do3);


    }


    int lastX;
    int lastY;
    int y;
    int x;


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
                y = (int) ev.getRawY();
                x = (int) ev.getRawX();
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
//        if (mIsBeingDragged) {
            return mIsBeingDragged;
//        } else {
//            return super.onInterceptTouchEvent(ev);
//        }
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
//        lastX = (int) ev.getRawX();

    }

    // 开始滑动
    private void startDrag() {
        mIsBeingDragged = true;
        mScrollToEnd = false;
    }


    @Override
    public void computeScroll() {
//        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            mOffset = scroller.getCurrY();
            scrollTo(scroller.getCurrX(),scroller.getCurrY());
            postInvalidate();
        } else {
//            if (isFastScroll) {
//                int x = mScroller.getCurrY();
//
//                scrollTo(0, x);
//                postInvalidate();
//            }
        }
    }


    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
//        @Override
//        public boolean onDown(MotionEvent e) {
////            y = (int) e.getRawY();
////            x = (int) e.getRawX();
////            scroller.forceFinished(true);
////            invalidate();
//            return true;
//        }
//
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//            int distance = lastY - y;  //上滑 dy<0  distaceY>0
//            if (distance * distanceY <= 0) {
//                mOffset += -distanceY;
//                mOffset = Math.min(mOffset, 0);
//                mOffset = Math.max(mOffset, -totalHeight + screenHeight - 500);
//                Log.e("smooth", "distanceY:" + distanceY);
//                Log.e("smooth", "dY:" + distance);
//                Log.e("smooth", "mOffset:" + mOffset);
//                int x = (int) e2.getRawX();
//                int y = (int) e2.getRawY();
//                invalidate();
//            }
//            lastY = y;
//            lastX = x;
//            return true;
//        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocitX, float velocityY) {
//            if (mOffset >= (-totalHeight + screenHeight)) {
                scroller.fling(getScrollX(), getScrollY(), 0, (int) velocityY, 0, 0, totalHeight-screenHeight, 0);
//            }
//            invalidate();
            int x = (int) e2.getRawX();
            int y = (int) e2.getRawY();



            return true;
        }


    };


    public void setData(List<RouteBean> itemList) {
        itemList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            RouteBean routeBean = new RouteBean();
            if (i == 0) {

                routeBean.setLearning(true);
                routeBean.setFree(true);

            } else if (i == 4) {
                routeBean.setFree(true);
            }

            routeBean.setTitle(i + "");
            itemList.add(routeBean);

            String url1 = "https://staticcdn.changguwen.com/cms/img/2019123/779a9469-a011-454f-a445-f0b5c764223c-1548229753383.jpg";
            String url2 = "https://staticcdn.changguwen.com/cms/img/2018111/42aa7020-98ec-4ab3-923a-d122a84c4e1d-1541040921320.jpg";
            if (i % 2 == 0) {
                routeBean.setImgUrl(url1);
            } else {
                routeBean.setImgUrl(url2);

            }

        }

        this.itemList = itemList;

        invalidate();
        requestLayout();

    }


    float downY;
    float moveY;
    float lastMoveY;
    private int xVelocity;

    private Scroller mScroller;

    private float mLastY;
    private int touchSlop;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private boolean isFastScroll;
    private float currentOffset; // 当前偏移

    private int space = 30;



    float scrollY;

    int dy;

    float distance;



    /**
     * 惯性滑动
     */
    public void flingY(int velocityY) {
        scroller.fling(getScrollX(), getScrollY(), 0, velocityY, 0, 0, 0, totalHeight-screenHeight+firstTopMargin);
        awakenScrollBars(mScroller.getDuration());
        Log.e("xw", "mScroller.getDuration():" + mScroller.getDuration());
        invalidate();
    }

    void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 500);//从当前滑动的位置，平滑地过度到目标位置
        invalidate();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        obtainVelocityTracker();
        mVelocityTracker.computeCurrentVelocity(500, mMaximumVelocity);
        if (mVelocityTracker != null) {
            mVelocityTracker.addMovement(event);
        }
        int action = event.getAction();
        float moveY = event.getRawY();
//        mGestureDetector.onTouchEvent(event);


        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFling.stop();
                mLastY = event.getRawY();
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
//                y = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:

                isFastScroll = false;
                currentOffset = (int) (moveY - y);
                scrollY=getScrollY() - currentOffset;
                if (scrollY<=0){
                    scrollY=0;
                }else if (scrollY>=totalHeight-screenHeight+firstTopMargin){
                    scrollY=totalHeight-screenHeight+firstTopMargin;
                }
                scrollTo(0, (int) scrollY);
                distance=scrollY;
                y = (int) moveY;
//                invalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                int initialVelocity = (int) mVelocityTracker.getYVelocity();
                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
                    flingY(-initialVelocity);
                }


//                actionUp();

//                if (scrollY<=0){
//                    scrollY=0;
//                    return true;
//                } if (scrollY>=totalHeight-screenHeight+firstTopMargin){
//                    scrollY=totalHeight-screenHeight+firstTopMargin;
//                    return true;
//                }
//                int initialVelocity = (int) mVelocityTracker.getYVelocity();
//                if ((Math.abs(initialVelocity) > mMinimumVelocity)) {
//                    isFastScroll = true;
////                    flingY(-initialVelocity);
//                    autoVelocityScroll(initialVelocity);
//                } else {
//                    int y = getScrollY();
//
//                    if (y < 0) {
//                        y = 0;
//                    } else if (y > (totalHeight - screenHeight+firstTopMargin)) {
//                        y = totalHeight - screenHeight+firstTopMargin;
//                    }
//                    scrollTo(0, y);
//                }
//                releaseVelocityTracker();
                break;
            default:
                break;
        }

        return true;

    }


    private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
    private ValueAnimator valueAnimator;
    private boolean isUp = false;

    /**
     * 初始化 速度追踪器
     */
    private void obtainVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
    }

    /**
     * 释放 速度追踪器
     */
    private void releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }


    private void autoVelocityScroll(int xVelocity) {
        //惯性滑动的代码,速率和滑动距离,以及滑动时间需要控制的很好,应该网上已经有关于这方面的算法了吧。。这里是经过N次测试调节出来的惯性滑动
        if (Math.abs(xVelocity) < 50) {
            isUp = true;
            return;
        }
        if (valueAnimator.isRunning()) {
            return;
        }
        valueAnimator = ValueAnimator.ofInt(0, xVelocity / 40).setDuration(Math.abs(xVelocity / 60));
//        valueAnimator.setDuration(300);

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value=(int) animation.getAnimatedValue();
                distance -= (int) animation.getAnimatedValue();
                if (distance <0) {
                    distance = 0;
                } else if (distance >=totalHeight-screenHeight+firstTopMargin) {
                    distance = totalHeight-screenHeight;
                }
                scrollTo(0, (int) distance);
//                invalidate();
            }

        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isUp = true;
                invalidate();
            }
        });

        valueAnimator.start();
    }



    public void actionUp(){
        // 计算当前速度， 1000表示每秒像素数等
        mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

        // 获取横向速度
        int velocityY = (int) mVelocityTracker.getXVelocity();

        // 速度要大于最小的速度值，才开始滑动
        if (Math.abs(velocityY) > mMinimumVelocity) {

            int initY = getScrollY();

            int maxY = (int) (totalHeight - screenHeight);
            if (maxY > 0) {
                mFling.start(initY, velocityY, initY, maxY);
            }
        }

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    FlingRunnable mFling;

    /**
     * 滚动线程
     */
    private class FlingRunnable implements Runnable {

        private Scroller mScroller;

        private int initY;
        private int minY;
        private int maxY;
        private int velocityY;

        FlingRunnable(Context context) {
            this.mScroller = new Scroller(context, null, false);
        }

        void start(int initY,
                   int velocityY,
                   int minY,
                   int maxY) {
            this.initY = initY;
            this.velocityY = velocityY;
            this.minY = minY;
            this.maxY = maxY;

            // 先停止上一次的滚动
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }

            // 开始 fling
            mScroller.fling(0, initY, 0,
                    velocityY, 0,0 , -maxY, 0);
            post(this);
        }

        @Override
        public void run() {

            // 如果已经结束，就不再进行
            if (!mScroller.computeScrollOffset()) {
                return;
            }

            // 计算偏移量
            int currY = mScroller.getCurrY();
            int diffy = initY - currY;

            Log.i("xw", "run: [currX: " + currY + "]\n"
                    + "[diffy: " + diffy + "]\n"
                    + "[initY: " + initY + "]\n"
                    + "[minY: " + minY + "]\n"
                    + "[maxY: " + maxY + "]\n"
                    + "[velocityY: " + velocityY + "]\n"
            );

            // 用于记录是否超出边界，如果已经超出边界，则不再进行回调，即使滚动还没有完成
            boolean isEnd = false;

            if (diffy != 0) {

                // 超出右边界，进行修正
                if (getScrollX() + diffy >= totalHeight-screenHeight) {
                    diffy = (int) (totalHeight-screenHeight - getScrollX());
                    isEnd = true;
                }

                // 超出左边界，进行修正
                if (getScrollX() <= 0) {
                    diffy = -getScrollX();
                    isEnd = true;
                }

                if (!mScroller.isFinished()) {
                    scrollBy(diffy, 0);
                }
                initY = currY;
            }

            if (!isEnd) {
                post(this);
            }
        }

        /**
         * 进行停止
         */
        void stop() {
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
        }
    }
}


