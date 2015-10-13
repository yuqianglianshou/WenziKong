package com.lanqi;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;

import com.lanqi.utils.Font16;
import com.lanqi.utils.Font24;
import com.lanqi.utils.Font32;

public class MainActivity extends Activity {
	
	MediaPlayer mp;
	
	private Context mContext =MainActivity.this;
	  private int w;
	  private int h;
	private RingWaveView rwv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mp = MediaPlayer.create(getApplication(),R.raw.chongerfei);
        mp.setLooping(true);
        mp.start();
        
	    Display localDisplay = getWindowManager().getDefaultDisplay();
	    w = localDisplay.getWidth();
	    h = localDisplay.getHeight();
	    System.out.println(w+"###"+h);
        
        rwv = (RingWaveView) findViewById(R.id.rwv);
        
        initArr();
		
    }



	private void initArr() {
		

		handler.sendEmptyMessageDelayed(1, 1000L);
		handler.sendEmptyMessageDelayed(2, 2000L);
		handler.sendEmptyMessageDelayed(3, 2500L);
		handler.sendEmptyMessageDelayed(4, 4000L);
		handler.sendEmptyMessageDelayed(5, 5000L);
		handler.sendEmptyMessageDelayed(6, 6000L);
		handler.sendEmptyMessageDelayed(7, 7800L);
		
		
		handler.sendEmptyMessageDelayed(8, 11000L);
		handler.sendEmptyMessageDelayed(9, 12000L);
		handler.sendEmptyMessageDelayed(10, 12500L);
		handler.sendEmptyMessageDelayed(11, 14000L);
		handler.sendEmptyMessageDelayed(12, 14500L);
		handler.sendEmptyMessageDelayed(13, 16500L);
		handler.sendEmptyMessageDelayed(14, 18000L);
		
		handler.sendEmptyMessageDelayed(15, 22000L);
		handler.sendEmptyMessageDelayed(16, 24000L);
		handler.sendEmptyMessageDelayed(17, 26000L);
		handler.sendEmptyMessageDelayed(18, 28000L); 


	}
	
    
    Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
    		
    		default:
			case 1:
				showWenZi("问",20,30 );
				break;
			case 2:
				showWenZi("世",w/2+10,h/3);
				
				break;
				
			case 3:
				showWenZi("间",10,h/3*2-10);
				
				break;
			case 4:
				showWenZi("情",10, h/3);
				break;
				
			case 5:
				showWenZi("为",w/2+15,20);
				break;
			case 6:
				showWenZi("何",w/2+15,h/3*2-10);
				break;
				
			case 7:
				showWenZi("物",w/2-90,h/3);
				break;
				
				
			case 8:
				showWenZi("直",w/2+15,20);
				break;
			case 9:
				showWenZi("叫",10, h/3);
				break;
			case 10:
				showWenZi("人",w/2+15,h/3*2);
				break;
			case 11:
				showWenZi("生",w/2+10,h/3);
				break;
			case 12:
				showWenZi("死",20,30);
				break;
			case 13:
				showWenZi("相",10,h/3*2);
				break;
			case 14:
				showWenZi("许",w/2-90,h/3);
				break;
				
			case 0:
				MainActivity.this.finish();
				break;
				
			case 15:
				showWenZi("青",20,30);
				break;
			case 16:
				showWenZi("春",w/2+10,h/3);
				break;
			case 17:
				showWenZi("如",w/2+15,20);
				break;
			case 18:
				showWenZi("梦",10, h/3);
				break;

			

			}
    	};
    };

	

	//适配有问题
	 void showWenZi(String s,int x,int y){
		 
		 int onestartx,onestarty;
				
			onestartx = x;
			onestarty = y;
			
			//适配
			
			int bei = 6;
			if(w<500){
				
			}else if(w>499 && w<750){

				bei = 8;
				
			}else if(w>749 && w<1000){
				
				bei = 10;
			}else{
				
				bei = 14;
			}
					
			System.out.println("create_wenzi");
			
			//变量,24固定写死，字体，大小
			show_font16_24_32(24, s, onestartx, onestarty,  bei);
			
	}
	
	
	public void show_font16_24_32(int font_kind,String s, int stx, int sty, int beishu) {
		
		
		boolean[][] arr = null;
		int weith = 16;
		int height = 16;
		if(font_kind == 16){
			weith = 16;
			height = 16;
			arr = new boolean[weith][height];
			Font16 font16 = new Font16(mContext);
			arr = font16.drawString(s);
		}else if(font_kind == 24){
			weith = 24;
			height = 24;
			arr = new boolean[weith][height];
			Font24 font24 = new Font24(mContext);
			arr = font24.drawString(s);
		}else {
			weith = 32;
			height = 32;
			arr = new boolean[weith][height];
			Font32 font32 = new Font32(mContext);
			arr = font32.drawString(s);
		}
		
		int startx = stx, starty = sty;		
		int bei = beishu;
		for (int i = 0; i < weith ; i++) {
			for (int j = 0; j < height; j++) {
				
				float xx = (float) j;
				float yy = (float) i;
				if (arr[i][j]) {

					float a = startx+ xx * bei ;
					float b = starty + yy* bei ;
					
					//添加点
					rwv.addPoint(a, b);
						
				}
			}

		}
		
		}
	@Override
	public void onBackPressed() {
		System.out.println("stop");
		mp.stop();
		MainActivity.this.finish();
	}
	
}