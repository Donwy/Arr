package com.longse.lsapc.lsacore.interf;

import android.content.Context;
import com.longse.lsapc.lsacore.mode.Result;
import net.sqlcipher.database.SQLiteOpenHelper;


/**
 * Created by lw on 2017/11/22.
 */

public interface LSCoreInterface {

    /**
     * 异步调用，执行结果通过消息返回
     */
    int ASYNC_HANDLE = 1;

    /**
     * 同步方法，执行结果直接返回
     */
    int SYNC_HANDLE = 2;

    /**
     * 初始化内部组件，Application中调用
     * @param ctx
     * @param sqLiteOpenHelper
     */
    void init(Context ctx, SQLiteOpenHelper sqLiteOpenHelper);



    /**
     * 获取全局Context
     * @return
     */
    Context getApplicationContext();

    /**
     * 异步请求
     * 发送命令接口，主要提供给UI层调用
     * 执行后的结果通过消息抛回
     * 注意：之后的操作将在子线程执行
     * @param cmd 命令代号
     * @param jsonParams 需要携带的参数,可以是简单字符串 也可以是JSON格式字符串
     * @param handleType 同步或异步,同步调用结果直接返回
     * @return 可判断操作是否执行
     */
    Result exec(int cmd, String jsonParams, int handleType);

    /**
     * 临时存储部分数据
     * 像一些大的数据列表推荐存储在这里，
     * 接口层获取到数据后保存到这里，然后把KEY抛给UI层
     * UI层的列表显示时，通过KEY到这拿数据
     * 注意:退出一个界面时需要及时删除存储的数据
     * @param key
     * @param obj
     */
    void putData(String key, Object obj);

    /**
     * 通过KEY获取缓存数据
     * @param key
     * @return
     */
    Object getData(String key);

    /**
     * 通过KEY删除缓存数据
     * @param key
     */
    void delData(String key);

    /**
     * 清空所有保存的数据
     */
    void clearData();

//    /**
//     * 查询错误码所代表的文字信息
//     * @param errorCode
//     * @return
//     */
//    int findErr(int errorCode);

    /**
     * 该方法需要在初始化(init)之前执行
     * 注册Worker，收到命令后会查找所有注册的Worker
     * UI层发送的命令最后都会交给Worker处理
     * @param worker
     */
    void registeredWorker(Class<? extends Worker> worker);

    /**
     * 切换线程执行一个事件
     * @param runnable
     * @param thread 指定事件执行的线程 {@link ThreadWrapper}
     */
    void post(Runnable runnable, int thread);

    /**
     * 接口层主要工作都集中在Worker里面
     * 实现每个Worker后需要调接口注册
     * 注意:实现Worker时不要重写构造方法
     */
    interface Worker{

        /**
         * 做一些初始化的工作
         * 创建时会自动调用
         */
        void init();

        /**
         * 该Worker可以处理的命令
         * 上层发送命令后会根据该方法判断是否处理
         * @param cmd
         * @return
         */
        boolean isMyCmd(int cmd);

        /**
         * 处理命令
         * @param cmd
         * @param jsonParams
         * @param handleType 同步或异步,同步调用结果直接返回,异步调用执行结果通过消息返回
         * @return
         */
        Result worked(int cmd, String jsonParams, int handleType);
    }

}
