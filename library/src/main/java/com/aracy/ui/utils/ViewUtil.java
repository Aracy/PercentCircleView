package com.aracy.ui.utils;

import android.view.View;

/***
 * 圆形百分比控件
 *
 * @author aracy
 * @version [1.0, 2017-03-17]
 */
public class ViewUtil {

    private ViewUtil(){
        throw new AssertionError("util class");
    }

    /**
     * 选择自定义view合理的长度
     * @param spec The measureSpec
     * @param desired 对应边的padding和
     * @param min 对应边的最小值
     * @return 边长
     */
    public static int chooseSize(int spec, int desired, int min){
        final int mode = View.MeasureSpec.getMode(spec);
        final int size = View.MeasureSpec.getSize(spec);
        switch (mode) {
            case View.MeasureSpec.EXACTLY:
                return size;
            case View.MeasureSpec.AT_MOST:
                return Math.min(size, Math.max(desired, min));
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return Math.max(desired, min);
        }
    }



}
