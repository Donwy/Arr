package com.longse.lsapc.lsacore.sapi.log.printer;

/**
 * Created by lw on 2017/7/10.
 */

import android.util.Log;

/**
 * 打印显示到logcat
 */
public class LogCatPrinter implements Printer {

    private static final String topBottomLine = "/****************************************************************************************************" +
            "****************************************************************************************************/";

    private static final int MAX_LENGTH_SING_LINE = 1024;//一行最多打印多长的字符

    @Override
    public void println(int level, String tag, String msg) {
        if (tag == null || msg == null)return;
        int length = msg.length();
        StringBuilder builder = new StringBuilder();
        if (length < 3 * MAX_LENGTH_SING_LINE){
            builder.append(topBottomLine);
        } else {
            Log.println(level,tag,topBottomLine);
        }
        if (length <= MAX_LENGTH_SING_LINE){
            builder.append("\n");
//            builder.append(" *");
            builder.append(msg);
        } else {
            int start = 0;
            int end = MAX_LENGTH_SING_LINE;
            while (start < length){
                if (length >= 3 * MAX_LENGTH_SING_LINE){
                Log.println(level,tag,msg.substring(start,end));
                } else {
                    builder.append("\n");
                    builder.append(" *");
                    builder.append(msg.substring(start,end));
                }
                start = end;
                end = Math.min(start + MAX_LENGTH_SING_LINE , length);
            }
        }
        builder.append("\n");
        builder.append(topBottomLine);
        Log.println(level,tag,builder.toString());
    }
}
