package com.longse.lsapc.lsacore.interf;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by lw on 2017/8/9.
 */

public interface ThreadWrapper {

    /**
     * 主线程执行
     */
    int THREAD_MAIN = 3;
    /**
     * 在一个多线程的线程池中执行
     */
    int THREAD_IO = 1;
    /**
     * 在一个单线程的线程池中执行
     */
    int THREAD_BACKGROUND = 2;

    /**
     * 默认当前线程
     */
    int THREAD_DEFAULT = 4;

    void run(Runnable runnable);

    /**
     * 在Android主线程中执行
     */
    ThreadWrapper AndroidMainThread = new ThreadWrapper() {

        private Handler handler = new Handler(Looper.getMainLooper());

        @Override
        public void run(Runnable runnable) {
            if (runnable != null){
                if (Thread.currentThread() == Looper.getMainLooper().getThread()){
                    runnable.run();
                } else {
                    handler.post(runnable);
                }
            }
        }
    };

    /**
     * 在线程数无限制的线程中执行
     */
    ThreadWrapper IO_Thread = new ThreadWrapper() {

        private ExecutorService executorService = Executors.newCachedThreadPool();

        @Override
        public void run(Runnable runnable) {
            if (runnable != null){
                executorService.execute(runnable);
            }
        }
    };

    /**
     * 在一个单线程的线程池中执行，多任务需要排队
     */
    ThreadWrapper Background_Thread = new ThreadWrapper() {

        private ExecutorService executorService = Executors.newSingleThreadExecutor();

        @Override
        public void run(Runnable runnable) {
            if (runnable != null){
                executorService.execute(runnable);
            }
        }
    };

    /**
     * 默认在当前线程中直接执行
     */
    ThreadWrapper Default_Thread = new ThreadWrapper() {
        @Override
        public void run(Runnable runnable) {
            if (runnable != null){
                runnable.run();
            }
        }
    };
}
