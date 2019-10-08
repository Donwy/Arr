package com.gzch.lsplat.work;

/**
 * Created by lw on 2018/1/2.
 */


import com.example.sdk.R;

/**
 * 专门处理各种错误码的文字提示
 */
public class HandleErrorCode {

    private static final HandleErrorCode ourInstance = new HandleErrorCode();

    public static HandleErrorCode getInstance() {
        return ourInstance;
    }

    private HandleErrorCode() {
    }

    /**
     * 根据错误码和命令返回相应的文字提示
     * @param code 错误码
     * @param cmd 命令
     * @return
     */
    public int error(int code,int cmd){
        int result = 0;
        if (code == ErrorCode.SUCCESS)return result;
        switch (code){
            /**************测试时使用START***************/
            case ErrorCode.NO_FIND_WORKER_ERROR:
                break;
            /**************测试时使用END***************/

            case ErrorCode.UNKNOWN_ERROR:
                result = R.string.system_error;
                break;
            case ErrorCode.PARAMS_ERROR:
                result = R.string.params_error;
                break;
            case ErrorCode.EMPTY_RESULT:
                result = R.string.response_error;
                break;
            case ErrorCode.WIFI_STATUS_UNKNOWN:
            case ErrorCode.WIFI_DISABLE_ERROR:
                result = R.string.wifi_closed;
                break;
            case ErrorCode.NO_FIND_WIFI_SSID_ERROR:
                result = R.string.no_wifi;
                break;
            case ErrorCode.WIFI_CONNECT_FAIL:
                result = R.string.wifi_connect_fail_error;
                break;
            case ErrorCode.HTTP_RESPONSE_IO_EXCEPTION:
                result = R.string.web_response_error;
                break;
            case ErrorCode.NO_COOKIE:
                result = R.string.no_cookie_error;
                break;
            case ErrorCode.RESPONSE_ERROR:
                result = R.string.web_response_error;
                break;
            case ErrorCode.CONNECT_UNABLE:
                result = R.string.http_connect_error;
                break;
            case ErrorCode.DIRECT_DEVICE_EXISTS:
                result = R.string.direct_exists;
                break;
            case ErrorCode.HTTP_REQUEST_ERROR:
                result = R.string.web_response_error;
                break;
            case ErrorCode.NO_PERMISSION_ERROR:
                result = R.string.no_permission_device;
                break;
        }
        return result;
    }
}
