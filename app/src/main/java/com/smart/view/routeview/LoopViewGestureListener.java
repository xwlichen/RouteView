package com.smart.view.routeview;

import android.view.MotionEvent;



/**
 * 手势监听
 */
public final class LoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    private final RouteView routeView;


    public LoopViewGestureListener(RouteView routeView) {
        this.routeView = routeView;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        routeView.scrollBy(velocityY);
        return true;
    }
}
