package com.lq.wenzikong;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.InputStream;

public class Font24 {
    private static final String TAG = "Font24";
    private Context context;

    public Font24(Context context) {
        this.context = context;
    }

    private final static String ENCODE = "GB2312";
    private final static String ZK16 = "Hzk24s";//24*24宋体
//    private final static String ZK16 = "Hzk24h";//24*24黑体

    /**
     * 点阵图点的数量 24*24，由点阵字库决定
     */
    private static final int point_number_24 = 24;
    private static final int all_72 = 72;
    private static final int all_3 = 3;

    /**
     * 返回一个汉字的点阵图24*24
     *
     * @param str
     * @return
     */
    public boolean[][] getSingleWordArr(String str) {
        if(TextUtils.isEmpty(str)){
            Log.d(TAG, "getSingleWordArr: 无字符");
            return new boolean[point_number_24][point_number_24];
        }
        if (str.length()>1 || str.charAt(0) < 0x80) {
            //多字符 或 非中文
            Log.d(TAG, "getSingleWordArr: 多字符 或 非中文");
            return new boolean[point_number_24][point_number_24];
        }
        boolean[][] arr = new boolean[point_number_24][point_number_24];
        //到点阵数据的第几个字节了
        int byteCount = 0;

        int[] code = getByteCode(str);
        //72
        byte[] data = read(code[0], code[1], context);
        int lCount;//控制列
        for (int line = 0; line < point_number_24; line++) {
            lCount = 0;
            for (int k = 0; k < all_3; k++) {
                for (int j = 0; j < 8; j++) {
                    if (((data[byteCount] >> (7 - j)) & 0x1) == 1) {
                        arr[line][lCount] = true;
                        System.out.print("*");
                    } else {
                        System.out.print(" ");
                        arr[line][lCount] = false;
                    }
                    lCount++;
                }
                byteCount++;
            }
            System.out.println();
        }

        return arr;
    }


    /**
     * 读取文字信息
     *
     * @param areaCode 区码
     * @param posCode  位码
     * @return 文字数据
     */
    private static byte[] read(int areaCode, int posCode, Context context) {
        byte[] data = null;
        try {
            int area = areaCode - 0xa0;//获得真实区码
            int pos = posCode - 0xa0;//获得真实位码
            InputStream in = context.getResources().getAssets().open(ZK16);
            long offset = all_72 * ((area - 1) * 94 + pos - 1);
            in.skip(offset);
            data = new byte[all_72];
            in.read(data, 0, all_72);
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }

    /**
     * 获得文字的区位码
     *
     * @param str
     * @return int[2]
     */
    private static int[] getByteCode(String str) {
        int[] byteCode = new int[2];
        try {
            byte[] data = str.getBytes(ENCODE);
            byteCode[0] = data[0] < 0 ? 256 + data[0] : data[0];
            byteCode[1] = data[1] < 0 ? 256 + data[1] : data[1];
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return byteCode;
    }

}
//    GB2312 规定“对任意一个图形字符都采用两个字节表示，每个字节均采用七位编码表示”，习惯上称第一个字节为“高字节”，第二个字节为“低字节”。
//        GB2312-80 包含了大部分常用的一、二级汉字，和 9 区的符号。该字符集是几乎所有的中文系统和国际化的软件都支持的中文字符集，
//        这也是最基本的中文字符集。其编码范围是高位 0xa1 － 0xfe ，低位也是 0xa1-0xfe ；汉字从 0xb0a1 开始，结束于 0xf7fe 。
//
//        GB2312 将代码表分为 94 个区，对应第一字节（ 0xa1-0xfe ）；每个区 94 个位（ 0xa1-0xfe ），对应第二字节，两个字节的值分别为区号值和位号值加 32 （ 2OH ），
//        因此也称为区位码。 01-09 区为符号、数字区， 16-87 区为汉字区（ 0xb0-0xf7 ）， 10-15 区、 88-94 区是有待进一步标准化的空白区。
//        GB2312 将收录的汉字分成两级：第一级是常用汉字计 3755 个，置于 16-55 区，按汉语拼音字母 / 笔形顺序排列；第二级汉字是次常用汉字计 3008 个，
//        置于 56-87 区，按部首 / 笔画顺序排列。故而 GB2312 最多能表示 6763 个汉字。
//
//        仍然是 12*12 点阵的“我”字为例，“我”的编码为 0xCED2 ，因此，在字库文件中，“我”字的偏移为：
//        ((0xCE-0xA1)*94+(0xD2-0xA1))*24 字节 / 字 =102696 ，其后的连续 24 字节即为“我”字的点阵信息。