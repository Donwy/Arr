package com.longse.lsapc.lsacore.sapi.log.formatter;

import android.os.Bundle;

import java.util.Arrays;

/**
 * Created by lw on 2017/7/6.
 */

public class BundleFormatter implements Formatter<Bundle> {

    @Override
    public String formatter(Bundle data) {
        if (data == null)return EMPTY;
        return bundle2String(data);
    }

    private String bundle2String(Bundle data){
        if (data == null)return EMPTY;
        StringBuilder b = new StringBuilder();
        b.append("Bundle[{");
        b.append("\n");
        bundle2RealString(data,b);
        b.append("\n");
        b.append("   }]");
        return b.toString();
    }

    private void bundle2RealString(Bundle bundle, StringBuilder b) {
        boolean first = true;
        for (String key : bundle.keySet()) {
            if (!first) {
                b.append(", ");
                b.append("\n");
            }
            b.append("   ");
            b.append(key).append(" = ");
            Object value = bundle.get(key);
            if (value instanceof Object[]) {
                b.append(Arrays.toString((Object[]) value));
            } else if (value instanceof Bundle) {
                b.append(bundle2String((Bundle) value));
            } else {
                b.append(value);
            }
            first = false;
        }
    }
}
