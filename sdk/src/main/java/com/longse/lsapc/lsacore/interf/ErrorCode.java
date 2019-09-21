package com.longse.lsapc.lsacore.interf;

/**
 * Created by lw on 2017/11/22.
 */

public interface ErrorCode {

    /**
     * 无错误
     */
    int SUCCESS = 0;

    /**
     * 未知错误
     */
    int UNKNOWN_ERROR = 10001;

    /**
     * 没有找到能执行某条命令的Worker{@link LSCoreInterface.Worker}
     */
    int NO_FIND_WORKER_ERROR = 10002;

    /**
     * 成功执行异步操作，请等待消息返回
     */
    int ASYNC_EXEC_OK = SUCCESS;

    /**
     * 参数错误
     * 可能是参数为空或者格式错误
     */
    int PARAMS_ERROR = 10004;

    /**
     * WIFI状态未知
     */
    int WIFI_STATUS_UNKNOWN = 10005;

    /**
     * WIFI不可用,需要检查WiFi是否开启
     */
    int WIFI_DISABLE_ERROR = 10006;

    /**
     * 没找到这个wifi，请用户检查是否开启路由
     */
    int NO_FIND_WIFI_SSID_ERROR = 10007;

    /**
     * WiFi连接失败;可能是密码错误
     */
    int WIFI_CONNECT_FAIL = 10008;

    /**
     * 网络请求成功并得到了响应，但是响应body转成需要的结果时出现IOException
     */
    int HTTP_RESPONSE_IO_EXCEPTION = 10009;

    /**
     * 网络请求失败
     */
    int HTTP_REQUEST_ERROR = 10010;

    /**
     * 没有Cookie,请求Cookie失败
     */
    int NO_COOKIE = 10011;

    /**
     * 服务器响应错误
     */
    int RESPONSE_ERROR = 10012;

    /**
     * 网络连接不可用
     */
    int CONNECT_UNABLE = 10013;

    /**
     * 命令调用错误
     * 当前处理的命令 与 方法调用 不匹配
     * 即当前方法不能处理该命令
     */
    int CMD_USE_ERROR = 10014;

    /**
     * 设备不在线
     */
    int DEVICE_OFF_LINE = 10015;

    /**
     * 设备账号或密码校验失败
     */
    int DEVICE_ACCOUNT_PASSWORD_ERROR = 10016;

}
