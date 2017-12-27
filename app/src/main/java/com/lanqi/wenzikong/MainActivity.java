package com.lanqi.wenzikong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Window;

import com.lanqi.wenzikong.utils.Font24;

import java.lang.ref.WeakReference;
import java.util.Random;

/**
 * @author lq
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "lq";

    private int w;
    private int h;
    private int h_h;//状态栏和标题栏高度
    private RingWaveView rwv;
    private MediaPlayer mp;
    private Context mContext;
    private String wenzi = "问世间情为何物 直教人生死相许";
    private char[] wenzichars;

    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> mReference;

        public MyHandler(MainActivity mainActivity) {
            mReference = new WeakReference<MainActivity>(mainActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mReference.get();
            char ch = (char) msg.obj;
            //计算字放的位置
            int point = msg.arg1;
            //顺序位置
//            point = point % 6 + 1;
            //随机位置
            point = new Random().nextInt(100) % 6 + 1;
            mainActivity.showWenZi(String.valueOf(ch), point);

            super.handleMessage(msg);
        }
    }

    private MyHandler handler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        wenzichars = wenzi.toCharArray();
        for (int i = 0; i < wenzichars.length; i++) {
            System.out.println(wenzichars[i]);
        }

        mp = MediaPlayer.create(this, R.raw.chongerfei);
        mp.setLooping(true);
        mp.start();

        rwv = (RingWaveView) findViewById(R.id.rwv);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Dimension dimen1 = getAreaOne(this);
//            Dimension dimen2 = getAreaTwo(this);
            Dimension dimen3 = getAreaThree(this);
            w = dimen1.mWidth;
            h = dimen1.mHeight;
            System.out.println(w + "###" + h);
            Log.i(TAG, "onWindowFocusChanged: " + w + "###" + h);
            h_h = h - dimen3.mHeight;
            System.out.println("状态栏和标题栏的高度" + h_h);
            Log.i(TAG, "onWindowFocusChanged: " + "状态栏和标题栏的高度" + h_h);
        }
        initArr3();
    }

    private void initArr3() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg;
                for (int i = 0; i < wenzichars.length; i++) {
                    msg = handler.obtainMessage();
                    msg.obj = wenzichars[i];
                    //存储第几个字信息
                    msg.arg1 = i;
                    handler.sendMessage(msg);
                    SystemClock.sleep(3000L);
                }
            }
        }).start();
    }


    //适配有问题

    /**
     * @param s
     * @param point 位置参数123456
     */
    private void showWenZi(String s, int point) {
        int lesser = w < h ? w : h;
        //适配  字体放大倍数
        int bei = 4;
        if (lesser < 400) {
            //320*480
            bei = 4;
        } else if (lesser >= 400 && lesser < 500) {
            //480*640  480*854
            bei = 6;
        } else if (lesser >= 500 && lesser < 700) {
            //540*960
            bei = 8;
        } else if (lesser >= 700 && lesser < 900) {
            //720*1280
            bei = 10;
        } else if (lesser >= 900 && lesser < 1200) {
            //1080*1920
            bei = 14;
        } else {
            //1440*2560
            bei = 19;
        }

        Log.i(TAG, "showWenZi: 放大倍数 = " + bei);


        //确定汉字的位置
        //手机屏幕文字 2*3  ，6个汉字。
        //文字的起始点坐标（x,y）
        int x, y;
        switch (point) {
            case 1:
                x = (w / 2 - 24 * bei) / 2;//左边距
                y = ((h - h_h) / 3 - 24 * bei) / 2;//上边距
                break;
            case 2:
                x = (w / 2 - 24 * bei) / 2 + w / 2;
                y = (h / 3 - 24 * bei) / 2;
                break;
            case 3:
                x = (w / 2 - 24 * bei) / 2;
                y = ((h - h_h) / 3 - 24 * bei) / 2 + (h - h_h) / 3;
                break;
            case 4:
                x = (w / 2 - 24 * bei) / 2 + w / 2;
                y = ((h - h_h) / 3 - 24 * bei) / 2 + (h - h_h) / 3;
                break;
            case 5:
                x = (w / 2 - 24 * bei) / 2;
                y = ((h - h_h) / 3 - 24 * bei) / 2 + (h - h_h) / 3 * 2;
                break;
            case 6:
                x = (w / 2 - 24 * bei) / 2 + w / 2;
                y = ((h - h_h) / 3 - 24 * bei) / 2 + (h - h_h) / 3 * 2;
                break;

            default:
                x = 0;
                y = 0;
                break;
        }

        Log.i(TAG, "showWenZi: point = " + point + " x = " + x + " y = " + y);
        Font24 font24 = new Font24(mContext);
        boolean[][] arr = font24.drawString(s);

        int startx = x, starty = y;
        for (int i = 0; i < 24; i++) {
            for (int j = 0; j < 24; j++) {
                float xx = (float) j;
                float yy = (float) i;
                if (arr[i][j]) {
                    float a = startx + xx * bei;
                    float b = starty + yy * bei;
                    //添加点
                    rwv.addPoint(a, b);
                }
            }
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: ");
        mp.pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart: ");
        mp.start();
    }

    @Override
    protected void onDestroy() {
        mp.stop();
        Looper.getMainLooper().quit();
        super.onDestroy();
    }

    /**
     * 屏幕区域
     *
     * @param activity
     * @return
     */
    private Dimension getAreaOne(Activity activity) {
        Dimension dimen = new Dimension();
        Display disp = activity.getWindowManager().getDefaultDisplay();
        Point outP = new Point();
        disp.getSize(outP);
        dimen.mWidth = outP.x;
        dimen.mHeight = outP.y;
        return dimen;
    }

    /**
     * 应用区域
     *
     * @param activity
     * @return
     */
    private Dimension getAreaTwo(Activity activity) {
        Dimension dimen = new Dimension();
        Rect outRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
        System.out.println("top:" + outRect.top + " ; left: " + outRect.left);
        dimen.mWidth = outRect.width();
        dimen.mHeight = outRect.height();
        return dimen;
    }

    /**
     * view 绘制域
     *
     * @param activity
     * @return
     */
    private Dimension getAreaThree(Activity activity) {
        Dimension dimen = new Dimension();
        // 用户绘制区域
        Rect outRect = new Rect();
        activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(outRect);
        dimen.mWidth = outRect.width();
        dimen.mHeight = outRect.height();
        return dimen;
    }

    private class Dimension {
        public int mWidth;
        public int mHeight;

        public Dimension() {
        }
    }
}
