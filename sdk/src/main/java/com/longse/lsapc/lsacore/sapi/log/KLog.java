package com.longse.lsapc.lsacore.sapi.log;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.longse.lsapc.lsacore.sapi.log.formatter.BundleFormatter;
import com.longse.lsapc.lsacore.sapi.log.formatter.Formatter;
import com.longse.lsapc.lsacore.sapi.log.formatter.IntentFormatter;
import com.longse.lsapc.lsacore.sapi.log.formatter.StackTraceFormatter;
import com.longse.lsapc.lsacore.sapi.log.formatter.StringFormatterJSON;
import com.longse.lsapc.lsacore.sapi.log.formatter.ThreadFormatter;
import com.longse.lsapc.lsacore.sapi.log.printer.LogCatPrinter;
import com.longse.lsapc.lsacore.sapi.log.printer.Printer;


import net.sqlcipher.BuildConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lw on 2017/7/6.
 */
/**
 * 打印结果如下
 * 每一段数据都以‘*’开头
 * 数据上下以200个‘*’分隔
 */

/********************************************************************************************************************************************************************************************************
                                                                                  * print all        //msg字段    以‘*’开头是一段数据
                                                                                  * Bundle[{         //打印Bundle对象   以‘*’开头是一段数据
                                                                                  usename = 23438%%%$^&&*,
                                                                                  age = 30,
                                                                                  isLogin = false
                                                                                  }]
                                                                                  * Intent {        //打印Intent对象    以‘*’开头是一段数据
                                                                                  extras={Bundle[{
                                                                                  Activity = Mainactivty,
                                                                                  data = Bundle[{
                                                                                  usename = 23438%%%$^&&*,
                                                                                  age = 30,
                                                                                  isLogin = false
                                                                                  }]
                                                                                  }]}
                                                                                  }
                                                                                  * {               //打印JSONObject对象   以‘*’开头是一段数据
                                                                                  "id": 54321,
                                                                                  "name": "wei"
                                                                                  }
                                                                                  * Thread_name = main   //打印线程信息   以‘*’开头是一段数据
                                                                                  * com.liuwei.source.weimars.weibo.MainActivity.onCreate(MainActivity.java:52) //打印堆栈  以‘*’开头是一段数据
/********************************************************************************************************************************************************************************************************/
public final class KLog {

    private static final String TAG = "KLog";

    private static final int STACK_TRACE_EXCLUDE_DEPTH = 4;//打印堆栈时，排除最上层的

    private KLog(){}

    private static KLog klog;

    public static KLog getInstance(){
        synchronized (KLog.class){
            if (klog == null){
                klog = new KLog();
            }
            return klog;
        }
    }

    /**
     * 打印开关，上线时可关闭
     */
    private static final boolean PRINT_STATUS = BuildConfig.DEBUG;

    /**
     * 默认打印
     */
    private static final Printer printer = new LogCatPrinter();

    public Builder v(String msg){
        return v(TAG,msg);
    }

