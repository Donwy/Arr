package com.longse.lsapc.lsacore;

import android.content.Context;
import android.text.TextUtils;

import com.longse.lsapc.lsacore.interf.ErrorCode;
import com.longse.lsapc.lsacore.interf.LSCoreInterface;
import com.longse.lsapc.lsacore.interf.ThreadWrapper;
import com.longse.lsapc.lsacore.mode.Result;
import com.longse.lsapc.lsacore.sapi.db.DBHelper;
import com.longse.lsapc.lsacore.sapi.log.KLog;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;
import net.sqlcipher.database.SQLiteOpenHelper;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by lw on 2017/12/7.
 */

public class BitdogInterface implements LSCoreInterface {

    private static final String TAG = "BitdogInterface";

    /**
     * 保存所有注册的Worker
     */
    private List<Class<? extends Worker>> czWorkers = new ArrayList<>();

    /**
     * 缓存的Worker实例,自动创建无需管理
     */
    private List<WeakReference<Worker>> workers = new ArrayList<>();

    /**
     * 缓存部分数据，使用完后需要及时清空
     */
    private Map<String, Object> cacheData = new ConcurrentHashMap<>();

    /**
     * 是否初始化成功
     */
    private boolean isInitSuccess = false;

    private Context ctx;

    private static BitdogInterface instance;

    private BitdogInterface(){}

    public static BitdogInterface getInstance(){
        synchronized (BitdogInterface.class){
            if (instance == null){
                instance = new BitdogInterface();
            }
            return instance;
        }
    }

    @Override
    public void registeredWorker(Class<? extends Worker> worker) {
        if (worker == null || checkInit()){
            KLog.getInstance().e("ILSCoreInterface registeredWorker worker is null or checkInit is true.").print();
            return;
        }
        if (czWorkers == null){
            czWorkers = new ArrayList<>();
        }
        czWorkers.add(worker);
    }




    @Override
    public void init(Context ctx, SQLiteOpenHelper sqLiteOpenHelper) {
        if (ctx == null){
            isInitSuccess = false;
            KLog.getInstance().e("ILSCoreInterface init Context is null,init failure.").print();
            return;
        }
        this.ctx = ctx;
        DBHelper.getInstance().init(sqLiteOpenHelper);
        StringCache.getInstance().init(ctx.getApplicationContext());
//        HttpHelper.getInstance().clearCookie();
        createAllWorker();
        isInitSuccess = true;
    }

