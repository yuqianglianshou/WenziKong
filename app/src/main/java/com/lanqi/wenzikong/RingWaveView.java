package com.lanqi.wenzikong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * 水波纹效果
 *
 * @author lq
 */
public class RingWaveView extends View {

    double DIS_SOLP = 8;//2个波浪之间的距离

    private ArrayList<Wave> wList;

    private boolean isRunning = false;

    public RingWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        wList = new ArrayList<Wave>();
    }

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            //刷新数据
            flushData();

            //刷新页面
            invalidate();

            //循环动画
            if (isRunning) {

                //handler.sendEmptyMessageDelayed(0, 100);//��
                handler.sendEmptyMessageDelayed(0, 200);//��
                //handler.sendEmptyMessageDelayed(0, 500);//控制刷新时间。。
            }

        }

        ;
    };


    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < wList.size(); i++) {
            Wave wave = wList.get(i);
            //圆心，半径，画笔
            canvas.drawCircle(wave.cx, wave.cy, wave.cr, wave.paint);
        }
    }

    //刷新数据
    protected void flushData() {
        for (int i = 0; i < wList.size(); i++) {

            Wave w = wList.get(i);

            //如果透明度为 0 从集合中删除
            int alpha = w.paint.getAlpha();
            //System.out.println("alpha"+alpha);
            if (alpha == 0) {
                wList.remove(i);//删除i 以后，i的值应该再减1 否则会漏掉一个对象，不过，在此处影响不大，效果上看不出来。
                i--;
                continue;
            }

            //alpha-=4;
            alpha -= 6;
            if (alpha < 170) {
                alpha -= 30;
                if (alpha < 30) {
                    alpha = 0;//0是完全透明
                }
            }

            //降低透明度
            w.paint.setAlpha(alpha);

            //扩大半径
            //w.cr = w.cr+3;
            w.cr = w.cr + 0.5f;
            //设置圆环厚度
            //w.paint.setStrokeWidth(w.cr/4);
            w.paint.setStrokeWidth(w.cr / 2.5f);
//			w.paint.setStrokeWidth(w.cr/4);
        }

		/*
         * 如果集合被清空，就停止刷新动画
		 */
        if (wList.size() == 0) {
            isRunning = false;
            //handler.sendEmptyMessage(0);这是错误
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int y = (int) event.getY();

                addPoint(x, y);

                break;
        }
        return true;//true波浪形，false 单个环形
        //return false;
    }

    /**
     * 添加新的波浪中心点
     *
     * @param x
     * @param y
     */
    void addPoint(float x, float y) {
        if (wList.size() == 0) {
            addPointView(x, y);
            //第一次触摸屏幕，启动动画
            isRunning = true;
            handler.sendEmptyMessage(0);

        } else if (wList.size() > 0) {
            Wave w = wList.get(wList.size() - 1);

            if (Math.sqrt((w.cx - x) * (w.cx - x) + (w.cy - y) * (w.cy - y)) > DIS_SOLP) {
                addPointView(x, y);
//				isRunning = true;
//				handler.sendEmptyMessage(0);
            }
        }

    }

    /**
     * 添加新的波浪
     *
     * @param x
     * @param y
     */
    int i = 0;//颜色变量

    void addPointView(float x, float y) {
        Wave w = new Wave();
        w.cx = x;
        w.cy = y;
        Paint pa = new Paint();
        pa.setColor(colors[(int) (Math.random() * colors.length)]);//随机颜色
        //pa.setColor(colors[i%colors.length]);//顺序颜色
        i++;
        pa.setAntiAlias(true);
        pa.setStyle(Style.STROKE);

        w.paint = pa;

        wList.add(w);

    }

    //颜色值
    private int[] colors = new int[]
            {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, Color.CYAN, Color.MAGENTA};
    //红，黄，绿，蓝，蓝绿，洋红

    /**
     * 定义一个波浪
     *
     * @author lq
     */
    class Wave {
        //圆心
        float cx;
        float cy;
        //半径
        float cr;
        //画笔
        Paint paint;
    }

}
