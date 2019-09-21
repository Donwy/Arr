package com.longse.lsapc.lsacore.sapi.log.formatter;

import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by lw on 2017/7/6.
 */

public class IntentFormatter implements Formatter<Intent> {

    @Override
    public String formatter(Intent data) {
        if (data == null)return EMPTY;
        return intent2String(data);
    }

    private String intent2String(Intent data){
        if (data == null)return EMPTY;
        StringBuilder b = new StringBuilder(128);
        b.append("Intent { ");
        b.append("\n");
        intentToShortString(data, b);
        b.append("\n");
        b.append("   }");
        return b.toString();
    }

    private static void intentToShortString(Intent intent, StringBuilder b) {
        boolean first = true;
        String mAction = intent.getAction();
        if (mAction != null) {
            b.append("act=").append(mAction);
            first = false;
        }
        Set<String> mCategories = intent.getCategories();
        if (mCategories != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("cat=[");
            boolean firstCategory = true;
            for (String c : mCategories) {
                if (!firstCategory) {
                    b.append(',');
                }
                b.append(c);
                firstCategory = false;
            }
            b.append("]");
        }
        Uri mData = intent.getData();
        if (mData != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("dat=");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                b.append(uriToSafeString(mData));
            } else {
                String scheme = mData.getScheme();
                if (scheme != null) {
                    if (scheme.equalsIgnoreCase("tel")) {
                        b.append("tel:xxx-xxx-xxxx");
                    } else if (scheme.equalsIgnoreCase("smsto")) {
                        b.append("smsto:xxx-xxx-xxxx");
                    } else {
                        b.append(mData);
                    }
                } else {
                    b.append(mData);
                }
            }
        }
        String mType = intent.getType();
        if (mType != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("typ=").append(mType);
        }
        int mFlags = intent.getFlags();
        if (mFlags != 0) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("flg=0x").append(Integer.toHexString(mFlags));
        }
        String mPackage = intent.getPackage();
        if (mPackage != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("pkg=").append(mPackage);
        }
        ComponentName mComponent = intent.getComponent();
        if (mComponent != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("cmp=").append(mComponent.flattenToShortString());
        }
        Rect mSourceBounds = intent.getSourceBounds();
        if (mSourceBounds != null) {
            if (!first) {
                b.append(' ');
            }
            first = false;
            b.append("bnds=").append(mSourceBounds.toShortString());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData mClipData = intent.getClipData();
            if (mClipData != null) {
                if (!first) {
                    b.append(' ');
                }
                first = false;
                b.append("(has clip)");
            }
        }
        Bundle mExtras = intent.getExtras();
        if (mExtras != null) {
            if (!first) {
                b.append(' ');
            }
            b.append("   extras={");
            BundleFormatter bundleFormatter = new BundleFormatter();
            b.append(bundleFormatter.formatter(mExtras));
            b.append('}');
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            Intent mSelector = intent.getSelector();
            if (mSelector != null) {
                b.append(" sel=");
                intentToShortString(mSelector, b);
                b.append("}");
            }
        }
    }

    private static String uriToSafeString(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                Method toSafeString = Uri.class.getDeclaredMethod("toSafeString");
                toSafeString.setAccessible(true);
                return (String) toSafeString.invoke(uri);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return uri.toString();
    }
}
