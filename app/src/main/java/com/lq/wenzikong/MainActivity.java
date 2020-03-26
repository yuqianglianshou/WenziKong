package com.lq.wenzikong;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

/**
 * 文字控
 * 文字点阵图的水波纹动画
 * 2020/03/24 重构 lq
 */
public class MainActivity extends AppCompatActivity {
    private RingWaveView rwv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rwv = findViewById(R.id.rwv);
        //生命周期的监听
        getLifecycle().addObserver(rwv);
    }

}
