package com.smart.view.routeview;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;


/**
 * Created by zyt on 2018/8/9.
 * TextView工具类
 */

public class TextViewUtils {
    private static TextViewUtils textViewUtils;

    public static TextViewUtils init() {
        if (textViewUtils == null) {
            synchronized (TextViewUtils.class) {
                if (textViewUtils == null) {
                    textViewUtils = new TextViewUtils();
                }
            }
        }
        return textViewUtils;
    }

    /**
     * 动态设置字体大小
     *
     * @param context
     * @param textView
     * @param size     R.dimen.xxxx
     */
    public void setTextViewTextSize(Context context, TextView textView, int size) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getApplicationContext().getResources().getDimension(size));
    }


    /**
     * 获取dp的值===动态设置宽高
     *
     * @param dpValue R.dimen.xxxx
     * @return
     */
    public int getDpValue(Context context, int dpValue) {
        return context.getApplicationContext().getResources().getDimensionPixelOffset(dpValue);
    }

    /**
     * 获取Typeface===动态设置字体
     *
     * @param name dfgb_y7.ttf
     * @return
     */
    public Typeface getTypeface(Context context, String name) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/" + name);
    }

    /**
     * dp 2 px
     *
     * @param dpVal 直接传数字
     */
    public int dp2px(Context ctx, int dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, ctx.getResources().getDisplayMetrics());
    }

    public int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }


}
