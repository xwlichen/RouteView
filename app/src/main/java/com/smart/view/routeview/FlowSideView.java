package com.smart.view.routeview;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * @date : 2019-06-13 10:26
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class FlowSideView extends View {
    private Context context;
    private int width;
    private int height;

    private Paint paint;
    private int paintColor = Color.WHITE;

    private int boundSize = 10;
    private float padding = 10;


    private Bitmap bgBitmap;


    private int borderRadius;                  //圆角大小

    private Typeface typeface;

    private int rectLeft;
    private int rectTop;
    private int rectRight;
    private int rectBottom;
    private int rectPadding = 10;
    private int rectX;
    private int rectY;

    private int textSize = 50;

    private float baseline;

    private Bitmap tagBitmap;
    private int tagWidth;
    private int tagHeight;


    public FlowSideView(Context context) {
        super(context);
        init(context);
    }

    public FlowSideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public FlowSideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }


    protected void init(Context context) {
        this.context = context;


        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(paintColor);
        paint.setStrokeWidth(10);
        paint.setStrokeJoin(Paint.Join.ROUND);

        borderRadius = 20;

        AssetManager aManager = context.getApplicationContext().getAssets();
        typeface = Typeface.createFromAsset(aManager, "fonts/font_65s.otf");
        paint.setTextSize(textSize);
        paint.setTypeface(typeface);
        paint.setTextAlign(Paint.Align.CENTER);
        computeRectPadding("试听");

        tagBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_green_circle_true);
        tagWidth=tagBitmap.getWidth();
        tagHeight=tagBitmap.getHeight();


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
        Log.e("flow", "onMeasure");

        if (width<=0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            Log.e("flow", "width:" + width + "height:" + height);
            width = measure(widthMeasureSpec);
            height = measure(heightMeasureSpec);
        }else{
            setMeasuredDimension(width,height);
        }

//        bgBitmap = getBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.test), width, height);


    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (width > 0&&bgBitmap!=null) {
            drawBigBound(canvas);
            canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), 255, Canvas.ALL_SAVE_FLAG);
            paint.setColor(Color.RED);
            drawCutBound(canvas);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bgBitmap, 0, 0, paint);
            paint.setXfermode(null);

            paint.setColor(Color.BLUE);
            canvas.drawRoundRect(new RectF(rectLeft, rectTop, rectRight, rectBottom), 10, 10, paint);

            paint.setColor(Color.WHITE);
            canvas.drawText("试听", rectX, baseline, paint);

            canvas.drawBitmap(tagBitmap,width-tagWidth,height-tagHeight,null);

        }
    }


    protected void drawCutBound(Canvas canvas) {

        canvas.drawRoundRect(new RectF(boundSize, borderRadius + boundSize, getWidth() - boundSize, getHeight() - borderRadius - boundSize), borderRadius, borderRadius, paint);
        canvas.drawRoundRect(new RectF(borderRadius + boundSize, boundSize, getWidth() - borderRadius - boundSize, getHeight() - boundSize), borderRadius, borderRadius, paint);


    }


    protected void drawBigBound(Canvas canvas) {

        canvas.drawRoundRect(new RectF(0, borderRadius, getWidth(), getHeight() - borderRadius), borderRadius, borderRadius, paint);
        canvas.drawRoundRect(new RectF(borderRadius, 0, getWidth() - borderRadius, getHeight()), borderRadius, borderRadius, paint);

    }


    private Bitmap getBitmap(Bitmap bitmap, int newW, int newH) {

        Matrix matrix = new Matrix();//创建一个处理图片的类
        int width = bitmap.getWidth();//获取图片本身的大小(宽)
        int height = bitmap.getHeight();//获取图片本身的大小(高)

        float wS = (float) newW / width;//缩放比---->这块注意这个是新的宽度/高度除以旧的宽度
        float hS = (float) newH / height;//缩放比---->这块注意这个是新的宽度/高度除以旧的宽度
        matrix.postScale(wS, hS);//这块就是处理缩放的比例
        //matrix.setScale(sX,sY);//缩放图片的质量sX表示宽0.5就代表缩放一半,sX同样

        Bitmap bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);//创建一个新的图像
        bitmap.recycle();

        return bitmapResult;
    }

    private void computeRectPadding(String str) {
        Rect rect = new Rect();
        paint.getTextBounds(str, 0, str.length(), rect);
        int textWidth = rect.width();
        int textHeight = rect.height();

        rectLeft = boundSize;
        rectTop = borderRadius * 2 + boundSize;
        rectRight = rectLeft + textWidth + rectPadding * 2;
        rectBottom = rectTop + textHeight + rectPadding * 2;


        rectX = (rectLeft + rectRight) / 2;
        rectY = (rectTop + rectBottom) / 2;


        //计算baseline
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        baseline = rectY + distance;
    }


    public void setSize(int width,int height){
        this.width=width;
        this.height=height;
        bgBitmap = getBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.test), width, height);

        requestLayout();
        Log.e("flow", "setSize");

//        invalidate();
    }
}
