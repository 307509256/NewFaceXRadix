package com.yxdz.commonlib.util;


/**
 * 防止用户重复点击
 */
public class NoDoubleClick {

    private static long lastClickTime;


    public static boolean isFastDoubleClick(long mLong) {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < mLong) {
            lastClickTime = time;
            return true;
        } else {
            lastClickTime = time;
        }
        return false;

    }

}
