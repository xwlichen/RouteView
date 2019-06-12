package com.smart.view.routeview;

/**
 * @date : 2019-05-27 11:55
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class RouteBean {

    private int left;
    private int top;
    private int right;
    private int bottom;


    public RouteBean() {
    }

    public RouteBean(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }
}
