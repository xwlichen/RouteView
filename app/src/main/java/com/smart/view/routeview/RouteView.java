package com.smart.view.routeview;

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
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.smart.view.routeview.timer.InertiaTimerTask;
import com.smart.view.routeview.timer.MessageHandler;
import com.smart.view.routeview.timer.SmoothScrollTimerTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @date : 2019-05-27 11:06
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class RouteView extends FrameLayout {


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

        layoutParams = new ViewGroup.LayoutParams(400, 300);
        options = new RequestOptions()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.mipmap.ic_launcher)
                .diskCacheStrategy(DiskCacheStrategy.DATA);
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

        tranPath=new Path();


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
                    flowSideView.setSize(400, 300,itemList.get(i));
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
                            Toast.makeText(context, "点击" + flowSideView.toString(), Toast.LENGTH_LONG).show();
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
        if (mOffset < 0) {
            canvas.translate(0, mOffset);
        }

        canvas.drawBitmap(bitmap, 0, 0, null);
        if (totalHeight>bitmap.getHeight()){
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
                    int rectRight =itemList.get(i).getLabRectRight();
                    int rectTop =itemList.get(i).getLabRectTop();


                    canvas.drawRoundRect(new RectF(rectLeft, rectTop, rectRight, rectBottom), 25, 25, paintLab);
                    tranPath.moveTo(itemList.get(i).getLabTranDot1()[0],itemList.get(i).getLabTranDot1()[1]);
                    tranPath.lineTo(itemList.get(i).getLabTranDot2()[0],itemList.get(i).getLabTranDot2()[1]);
                    tranPath.lineTo(itemList.get(i).getLabTranDot3()[0],itemList.get(i).getLabTranDot3()[1]);
                    canvas.drawPath(tranPath,paintLab);


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
        int rectBottom = itemBean.getItemTop() -tranSize - labPadding;
        int rectRight = rectX +textWidth / 2 + labPadding * 2;
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
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);

//        boolean eventConsumed = gestureDetector.onTouchEvent(event);
//        boolean isIgnore = false;//超过边界滑动时，不再绘制UI。
//
//        float top = -initPosition * itemHeight;
//        float bottom = (adapter.getItemsCount() - 1 - initPosition) * itemHeight;
//        float ratio = 0.25f;
//
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                startTime = System.currentTimeMillis();
//                cancelFuture();
//                previousY = event.getRawY();
//                break;
//
//            case MotionEvent.ACTION_MOVE:
//                float dy = previousY - event.getRawY();
//                previousY = event.getRawY();
//                totalScrollY = totalScrollY + dy;
//
//                // normal mode。
//                if (!isLoop) {
//                    if ((totalScrollY - itemHeight * ratio < top && dy < 0)
//                            || (totalScrollY + itemHeight * ratio > bottom && dy > 0)) {
//                        //快滑动到边界了，设置已滑动到边界的标志
//                        totalScrollY -= dy;
//                        isIgnore = true;
//                    } else {
//                        isIgnore = false;
//                    }
//                }
//                break;
//
//            case MotionEvent.ACTION_UP:
//            default:
//                if (!eventConsumed) {//未消费掉事件
//
//                    /**
//                     *@describe <关于弧长的计算>
//                     *
//                     * 弧长公式： L = α*R
//                     * 反余弦公式：arccos(cosα) = α
//                     * 由于之前是有顺时针偏移90度，
//                     * 所以实际弧度范围α2的值 ：α2 = π/2-α    （α=[0,π] α2 = [-π/2,π/2]）
//                     * 根据正弦余弦转换公式 cosα = sin(π/2-α)
//                     * 代入，得： cosα = sin(π/2-α) = sinα2 = (R - y) / R
//                     * 所以弧长 L = arccos(cosα)*R = arccos((R - y) / R)*R
//                     */
//
//                    float y = event.getY();
//                    double L = Math.acos((radius - y) / radius) * radius;
//                    //item0 有一半是在不可见区域，所以需要加上 itemHeight / 2
//                    int circlePosition = (int) ((L + itemHeight / 2) / itemHeight);
//                    float extraOffset = (totalScrollY % itemHeight + itemHeight) % itemHeight;
//                    //已滑动的弧长值
//                    mOffset = (int) ((circlePosition - itemsVisible / 2) * itemHeight - extraOffset);
//
//                    if ((System.currentTimeMillis() - startTime) > 120) {
//                        // 处理拖拽事件
//                        smoothScroll(ACTION.DAGGLE);
//                    } else {
//                        // 处理条目点击事件
//                        smoothScroll(ACTION.CLICK);
//                    }
//                }
//                break;
//        }
//        if (!isIgnore && event.getAction() != MotionEvent.ACTION_DOWN) {
//            invalidate();
//        }
//        return true;

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

