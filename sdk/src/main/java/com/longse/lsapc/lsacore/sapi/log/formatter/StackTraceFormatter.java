package com.longse.lsapc.lsacore.sapi.log.formatter;

import java.io.File;

/**
 * Created by lw on 2017/7/6.
 */

public class StackTraceFormatter implements Formatter<StackTraceElement[]> {
    @Override
    public String formatter(StackTraceElement[] stackTrace) {
        StringBuilder sb = new StringBuilder(256);
        if (stackTrace == null || stackTrace.length == 0) {
            return EMPTY;
        } else if (stackTrace.length == 1) {
            if (stackTrace[0] == null)return EMPTY;
            return " * " + stackTrace[0].toString();
        } else {
            sb.append(" * ");
            for (int i = 0, N = stackTrace.length; i < N; i++) {
                if (stackTrace[i] == null){
                    continue;
                }
                if (i != N - 1) {
                    sb.append(stackTrace[i].toString());
                    sb.append(File.separatorChar);
                    sb.append("\n");
                    sb.append("   ");
                } else {
                    sb.append(stackTrace[i].toString());
                }
            }
            return sb.toString();
        }
    }
}