    /**
     * 创建所有注册的Worker并初始化
     * 需要保证注册得Worker与生成得实例一一对应，
     * 运行时若某个实例被回收，会按照顺序创建
     */
    private void createAllWorker(){
        if (workers == null){
            workers = new ArrayList<>();
        }
        if (workers.size() > 0){
            workers.clear();
        }
        KLog.getInstance().d("ILSCoreInterface createAllWorker czWorkers size = %d", czWorkers.size()).print();
        WeakReference<Worker> workerWeakReference = null;
        LSCoreInterface.Worker worker = null;
        for (Class<? extends Worker> c : czWorkers) {
            try {
                if (c == null){
                    workers.add(null);//补空位
                    continue;
                }
                worker = c.newInstance();
                if (worker != null) {
                    worker.init();
                    workerWeakReference = new WeakReference<Worker>(worker);
                    workers.add(workerWeakReference);
                } else {
                    workers.add(null);//补空位
                    KLog.getInstance().e("ILSCoreInterface createAllWorker %s newInstance is null.", c.getName()).print();
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e1) {
                workers.add(null);//补空位
                e1.printStackTrace();
                workers.add(null);//补空位
            }
        }
        KLog.getInstance().d("ILSCoreInterface createAllWorker create over workers size = %d", workers.size()).print();
    }

    private boolean checkInit(){
        return isInitSuccess;
    }

    @Override
    public Context getApplicationContext() {
        if (ctx != null){
            return ctx.getApplicationContext();
        }
        return ctx;
    }

    @Override
    public Result exec(int cmd, String jsonParams, int handleType) {
        KLog.getInstance().d("exec cmd = %d",cmd).pjson(jsonParams).ws(3).tag(TAG).print();
        if (handleType == ASYNC_HANDLE){
            return execAsync(cmd,jsonParams);
        } else {
            return execSync(cmd,jsonParams);
        }
    }

    private Result execAsync(final int cmd, final String jsonParams) {
        final LSCoreInterface.Worker worker = findWorker(cmd);
        if (worker == null){
            return Result.getFastResult(ErrorCode.NO_FIND_WORKER_ERROR,cmd);
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    worker.worked(cmd,jsonParams,ASYNC_HANDLE);
                }
            }, ThreadWrapper.THREAD_IO);
            return Result.getFastResult(ErrorCode.ASYNC_EXEC_OK,cmd);
        }
    }

    private Result execSync(int cmd, String jsonParams) {
        LSCoreInterface.Worker worker = findWorker(cmd);
        if (worker == null){
            return Result.getFastResult(ErrorCode.NO_FIND_WORKER_ERROR,cmd);
        } else {
            return worker.worked(cmd,jsonParams,SYNC_HANDLE);
        }
    }

    /**
     * 查找需要执行的Worker
     * @param cmd
     * @return
     */
    private LSCoreInterface.Worker findWorker(int cmd){
        if (workers == null || czWorkers.size() == 0)return null;
        LSCoreInterface.Worker worker = null;
        int length = czWorkers.size();
        WeakReference<Worker> workerWeakReference = null;
        for (int i = 0; i < length; i++){
            if (workers.size() > i){
                workerWeakReference = workers.get(i);
            } else {
                workerWeakReference = null;
            }
            if (workerWeakReference != null){
                worker = workerWeakReference.get();
                if (worker != null){
                    if (worker.isMyCmd(cmd)){
                        return worker;
                    } else {
                        continue;
                    }
                }
            }
            Class<? extends Worker> cz = czWorkers.get(i);
            if (cz != null){
                try {
                    worker = cz.newInstance();
                    workerWeakReference = new WeakReference<Worker>(worker);
                    workers.add(i,workerWeakReference);
                    if (worker != null){
                        if (worker.isMyCmd(cmd)){
                            return worker;
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        return null;
    }



    @Override
    public void putData(String key, Object obj) {
        if (TextUtils.isEmpty(key) || obj == null){
            KLog.getInstance().e("ILSCoreInterface putData key or obj is null.").print();
            return;
        }
        if (cacheData == null){
            cacheData = new ConcurrentHashMap<>();
        }
        cacheData.put(key,obj);
    }

    @Override
    public Object getData(String key) {
        if (cacheData == null || TextUtils.isEmpty(key)){
            KLog.getInstance().e("ILSCoreInterface getData cacheData or key is null.").print();
            return null;
        }
        return cacheData.get(key);
    }

    @Override
    public void delData(String key) {
        if (cacheData == null || TextUtils.isEmpty(key)){
            KLog.getInstance().e("ILSCoreInterface delData cacheData or key is null.").print();
        }
        Object obj = cacheData.get(key);
        if (obj != null){
            cacheData.remove(obj);
        }
    }

    @Override
    public void clearData() {
        if (cacheData != null){
            cacheData.clear();
        }
    }

    @Override
    public void post(Runnable runnable, int thread) {
        if (runnable == null)return;
        ThreadWrapper threadWrapper = ThreadWrapper.Default_Thread;
        switch (thread){
            case ThreadWrapper.THREAD_MAIN:
                threadWrapper = ThreadWrapper.AndroidMainThread;
                break;
            case ThreadWrapper.THREAD_BACKGROUND:
                threadWrapper = ThreadWrapper.Background_Thread;
                break;
            case ThreadWrapper.THREAD_IO:
                threadWrapper = ThreadWrapper.IO_Thread;
                break;
            case ThreadWrapper.THREAD_DEFAULT:
            default:
                threadWrapper = ThreadWrapper.Default_Thread;
                break;
        }
        if (threadWrapper != null){
            threadWrapper.run(runnable);
        }
    }
}
