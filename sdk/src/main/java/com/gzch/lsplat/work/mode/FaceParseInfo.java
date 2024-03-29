package com.gzch.lsplat.work.mode;

import java.io.Serializable;

/**
 * Created by liangbin.yang on 2018/10/31.
 */

public class FaceParseInfo implements Serializable {

    private static final long serialVersionUID = 20190831L;

    public int left;
    public int top;
    public int right;
    public int bottom;

    public FaceParseInfo() {
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

    @Override
    public String toString() {
        return "FaceParseInfo{" +
                "left=" + left +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                '}';
    }
}
