package com.longse.lsapc.lsacore.mode;

import com.longse.lsapc.lsacore.interf.LSCoreInterface;

/**
 * Created by lw on 2017/11/22.
 */

/**
 * 不要对该类进行序列化操作
 */
public class Result {

    /**
     * 表示操作是否执行成功
     * 0 表示成功，其他为失败，
     */
    private int execResult;

    /**
     * 请求命令
     */
    private int cmd;

    /**
     * 调用同步方法试，可直接通过该值携带返回值。
     * 注意：强转类型时先做类型判断！！！
     * 该值有可能不可序列化，不要进行序列化操作
     */
    private Object obj;

    /**
     * 表示操作是否执行成功
     * 0 表示成功，其他为失败，
     * 错误码代表的文字信息可通过 {@link LSCoreInterface} findErr 方法查询
     */
    public int getExecResult() {
        return execResult;
    }

    public void setExecResult(int execResult) {
        this.execResult = execResult;
    }

    public int getCmd() {
        return cmd;
    }

    public Result setCmd(int cmd) {
        this.cmd = cmd;
        return this;
    }

    /**
     * 调用同步方法试，可直接通过该值携带返回值。
     * 注意：强转类型时先做类型判断！！！
     */
    public Object getObj() {
        return obj;
    }

    public Result setObj(Object obj) {
        this.obj = obj;
        return this;
    }

    public static Result getFastResult(int error, int cmd){
        Result r = new Result();
        r.setExecResult(error);
        r.setCmd(cmd);
        return r;
    }

    @Override
    public String toString() {
        return "Result{" +
                "execResult=" + execResult +
                ", cmd=" + cmd +
                ", obj=" + obj +
                '}';
    }
}
