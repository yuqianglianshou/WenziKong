# WenziKong
将文字转换为点阵图，每个点以圆环动画形式表现。
![](http://xiongzhoudadi.com/images/posts/excel_list/ExcelList.gif)

---
layout: post  
title: Android 文字点阵图的水波纹动画
date: 2020-03-26  
tags:  技术
---
### 我总想在宇宙中寻求意义，他却不予以理睬。    

 * 文字水波纹效果
 * 原理：文字生成点阵图，计算文字位置（点阵）集合，draw圆环，每个圆环半径以0开始递增，透明度以255开始递减，颜色随机，每一次刷新半径扩大一点，
 * 透明度降低一些，当透明度为0时，集合中删除点，集合为空时停止刷新，动画停止。
 * 所涉及的知识点：自定义view；文字点阵数据；lifecycles使用；handler处理；动画计算，位置计算等。
 * 效果：运行程序文字依次随机位置以水波纹动画显示，触摸或滑动屏幕，有水波纹动画，程序退到后台，动画暂停，返回到前台继续执行，效果如下：

 <br/> 
![](http://xiongzhoudadi.com/images/posts/wenzikong/wenzikong.gif)
 <br/> 
![](http://xiongzhoudadi.com/images/posts/wenzikong/wenzikong2.gif)
 <br/> 
