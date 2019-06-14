package com.smart.view.routeview;

/**
 * @date : 2019-05-27 11:55
 * @author: lichen
 * @email : 1960003945@qq.com
 * @description :
 */
public class RouteBean {

    private int itemLeft;
    private int itemTop;
    private int itemRight;
    private int itemBottom;


    private int labTextLeft;
    private int labTextTop;

    private int labRectLeft;
    private int labRectTop;
    private int labRectRight;
    private int labRectBottom;

    private int[] labTranDot1;
    private int[] labTranDot2;
    private int[] labTranDot3;

    private int titleLeft;
    private int titleTop;



    private String title;
    private String label;
    private String imgUrl;
    private boolean isFree;
    private boolean isLearning;


    public RouteBean() {
    }

    public RouteBean(int itemLeft, int itemTop, int itemRight, int itemottom) {
        this.itemLeft = itemLeft;
        this.itemTop = itemTop;
        this.itemRight = itemRight;
        this.itemBottom = itemottom;
    }

    public int getItemLeft() {
        return itemLeft;
    }

    public void setItemLeft(int itemLeft) {
        this.itemLeft = itemLeft;
    }

    public int getItemTop() {
        return itemTop;
    }

    public void setItemTop(int itemTop) {
        this.itemTop = itemTop;
    }

    public int getItemRight() {
        return itemRight;
    }

    public void setItemRight(int itemRight) {
        this.itemRight = itemRight;
    }

    public int getItemBottom() {
        return itemBottom;
    }

    public void setItemBottom(int itemBottom) {
        this.itemBottom = itemBottom;
    }

    public int getLabTextLeft() {
        return labTextLeft;
    }

    public void setLabTextLeft(int labTextLeft) {
        this.labTextLeft = labTextLeft;
    }

    public int getLabTextTop() {
        return labTextTop;
    }

    public void setLabTextTop(int labTextTop) {
        this.labTextTop = labTextTop;
    }

    public int getLabRectLeft() {
        return labRectLeft;
    }

    public void setLabRectLeft(int labRectLeft) {
        this.labRectLeft = labRectLeft;
    }

    public int getLabRectTop() {
        return labRectTop;
    }

    public void setLabRectTop(int labRectTop) {
        this.labRectTop = labRectTop;
    }

    public int getLabRectRight() {
        return labRectRight;
    }

    public void setLabRectRight(int labRectRight) {
        this.labRectRight = labRectRight;
    }

    public int getLabRectBottom() {
        return labRectBottom;
    }

    public void setLabRectBottom(int labRectBottom) {
        this.labRectBottom = labRectBottom;
    }

    public int[] getLabTranDot1() {
        return labTranDot1;
    }

    public void setLabTranDot1(int[] labTranDot1) {
        this.labTranDot1 = labTranDot1;
    }

    public int[] getLabTranDot2() {
        return labTranDot2;
    }

    public void setLabTranDot2(int[] labTranDot2) {
        this.labTranDot2 = labTranDot2;
    }

    public int[] getLabTranDot3() {
        return labTranDot3;
    }

    public void setLabTranDot3(int[] labTranDot3) {
        this.labTranDot3 = labTranDot3;
    }

    public int getTitleLeft() {
        return titleLeft;
    }

    public void setTitleLeft(int titleLeft) {
        this.titleLeft = titleLeft;
    }

    public int getTitleTop() {
        return titleTop;
    }

    public void setTitleTop(int titleTop) {
        this.titleTop = titleTop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }

    public boolean isLearning() {
        return isLearning;
    }

    public void setLearning(boolean learning) {
        isLearning = learning;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
