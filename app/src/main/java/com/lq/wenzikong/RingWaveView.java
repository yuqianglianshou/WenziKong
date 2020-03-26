package com.lq.wenzikong;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.lq.wenzikong.RingWaveViewUtil.PointOrigin;
import com.lq.wenzikong.RingWaveViewUtil.Wave;

import java.util.ArrayList;


/**
 * 文字水波纹效果
 * 原理：传入文字位置（点阵）集合，draw圆环，半径以0开始递增，透明度以255开始递减，颜色随机，每一次刷新半径扩大一点，透明度降低一些，
 * 当透明度为0时，集合中删除点，集合为空时停止刷新，动画停止。
 *
 * @author lq
 */
public class RingWaveView extends View implements LifecycleObserver {
    private static String TAG = "RingWaveView";
    private static String text = "问世间情为何物直教人生死相许";
    /**
     * 记录当前执行到哪个字了，圆环对象集合的位置
     */
    private int pos = 0;
    private ArrayList<ArrayList> list_text;
    private Context context;
    private int viewWidth, viewHeight;
    /**
     * 2个波浪之间的最小距离
     */
    static double DIS_SOLP = 8;

    /**
     * 圆环对象集合(点的集合，每个点都是一个圆环)
     */
    private ArrayList<Wave> wList;
    /**
     * 保证此处初始代码只执行一次
     */
    private boolean isFirst = true;

    public RingWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (wList == null) {
            wList = new ArrayList<Wave>();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                //循环数据更新并刷新界面
                case 0:
                    //循环动画
                    Log.i(TAG, "handleMessage: 刷新");
                    if (!handler.hasMessages(0)) {
                        //刷新数据
                        flushData();
                        //刷新页面
                        invalidate();
                        /*
                         * 如果集合不为控，继续刷新动画
                         */
                        if (wList.size() > 0) {
                            handler.sendEmptyMessageDelayed(0, 200L);
                        }
                    }

                    break;

                case 1:
                    if (pos < list_text.size()) {
                        addPointList(list_text.get(pos));
                        Log.i(TAG, "handleMessage: 添加 pos = " + pos);
                        pos += 1;
                        handler.sendEmptyMessageDelayed(1, 3000L);
                    } else {
                        //执行完毕,指针归0
                        pos = 0;
                    }

                    break;
                default:
                    break;
            }


        }

        ;
    };


    @Override
    protected void onDraw(Canvas canvas) {
        Wave wave;
        for (int i = 0; i < wList.size(); i++) {
            wave = wList.get(i);
            //圆心，半径，画笔，画出集合中所有圆环
            canvas.drawCircle(wave.origin.x, wave.origin.y, wave.cr, wave.paint);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    private void OnResume() {
        Log.i(TAG, "OnResume: ");
        if (!isFirst) {
            //没执行完继续执行，执行完重新执行一次
            handler.sendEmptyMessage(1);
        }

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    private void OnPause() {
        Log.i(TAG, "OnPause: ");
        if (handler.hasMessages(0)) {
            handler.removeMessages(0);
        }
        if (handler.hasMessages(1)) {
            handler.removeMessages(1);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus && isFirst) {
            isFirst = false;
            //打开程序执行，执行完毕退到后台再次打开重新执行，未执行完退到后台再次打开继续上次执行
            //触摸屏幕执行
            Log.i(TAG, "onWindowFocusChanged: getWidth() == " + getWidth());
            Log.i(TAG, "onWindowFocusChanged: getHeight() == " + getHeight());
            viewWidth = getWidth();
            viewHeight = getHeight();

            //文本生成点阵
            list_text = RingWaveViewUtil.text2Data(context, viewWidth, viewHeight, text);

            //动画起点
            handler.sendEmptyMessageDelayed(1, 200L);
        }

    }

    /**
     * 刷新集合中每个圆环对象的状态
     */
    private void flushData() {
        Wave w;
        for (int i = 0; i < wList.size(); i++) {
            w = wList.get(i);
            //如果透明度为 0 从集合中删除
            int alpha = w.paint.getAlpha();
            if (alpha == 0) {
                //删除i 以后，i的值应该再减1 否则会漏掉一个对象，不过，在此处影响不大，效果上看不出来。
                wList.remove(i);
                i--;
                continue;
            }

            //透明度递减
            alpha -= 6;
            if (alpha < 160) {
                alpha -= 22;
                if (alpha < 22) {
                    //0是完全透明
                    alpha = 0;
                }
            }
            //降低透明度
            w.paint.setAlpha(alpha);
            //圆环半径递增
            w.cr = w.cr + 0.66f;
            //圆环厚度随半径递增
            w.paint.setStrokeWidth(w.cr / 2.5f);
        }

    }

    /**
     *     记录触碰屏幕最后一次添加的点。作用：使滑动屏幕时先后添加的点有一定的距离
     */
    private PointOrigin lastOrigin = new PointOrigin(0, 0);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        /**
         * 记录按下或移动的点
         * 当触摸屏幕时，记录对应的点，以点为中心生成圆环动画
         */
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                int x = (int) event.getX();
                int y = (int) event.getY();

                //如果先后添加的点有一定的距离则有效，否则无效
                if (Math.sqrt((lastOrigin.x - x) * (lastOrigin.x - x) + (lastOrigin.y - y) * (lastOrigin.y - y)) > DIS_SOLP) {
                    lastOrigin = new PointOrigin(x, y);
                    addPoint(new PointOrigin(x, y));
                }

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 添加一个点的集合到圆环集合中，并开启刷新
     *
     * @param pointList 点的集合
     */
    public void addPointList(ArrayList<PointOrigin> pointList) {
        if (pointList == null || pointList.size() == 0) {
            Log.d(TAG, "setPointList: pointList 无效");
            return;
        }
        for (int j = 0; j < pointList.size(); j++) {
            wList.add(new Wave((pointList.get(j))));
        }
        handler.sendEmptyMessage(0);
    }

    /**
     * 添加一个点
     * 添加新的波浪圆环对象，并开启刷新
     */
    private void addPoint(PointOrigin origin) {
        wList.add(new Wave((origin)));
        handler.sendEmptyMessage(0);
    }




}
