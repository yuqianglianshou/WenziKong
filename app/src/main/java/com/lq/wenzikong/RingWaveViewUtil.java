package com.lq.wenzikong;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author : lq
 * @date : 2020/3/26
 * @desc : 一些方法
 */
public class RingWaveViewUtil {
    private static final String TAG = "RingWaveViewUtil";
    /**
     * 所做事情：
     * 1，根据view的大小、屏幕分辨率，确定文字生成点阵的大小；
     * 2，随机给出一个view上合适的位置放置文字，保证每个汉字显示在view的不同位置
     * 3，返回计算后所有文本的点阵
     * 以 文本为 "问世间情为何物直教人生死相许"  解释返回值：
     * ArrayList<ArrayList> list_text   ：list_text.size() = 14,汉字的个数；
     * list_text[0]  代表 "问" 这个字的点阵数据（已经计算好位置、大小）
     *
     * @param context
     * @param viewWidth  view 宽度
     * @param viewHeight view 高度
     * @param text       想要显示的汉字
     * @return 返回计算后所有文本的点阵
     */
    public static ArrayList<ArrayList> text2Data(Context context, int viewWidth, int viewHeight, String text) {

        /**
         * 根据屏幕密度放大一定倍数
         */
        //*****  确定放大倍数******** 开始
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        Log.i(TAG, " 屏幕密度 metrics.density== " + metrics.density);
        float times = metrics.density * 6;
        //字体边长 24*times，view宽度 getWidth(),view长度 getHeight()
        //判断view是否有能容下一个汉字的位置
        if (24 * times > viewWidth || 24 * times > viewHeight) {
            //降低放大倍数
            times = times / 2;
            if (24 * times > viewWidth || 24 * times > viewHeight) {
                Log.d(TAG, " RingWaveView太小，容不下一个字");
                times = 1;
            }
        }
        //*****  确定放大倍数******** 结束

        //所有文本的点阵
        ArrayList<ArrayList> list_text = new ArrayList();
        Font24 font24 = new Font24(context);
        PointOrigin startPoint;
        boolean[][] arr;
        ArrayList<PointOrigin> listSingleWord;

        //循环遍历所有汉字，拿到每个汉字的点阵信息
        for (int k = 0; k < text.length(); k++) {
            //随机获取一个起始点坐标
            startPoint = getStartPoint(viewWidth, viewHeight, times);

            arr = font24.getSingleWordArr(text.substring(k, k + 1));
            listSingleWord = new ArrayList();
            for (int i = 0; i < arr.length; i++) {
                for (int j = 0; j < arr[i].length; j++) {
                    if (arr[i][j]) {
                        float a = startPoint.x + j * startPoint.times;
                        float b = startPoint.y + i * startPoint.times;
                        listSingleWord.add(new PointOrigin(a, b));
                    }
                }
            }
            //添加一个字的点阵
            list_text.add(listSingleWord);
        }

        return list_text;
    }

    /**
     * 返回一个合适的随机起点（左上角）坐标
     * 汉字的起始位置
     *
     * @param viewWidth  view 宽度
     * @param viewHeight view 高度
     * @param times      字体点阵放大倍数
     * @return 返回一个合适的随机起点（左上角）坐标 和 汉字的放大倍数
     */
    public static PointOrigin getStartPoint(int viewWidth, int viewHeight, float times) {

        //起点坐标 x 取值范围 0 ～ getWidth()-24 * times
        //起点坐标 y 取值范围 0 ～ getHeight()-24 * times
        float startx = (float) Math.random() * (viewWidth - 24 * times);
        float starty = (float) Math.random() * (viewHeight - 24 * times);
        return new PointOrigin(startx, starty, times);
    }

    /**
     * 获取一个随机颜色
     */
    public static int getColor() {
        //红，黄，绿，蓝，蓝绿，洋红
        int[] colors = new int[]
                {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA};
        return colors[(int) (Math.random() * colors.length)];
    }

    /**
     * 点的坐标
     */
    static class PointOrigin {
        public float x;
        public float y;
        //获取起点坐标有用到这个参数
        private float times = 1;

        public PointOrigin(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public PointOrigin(float x, float y, float times) {
            this.x = x;
            this.y = y;
            this.times = times;
        }
    }
    /**
     * 定义一个波浪/圆环
     *
     * @author lq
     */
    static class Wave {
        //圆心
        public PointOrigin origin;
        //半径
        public float cr = 0;
        //画笔
        public Paint paint;

        public Wave(PointOrigin origin) {
            this.origin = origin;
            Paint paint = new Paint();
            paint.setColor(RingWaveViewUtil.getColor());
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            this.paint = paint;
        }
    }
}
