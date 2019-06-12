package com.smart.view.routeview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

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



        RouteView routeView=findViewById(R.id.routeView);
//        routeView.setVisibility(View.GONE);

        routeView.setOnScrollerListener(new OnScrollerListener() {
            @Override
            public void onScroll(int x, int y, int lastX, int lastY) {
//                move(x,y,lastX,lastY);
            }
        });
        ivBg=findViewById(R.id.ivBg);
        Bitmap bm = mDecoder.decodeRegion(mRect, options);
        ivBg.setImageBitmap(bm);

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