    public Builder v(String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return v(TAG, msg, obj);
        } else {
            return v(TAG, msg);
        }
    }

    private Builder v(String tag, String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return print(Log.VERBOSE, tag, msg, obj);
        } else {
            return print(Log.VERBOSE, tag, msg);
        }
    }

    public Builder d(String msg) {
        return d(TAG, msg);
    }

    public Builder d(String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return d(TAG, msg, obj);
        } else {
            return d(TAG, msg);
        }
    }

    private Builder d(String tag, String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return print(Log.DEBUG, tag, msg, obj);
        } else {
            return print(Log.DEBUG, tag, msg);
        }
    }

    public Builder i(String msg) {
        return i(TAG, msg);
    }

    public Builder i(String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return i(TAG, msg, obj);
        } else {
            return i(TAG, msg);
        }
    }

    private Builder i(String tag, String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return print(Log.INFO, tag, msg, obj);
        } else {
            return print(Log.INFO, tag, msg);
        }
    }

    public Builder w(String msg) {
        return w(TAG, msg);
    }

    public Builder w(String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return w(TAG, msg, obj);
        } else {
            return w(TAG, msg);
        }
    }

    private Builder w(String tag, String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return print(Log.WARN, tag, msg, obj);
        } else {
            return print(Log.WARN, tag, msg);
        }
    }

    public Builder e(String msg) {
        return e(TAG, msg);
    }

    public Builder e(String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return e(TAG, msg, obj);
        } else {
            return e(TAG, msg);
        }
    }

    private Builder e(String tag, String msg, Object... obj) {
        if (msg != null && obj != null && obj.length > 0) {
            return print(Log.ERROR, tag, msg, obj);
        } else {
            return print(Log.ERROR, tag, msg);
        }
    }

    private Builder print(int level, String tag, String msg, Object... obj) {
        Builder build = getDefaultBuilder();
        try {
            if (PRINT_STATUS && msg != null) {
                if (obj != null && obj.length > 0) {
                    build.setMsg(level, tag, String.format(msg, obj));
                } else {
                    build.setMsg(level, tag, msg);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return build;
    }

    /**
     * 默认打印线程信息和一条堆栈
     *
     * @return
     */
    private Builder getDefaultBuilder() {
        Builder mBuilder = new Builder();
        mBuilder.tag(TAG);
//        mBuilder.setLevel(Log.DEBUG);
        mBuilder.wt(true);
        mBuilder.ws(1);
        return mBuilder;
    }

    public class Builder {
        private String tag;

        private int level;

        /**
         * 需要打印的字符串信息
         */
        private String msg;

        /**
         * 打印JSON字符串
         */
        private String jsonMsg;

        /**
         * 是否打印线程信息
         */
        private boolean mWithThread;

        /**
         * 默认堆栈信息长度
         */
        private int stackTraceLength;

        /**
         * 需要打印的bundle数据
         */
        private Bundle mBundle;

        /**
         * 需要打印的intent数据
         */
        private Intent intent;

        /**
         * 自定义对象的格式化
         */
        private Map<Class<?>, Formatter<?>> objFormatter = new HashMap<Class<?>, Formatter<?>>();

        int getLevel() {
            return level;
        }

        void setLevel(int level) {
            this.level = level;
        }

        String getMsg() {
            return msg;
        }

        void setMsg(String msg) {
            this.msg = msg;
        }

        /**
         * log tag
         *
         * @param tag
         * @return
         */
        public Builder tag(String tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 是否需要线程信息
         *
         * @param thread
         * @return
         */
        public Builder wt(boolean thread) {
            this.mWithThread = thread;
            return this;
        }

        /**
         * 堆栈长度
         *
         * @param stackTraceLength
         * @return
         */
        public Builder ws(int stackTraceLength) {
            if (stackTraceLength < 0) {
                this.stackTraceLength = 0;
            }
            this.stackTraceLength = stackTraceLength;
            return this;
        }

        /**
         * 打印JSON字符串
         *
         * @param jsonString
         * @return
         */
        public Builder pjson(String jsonString) {
            this.jsonMsg = jsonString;
            return this;
        }

        /**
         * 打印Bundle数据
         *
         * @param bundle
         * @return
         */
        public Builder pBundle(Bundle bundle) {
            this.mBundle = bundle;
            return this;
        }

        /**
         * 打印Intent数据
         *
         * @param intent
         * @return
         */
        public Builder pIntent(Intent intent) {
            this.intent = intent;
            return this;
        }

        /**
         * 暂不支持
         * //     * @param obj
         *
         * @return
         */
//    public KLog pObject(Object obj){
//    }
        public Map<Class<?>, Formatter<?>> getObjFormatter() {
            return objFormatter;
        }

        public void setObjFormatter(Map<Class<?>, Formatter<?>> objFormatter) {
            this.objFormatter = objFormatter;
        }

        void setMsg(int level, String tag, String msg) {
            if (!TextUtils.isEmpty(tag)) {
                tag(TAG);
            }
            if (msg == null) {
                msg = "";
            }
            this.msg = msg;
            this.level = level;
        }

        /**
         * 设置完了数据，最后一定要调该方法才会打印
         */
        public void print() {
            try {
                createMsgAndPrint();
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        private String formatterThreadInfo(Thread thread) {
            if (thread == null) return "";
            return new ThreadFormatter().formatter(thread);
        }

        private void createMsgAndPrint() {
            if (!PRINT_STATUS) return;
            StringBuilder builder = new StringBuilder();
            if (msg != null) {
                builder.append(" * ");
                builder.append(msg);
                builder.append("\n");
            }
            if (mBundle != null) {
                builder.append(" * ");
                builder.append(new BundleFormatter().formatter(mBundle));
                builder.append("\n");
            }
            if (intent != null) {
                builder.append(" * ");
                builder.append(new IntentFormatter().formatter(intent));
                builder.append("\n");
            }
            if (jsonMsg != null) {
                builder.append(" * ");
                StringFormatterJSON stringFormatterJSON = new StringFormatterJSON();
                builder.append(stringFormatterJSON.formatter(jsonMsg));
                builder.append("\n");
            }
            if (mWithThread) {
                builder.append(" * ");
                builder.append(formatterThreadInfo(Thread.currentThread()));
                builder.append("\n");
            }
            int stackTraceDepth = stackTraceLength + STACK_TRACE_EXCLUDE_DEPTH;
            if (stackTraceDepth > STACK_TRACE_EXCLUDE_DEPTH) {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                if (stackTraceElements != null) {
                    stackTraceDepth = Math.min(stackTraceDepth, stackTraceElements.length);
                    StackTraceElement[] traceElements = new StackTraceElement[stackTraceDepth];
                    for (int i = STACK_TRACE_EXCLUDE_DEPTH; i < stackTraceDepth; i++) {
                        traceElements[i] = stackTraceElements[i];
                    }
                    if (traceElements.length > 0) {
                        StackTraceFormatter stackTraceFormatter = new StackTraceFormatter();
                        builder.append(stackTraceFormatter.formatter(traceElements));
                    }
                }
            }
            if (tag == null) {
                tag = TAG;
            }
            if (printer != null) {
                printer.println(level, tag, builder.toString());
            }
        }
    }

}
