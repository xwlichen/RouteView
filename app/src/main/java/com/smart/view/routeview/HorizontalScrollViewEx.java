package com.smart.view.routeview;

/**
 * @date : 2019-05-27 16:39
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 可以横向滚动的viewGroup，兼容纵向滚动的子view
 */
public class HorizontalScrollViewEx extends ViewGroup {

    //第一步，定义一个追踪器引用
    private VelocityTracker mVelocityTracker;//滑动速度追踪器


    public HorizontalScrollViewEx(Context context) {
        this(context, null);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalScrollViewEx(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
        //初始化追踪器
        mVelocityTracker = VelocityTracker.obtain();//获得追踪器对象，这里用obtain，按照谷歌的尿性，应该是考虑了对象重用
    }

    int childCount;

    /**
     * 确定每一个子view的宽高
     * <p>
     * 如果是逐个去测量子view的话，必须在测量之后，调用setMeasuredDimension来设置宽高
     * <p>
     * 这里测量出来的宽高，会在onLayout中用来作为参考
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//spec 测量模式，

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        childCount = getChildCount();
        measureChildren(widthMeasureSpec, heightMeasureSpec);//逐个测量所有的子view

        if (childCount == 0) {//如果子view数量为0，
            setMeasuredDimension(0, 0);//那么整个viewGroup宽高也就是0
        } else if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {//如果viewGroup的宽高都是matchParent
            width = childCount * getChildAt(0).getMeasuredWidth();// 那么,本viewGroup的宽，就是index为0的子view的测量宽度 乘以 子view的个数
            height = getChildAt(0).getMeasuredHeight();//高，就是子view的高
            setMeasuredDimension(width, height);//用子view的宽高，来设定
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = childCount * getChildAt(0).getMeasuredWidth();
            setMeasuredDimension(width, height);
        } else {
            height = getChildAt(0).getMeasuredHeight();
            setMeasuredDimension(width, height);
            Log.d("setMeasuredDimension", "" + width);
        }
    }

    /**
     * 这个方法用于，处理布局所有的子view，让他们按照代码写的规则去排布
     *
     * @param changed
     * @param l       left，当前viewGroup的左边线距离父组件左边线的距离
     * @param t       top，当前viewGroup的上边线距离父组件上边线的距离
     * @param r       right，当前viewGroup的左边线距离父组件右边线的距离
     * @param b       bottom，当前viewGroup的上边线距离父组件下边线的距离
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d("onLayout", ":" + l + "-" + t + "-" + r + "-" + b);
        int count = getChildCount();
        int offsetX = 0;
        for (int i = 0; i < count; i++) {
            int w = getChildAt(i).getMeasuredWidth();
            int h = getChildAt(i).getMeasuredHeight();
            Log.d("onLayout", "w:" + w + " - h:" + h);

            getChildAt(i).layout(offsetX + l, t, offsetX + l + w, b);//保证每次都最多只完整显示一个子view,因为在onMeasure中，已经将子view的宽度设置为了 本viewGroup的宽度
            offsetX += w;//每次的偏移量都递增
        }
    }


    private float lastInterceptX, lastInterceptY;

    /**
     * 事件的拦截，
     *
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean ifIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastInterceptX = event.getRawX();
                lastInterceptY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //检查是横向移动的距离大，还是纵向
                float xDistance = Math.abs(lastInterceptX - event.getRawX());
                float yDistance = Math.abs(lastInterceptY - event.getRawY());
                if (xDistance > yDistance) {
                    ifIntercept = true;
                } else {
                    ifIntercept = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return ifIntercept;
    }

    private float downX;
    private float distanceX;
    private boolean isFirstTouch = true;
    private int childIndex = -1;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int scrollX = getScrollX();//控件的左边界，与屏幕原点的X轴坐标
        int scrollXMax = (getChildCount() - 1) * getChildAt(1).getMeasuredWidth();
        final int childWidth = getChildAt(0).getWidth();
        mVelocityTracker.addMovement(event);//在onTouchEvent这里，截取event对象
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //先让你滑动起来
                float moveX = event.getRawX();
                if (isFirstTouch) {//一次事件序列，只会赋值一次？
                    downX = moveX;
                    isFirstTouch = false;
                }
                Log.d("distanceX", "" + downX + "|" + moveX + "|" + distanceX);
                distanceX = downX - moveX;

                //判定是否可以滑动
                //这里有一个隐患，由于不知道Move事件，会以什么频率来分发，所以，这里多少都会出现一点误差
                if (getChildCount() >= 2) {//子控件在2个或者2个以上时，才有下面的效果
                    //如果命令是向左滑动,distanceX>0 ，那么判断命令是否可以执行
                    //如果命令是向右滑动,distanceX<0 ，那么判断命令是否可以执行
                    Log.d("scrollX", "scrollX:" + scrollX);
                    if (distanceX <= 0) {
                        if (scrollX >= 0)
                            scrollBy((int) distanceX, 0);//滑动
                    } else {
                        if (scrollX <= scrollXMax)
                            scrollBy((int) distanceX, 0);//滑动
                    }
                }//如果只有一个，则不允许滑动，防止bug
                break;
            case MotionEvent.ACTION_UP:// 当手指松开的时候，要显示某一个完整的子view
                mVelocityTracker.computeCurrentVelocity(1000, configuration.getScaledMaximumFlingVelocity());//计算，最近的event到up之间的速率
                float xVelocity = mVelocityTracker.getXVelocity();//当前横向的移动速率
                float edgeXVelocity = configuration.getScaledMinimumFlingVelocity();//临界点
                childIndex = (scrollX + childWidth / 2) / childWidth;//整除的方式，来确定X轴应该所在的单元，将每一个item的竖向中间线定为滑动的临界线
                if (Math.abs(xVelocity) > edgeXVelocity) {//如果当前横向的速率大于零界点，
                    childIndex = xVelocity > 0 ? (childIndex - 1) : (childIndex + 1);//xVelocity正数，表示从左往右滑，所以child应该是要显示前面一个
                }
//                childIndex = Math.min(getChildCount() - 1, Math.max(childIndex, 0));//不可以超出左右边界,这种写法可能很难一眼看懂，那就替换成下面的写法
                if (childIndex < 0)//计算出的childIndex可能是负数。那就赋值为0
                    childIndex = 0;
                else if (childIndex >= getChildCount()) {//也有可能超出childIndex的最大值，那就赋值为最大值-1
                    childIndex = getChildCount() - 1;
                }
                smoothScrollBy(childIndex * childWidth - scrollX, 0);// 回滚的距离
                mVelocityTracker.clear();
                isFirstTouch = true;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        downX = event.getRawX();
        return super.onTouchEvent(event);
    }

    //实现平滑地回滚

    /**
     * 最叼的还是这个方法，平滑地回滚，从当前位置滚到目标位置
     * @param dx
     * @param dy
     */
    void smoothScrollBy(int dx, int dy) {
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 500);//从当前滑动的位置，平滑地过度到目标位置
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    private Scroller mScroller;//这个scroller是为了平滑滑动
}