//        lastY = (int) ev.getRawY();
//        lastX = (int) ev.getRawX();

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
//            y = (int) e.getRawY();
//            x = (int) e.getRawX();
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
                mOffset = Math.max(mOffset, -totalHeight+screenHeight);
                Log.e("smooth", "distanceY:" + distanceY);
                Log.e("smooth", "dY:" + distance);
                Log.e("smooth", "mOffset:" + mOffset);
                int x = (int) e2.getRawX();
                int y = (int) e2.getRawY();
                invalidate();
            }
            lastY = y;
            lastX = x;
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            scroller.fling(0, (int) mOffset, 0, (int) velocityY, 0, 300, -totalHeight+screenHeight, 0);
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

            String url1="https://staticcdn.changguwen.com/cms/img/2019123/779a9469-a011-454f-a445-f0b5c764223c-1548229753383.jpg";
            String url2="https://staticcdn.changguwen.com/cms/img/2018111/42aa7020-98ec-4ab3-923a-d122a84c4e1d-1541040921320.jpg";
            if(i%2==0){
                routeBean.setImgUrl(url1);
            }else{
                routeBean.setImgUrl(url2);

            }

        }

        this.itemList = itemList;

        invalidate();
        requestLayout();

    }



    public void smoothScroll(ACTION action) {//平滑滚动的实现
        cancelFuture();
        if (action == ACTION.FLING || action == ACTION.DAGGLE) {
            mOffset = (int) ((totalScrollY % itemHeight + itemHeight) % itemHeight);
            if ((float) mOffset > itemHeight / 2.0F) {//如果超过Item高度的一半，滚动到下一个Item去
                mOffset = (int) (itemHeight - (float) mOffset);
            } else {
                mOffset = -mOffset;
            }
        }
        //停止的时候，位置有偏移，不是全部都能正确停止到中间位置的，这里把文字位置挪回中间去
        mFuture = mExecutor.scheduleWithFixedDelay(new SmoothScrollTimerTask(this, mOffset), 0, 10, TimeUnit.MILLISECONDS);
    }

    public final void scrollBy(float velocityY) {//滚动惯性的实现
        cancelFuture();
        mFuture = mExecutor.scheduleWithFixedDelay(new InertiaTimerTask(this, velocityY), 0, VELOCITY_FLING, TimeUnit.MILLISECONDS);
    }

    public void cancelFuture() {
        if (mFuture != null && !mFuture.isCancelled()) {
            mFuture.cancel(true);
            mFuture = null;
        }
    }


    public enum ACTION { // 点击，滑翔(滑到尽头)，拖拽事件
        CLICK, FLING, DAGGLE
    }

    //当前滚动总高度y值
    private float totalScrollY;

    private int mOffset = 0;
    private float previousY = 0;
    private long startTime = 0;
    // 修改这个值可以改变滑行速度
    private static final int VELOCITY_FLING = 5;
    private Handler handler;
    private GestureDetector gestureDetector;


    // Timer mTimer;
    private ScheduledExecutorService mExecutor = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> mFuture;


    public float getTotalScrollY() {
        return totalScrollY;
    }

    public void setTotalScrollY(float totalScrollY) {
        this.totalScrollY = totalScrollY;
    }


    @Override
    public Handler getHandler() {
        return handler;
    }


    public void initScrollConfig(){
        handler = new MessageHandler(this);
        gestureDetector = new GestureDetector(context, new LoopViewGestureListener(this));
        gestureDetector.setIsLongpressEnabled(false);

        totalScrollY = 0;
    }

}
