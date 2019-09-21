package com.gzch.lsplat.work;

/**
 * Created by lw on 2017/12/7.
 */

/**
 * 错误码，常量
 * 一个五位数，每个开发最高位不同，防止冲突
 * 该类最高位从2开始(1已被使用)
 */
public interface ErrorCode extends com.longse.lsapc.lsacore.interf.ErrorCode{

    /**
     * 调用参数错误， 不支持同步，或不支持异步
     */
    int NO_SUPPORT_THREAD = 20002;

    /**
     * 空值
     */
    int EMPTY_RESULT = 20003;

    /**
     * 极光没有初始化成功
     */
    int JPUSH_INIT_ERROR = 20004;

    /**
     * 直连设备已存在
     */
    int DIRECT_DEVICE_EXISTS = 20005;

    /**
     * 未安装相关第三方应用
     */
    int APK_NOT_FOUND = 30001;

    /**
     * 第三方授权失败
     */
    int AUTHORIZATION_FAILURE = 30002;

    /**
     * 第三方授权取消
     */
    int AUTHORIZATION_CANCEL = 30003;

    /**
     * 取消支付
     */
    int PAY_CANCEL = 30004;

    /**
     * 订单支付验证无效
     */
    int PAY_INVALID = 30005;

    /**
     * 网络或订单异常
     */
    int PAY_ORDER_OR_NET_ERROR = 30006;

    /**
     * 没有权限
     */
    int NO_PERMISSION_ERROR = 20006;

}
