package com.smart.view.routeview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ImageView ivBg;

    private BitmapRegionDecoder mDecoder;
    private BitmapFactory.Options options;
    /**
     * 图片的宽度和高度
     */
    private int imageWidth, imageHeight;

    /**
     * 绘制的区域
     */
    private volatile Rect mRect = new Rect();

    private ViewGroup.LayoutParams layoutParams;


    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        //获取图片的宽高
        InputStream is = null;
        try {
            is = getResources().getAssets().open("timg.jpg");
            //初始化BitmapRegionDecode，并用它来显示图片
            //如果在decodeStream之前使用is，会导致出错
            // 此时流的起始位置已经被移动过了，需要调用is.reset()来重置，然后再decodeStream(imgInputStream, null, options)
            mDecoder = BitmapRegionDecoder
                    .newInstance(is, false);

            BitmapFactory.Options tmpOptions = new BitmapFactory.Options();
            // Grab the bounds for the scene dimensions
            tmpOptions.inJustDecodeBounds = true;

            is.reset();

            BitmapFactory.decodeStream(is, null, tmpOptions);
            imageWidth = tmpOptions.outWidth;
            imageHeight = tmpOptions.outHeight;


            //默认显示图片的中心区域
            mRect.left = 0;
            mRect.top = 0;
            mRect.right = TDevice.getScreenWidth(this);
            mRect.bottom = TDevice.getScreenHeight(this);

            Log.e("smooth", "width:" + imageWidth + ",height:" + imageHeight);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FlowSideView flowSizeView=findViewById(R.id.flowSizeView);
        String url1="https://staticcdn.changguwen.com/cms/img/2019123/779a9469-a011-454f-a445-f0b5c764223c-1548229753383.jpg";
        ImageView ivView=findViewById(R.id.ivView);

        RequestOptions options = new RequestOptions();
        options.error(R.mipmap.ic_launcher);
        options.placeholder(R.mipmap.ic_launcher);
        Glide.with(this).load(url1).apply(options).into(ivView);
        Glide.with(this).load(url1).apply(options).into(flowSizeView);

//        final FlowSideView flowSideView = new FlowSideView(this);
//        layoutParams=new ViewGroup.LayoutParams(200,150);
//
//        flowSideView.setLayoutParams(layoutParams);
//        root.addView(flowSideView);


        RouteView routeView=findViewById(R.id.routeView);
        routeView.setData(new ArrayList<RouteBean>());
//        routeView.setVisibility(View.GONE);



    }



    /**
     * 移动的时候更新图片显示的区域
     *
     * @param x
     * @param y
     */
    private void move(int x, int y,int lastX,int lastY) {


        int deltaX = x - lastX;
        int deltaY = y - lastY;
        Log.d("smmoth", "move, deltaX:" + deltaX + " deltaY:" + deltaY);
//        //如果图片宽度大于屏幕宽度
//        if (imageWidth > ivBg.getWidth()) {
//            //移动rect区域
//            mRect.offset(-deltaX, 0);
//            //检查是否到达图片最右端
//            if (mRect.right > imageWidth) {
//                mRect.right = imageWidth;
//                mRect.left = imageWidth - ivBg.getWidth();
//            }
//
//            //检查左端
//            if (mRect.left < 0) {
//                mRect.left = 0;
//                mRect.right = ivBg.getWidth();
//            }
//
//        }
//        //如果图片高度大于屏幕高度
//        if (imageHeight > ivBg.getHeight()) {
            mRect.offset(0, -deltaY);

            //是否到达最底部
            if (mRect.bottom > imageHeight) {
                mRect.bottom = imageHeight;
                mRect.top = imageHeight - ivBg.getHeight();
            }

            if (mRect.top < 0) {
                mRect.top = 0;
                mRect.bottom = ivBg.getHeight();
            }

//        }

        Bitmap bm = mDecoder.decodeRegion(mRect, options);
        ivBg.setImageBitmap(bm);

    }
}
