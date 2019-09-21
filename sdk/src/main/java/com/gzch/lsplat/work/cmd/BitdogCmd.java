package com.gzch.lsplat.work.cmd;

/**
 * Created by lw on 2017/12/7.
 */

import com.gzch.lsplat.work.mode.DeviceNativeInfo;
import com.gzch.lsplat.work.mode.UserInfo;
import com.gzch.lsplat.work.mode.event.DataKey;
import com.gzch.lsplat.work.mode.event.DeviceList;
import com.longse.lsapc.lsacore.mode.Result;

/**
 * 命令参数，常量
 * 每个开发最高位不同，防止冲突
 */
public interface BitdogCmd {

    /**
     * 请求设备设置信息
     * 请求参数示例:{"cmd":"cmd_get_param","device_id":"9874076354162","dev_username":"","dev_passwd":""}
     * 不接受同步调用
     * 登录结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     * 返回结果bean类为{@link DeviceNativeInfo} 通过Result#getObj获取
     */
    int REQUEST_DEVICE_SETTING_PARAMS_CMD = 1001;

    /**
     * 修改设备设置
     * 请求参数示例: {"cmd":"cmd_set_param","device_id":"9874076354162","dev_username":"","dev_passwd":"","k1":"v1","k2":"v2","k3":"v3"}
     * 不接受同步调用
     * 登录结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     */
    int SETTING_DEVICE_PARAMS_CMD = 1002;

    /**
     * 登录平台
     * 请求参数示例:{"user_name":"","local_ip":"","port":0,"no_ssl_port":0 ,"need_return":0}
     * need_return : 是否需要返回消息, 0或者没有该参数 默认返回，否则不返回
     * 参数都可为空
     * 登录结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     */
    int LOGIN_PLATFORM_CMD = 1003;

    /**
     * 登录账号
     * 请求参数示例:{"account":"","password":"","local_ip":"","save":0}
     * save : 0 或者不传 保存密码，否则不保存(save = 1); 默认保存密码
     * 没有账号密码，可传""
     * local_ip : 登录平台时使用的IP，如果用户手动设置了的话，传用户设置的IP，否则该值传空，通过接口获取IP
     * 账号或密码为空时，只做部分初始化工作,还是会登录平台
     * 如果 local_ip 不为空，就不会去请求IP
     * 不接受同步调用
     * UI 调该命令后 会同时收到该命令和 LOGIN_PLATFORM_CMD 命令的结果
     * 需要确保两个结果都收到之后才进行下一步,如果网络不可用，只会收到该命令的结果
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#NO_SUPPORT_THREAD} 不支持同步调用，检查代码
     *  4.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  另外还需要处理平台返回的code值做相应的处理
     *  登录结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     *  如果请求失败，平台错误码通过Result#getExecResult获取，平台返回的msg通过Result#getObj获取
     */
    int LOGIN_CMD = 2003;

    /**
     * 登录类型
     * 微信登录
     * 仅第三方登录时作为who参数
     */
    int WECHAT_LOGIN = 1;

    /**
     * 登录类型
     * facebook登录
     * 仅第三方登录时作为who参数
     */
    int FACEBOOK_LOGIN = 2;

    /**
     * 登录类型
     * GooglePlus登录
     * 仅第三方登录时作为who参数
     */
    int GOOGLE_PLUS_LOGIN = 3;

    /**
     * 登录类型
     * Twitter登录
     * 仅第三方登录时作为who参数
     */
    int TWITTER_LOGIN = 4;

    /**
     * 登录类型
     * Line登录
     * 仅第三方登录时作为who参数
     */
    int LINE_LOGIN = 5;

    /**
     * 第三方登录
     * 请求参数示例:{"token":"","who":1,"local_ip":"","auto":0}
     * who:{WECHAT_LOGIN,FACEBOOK_LOGIN}
     * token不能为空,who不能为无关值
     * local_ip : 登录平台时使用的IP，如果用户手动设置了的话，传用户设置的IP，否则该值传空，通过接口获取IP
     * auto : 是否是自动登录；0 或 没有该参数 默认为自动登录，否则为手动登录
     * UI 调该命令后 会同时收到该命令和 LOGIN_PLATFORM_CMD 命令的结果
     * 需要确保两个结果都收到之后才进行下一步
     * 不接受同步调用
     * 登录结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#NO_SUPPORT_THREAD} 不支持同步调用，检查代码
     *  4.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  5.{@link com.gzch.lsplat.work.ErrorCode#APK_NOT_FOUND} 未安装应用
     *  6.{@link com.gzch.lsplat.work.ErrorCode#AUTHORIZATION_FAILURE} 第三方授权失败
     *  7.{@link com.gzch.lsplat.work.ErrorCode#AUTHORIZATION_CANCEL} 第三方授权取消
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int THIRD_PARTY_LOGIN_CMD = 2004;

    /**
     * 请求用户信息
     * 该请求不需要参数，但是需要在用户登录成功之后请求
     * 不接受同步调用
     * 登录结果通过EventBus#post UserInfo{@link UserInfo} 返回
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#NO_SUPPORT_THREAD} 不支持同步调用，检查代码
     *  4.{@link com.gzch.lsplat.work.ErrorCode#UNKNOWN_ERROR} 调试，检查代码
     *  5.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int REQUEST_USER_INFO_CMD = 2005;

    /**
     * 极光推送是否开启
     * 该命令不需参数，同步结果
     * 如果极光当前状态不可用，提示用户重新设置
     * 返回值在Result obj 中 为Boolean类型
     */
//    int JPUSH_STATUS_ISOPEN_CMD = 2006;

    /**
     * 推送是否可用
     * 该命令不需参数，同步结果
     * 响应结果只能通过异步消息返回
     */
    int CHECK_JPUSH_STATUS_CMD = 2006;

    /**
     * 极光推送检测是否需要开启
     * 请求参数示例:"123456",不用拼JSON，直接传userid字符串
     */
    int JPUSH_AUTO_START_CMD = 2007;

    /**
     * 开启极光推送
     * 请求参数示例:"123456",不用拼JSON，直接传userid字符串
     * web登录成功后,拿到用户信息，然后调该命令
     * 返回值通过Result post
     * 响应结果只能通过异步消息返回
     */
    int JPUSH_START_CMD = 2008;

    /**
     * 停止极光推送
     * 请求参数示例:{"record_status":0}
     * record_status : 是否记录状态;0或者没有该参数 记录；否则 不记录;(默认是记录状态的)
     * 返回值通过Result post
     * 响应结果只能通过异步消息返回
     */
    int JPUSH_STOP_CMD = 2009;

    /**
     * 请求所有的设备 不包括直连设备
     * 该命令不需参数
     * 请求结果类DeviceList {@link DeviceList}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#EMPTY_RESULT} 没有请求到列表,需要调试查找原因
     *  4.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int REQUEST_DEVICE_LIST_CMD = 2010;

    /**
     * 请求仅包含VR设备的 设备列表
     * 该命令不需参数
     * 请求结果类DeviceList {@link DeviceList}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#EMPTY_RESULT} 没有请求到列表,需要调试查找原因
     *  4.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int REQUEST_VR_DEVICE_LIST_CMD = 2011;

    /**
     * 请求不包含VR设备的 设备列表  不包括直连设备
     * 该命令不需参数
     * 请求结果类DeviceList {@link DeviceList}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#EMPTY_RESULT} 没有请求到列表,需要调试查找原因
     *  4.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int REQUEST_NO_VR_DEVICE_LIST_CMD = 2012;

    /**
     * 请求分组
     * 该命令不需参数
     * 请求结果类 GroupList {@link com.gzch.lsplat.work.mode.event.GroupList}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int REQUEST_DEVICE_GROUP_CMD = 2013;

    /**
     * 设备管理请求所有分组和设备列表，包括直连设备
     * 请求参数{"need_cache":""}
     * need_cache : 是否需要缓存 需要 1 , 不需要 0
     * 请求结果类DataKey {@link DataKey}
     * 判断请求成功后可通过{@link com.longse.lsapc.lsacore.BitdogInterface#getData(String)}
     * 方法获取列表数据
     * 如果内存中已经有上次的请求值，该命令会先马上返回上次的值，然后再去网络请求刷新
     * 可能会返回请求分组和请求设备列表中的所有错误
     */
    int REQUEST_DEVICE_MANAGER_DEVICE_LIST_CMD = 2014;

    /**
     * VR播放，请求分组和VR设备列表
     * 请求结果类DataKey {@link DataKey}
     * 判断请求成功后可通过{@link com.longse.lsapc.lsacore.BitdogInterface#getData(String)}
     * 方法获取列表数据
     * 如果内存中已经有上次的请求值，该命令会先马上返回上次的值，然后再去网络请求刷新
     * 可能会返回请求分组和请求设备列表中的所有错误
     */
    int REQUEST_VR_PLAY_DEVICE_LIST_CMD = 2015;

    /**
     * 预览,回放，请求分组和不包含VR的设备列表
     * 请求参数{"need_direct":""}
     * need_direct : 是否需要直连设备,需要 1 , 不需要 0 ;回放不需要直连设备
     * 请求结果类DataKey {@link DataKey}
     * 判断请求成功后可通过{@link com.longse.lsapc.lsacore.BitdogInterface#getData(String)}
     * 方法获取列表数据
     * 如果内存中已经有上次的请求值，该命令会先马上返回上次的值，然后再去网络请求刷新
     * 可能会返回请求分组和请求设备列表中的所有错误
     */
    int REQUEST_NO_VR_PLAY_DEVICE_LIST_CMD = 2016;

    /**
     * 显示验证码
     * 请求结果类DataKey {@link DataKey}
     * 判断请求成功后可通过{@link com.longse.lsapc.lsacore.BitdogInterface#getData(String)}
     * 方法获取WeakReference<Bitmap>
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.gzch.lsplat.work.ErrorCode#UNKNOWN_ERROR} 调试，检查代码，提示重新请求
     *  3.{@link com.gzch.lsplat.work.ErrorCode#NO_SUPPORT_THREAD} 不支持同步调用，检查代码
     */
    int SHOW_VERIFICATION_CMD = 2017;

    /**
     * 修改分组名
     * 请求参数示例:{"cate_name":"","cate_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  4.{@link com.gzch.lsplat.work.ErrorCode#UNKNOWN_ERROR} 调试，检查代码
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int UPDATE_GROUP_NAME_CMD = 2018;

    /**
     * 删除分组
     * 请求参数示例:{"cate_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  4.{@link com.gzch.lsplat.work.ErrorCode#UNKNOWN_ERROR} 调试，检查代码
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int DELETE_GROUP_CMD = 2019;

    /**
     * 创建新分组
     * 请求参数示例:{"cat_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  4.{@link com.gzch.lsplat.work.ErrorCode#UNKNOWN_ERROR} 调试，检查代码
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int CREATE_GROUP_CMD = 2020;

    /**
     * 注册
     * 请求参数示例:{"username":"","password":"","code":""}
     * username : 用户名
     * password: 输入的密码，不需要任何处理,提交的时候会加密
     * code : 验证码
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int REGISTER_CMD = 2021;

    /**
     * 发送邮箱验证码
     * 请求参数示例:{"username":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SEND_EMAIL_CMD = 2022;

    /**
     * 忘记密码后 可重设密码
     * 请求参数示例:{"user_name":"","password":"","code":""}
     * code : 用户邮箱收到的验证码
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int RESET_PASSWORD_CMD = 2023;

    /**
     * 设置昵称
     * 请求参数示例:{"nick_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SET_NICK_NAME_CMD = 2024;

    /**
     * 设置真实名字
     * 请求参数示例:{"real_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SET_REAL_NAME_CMD = 2025;

    /**
     * 修改密码
     * 请求参数示例:{"old_pwd":"","new_pwd":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SET_PASSWORD_CMD = 2026;

    /**
     * 退出登录
     * 该命令不需要参数
     * 同步调用，直接返回
     */
    int LOGOUT_CMD = 2027;

    /**
     * 请求登录记录
     * 请求参数示例:{"need_password":0}
     * need_password : 0或者不传参数 返回所有的登录记录，列表的第一个有密码 , 可用于输入账号提醒 和 自动登录
     * need_password = 1 返回所有的登录记录 用于输入账号提醒(下拉列表)
     * need_password = 2 用于切换账号 ， 可用于自动登录
     * 请求结果类 {@link com.gzch.lsplat.work.mode.event.LoginHistoryEvent}
     */
    int GET_LOGIN_HISTORY_CMD = 2028;

    /**
     * 上传头像
     * 请求参数示例:{"file_path":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int UPLOAD_ICON_CMD = 2029;

    /**
     * 设置设备名
     * 请求参数示例:{"device_id":"","device_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SET_DEVICE_NAME_CMD = 2030;

    /**
     * 解绑设备
     * 请求参数示例:{"device_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int UNBIND_DEVICE_CMD = 2031;

    /**
     * 改变设备分组
     * 请求参数示例:{"device_id":"","cate_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_DEVICE_GROUP_CMD = 2032;

    /**
     * 获取设备的所有分享用户列表
     * 请求参数示例:{"device_id":""}
     * 请求结果类 {@link com.gzch.lsplat.work.mode.event.MemberList}
     */
    int GET_DEVICE_SHARE_USER_LIST_CMD = 2033;

    /**
     * 绑定设备
     * 请求参数示例:{"device_id":"","local_user":"","local_pwd":"","verify":""}
     * local_user : 设备机身账号
     * local_pwd : 设备机身密码
     * verify : 设备验证码，机身上 一串大写字母(可为"")
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int BIND_DEVICE_CMD = 2034;

    /**
     * 同时设置设备名和分组
     * 求参数示例:{"device_id":"","cate_id":"","device_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SET_DEVICE_NAME_AND_GROUP_CMD = 2035;

    /**
     * 修改设备用户名和密码
     * 请求参数示例:{"device_id":"","local_user":"","local_pwd":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_DEVICE_USER_PASSWORD_CMD = 2036;

    /**
     * 绑定分享设备
     * 请求参数示例:{"token":"","device_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int BIND_SHARE_DEVICE_CMD = 2037;

    /**
     * 取消分享给某个用户的分享设备
     * 请求参数示例:{"device_id":"","user_ids":""}
     * user_ids : 绑定了分享设备的用户id
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int UNBIND_SHARE_DEVICE_CMD = 2038;

    /**
     * 通过序列号获取设备信息
     * 请求参数示例:{"device_id":"","save":0,"account":"","password":"","valid":1}
     * save : 是否需要保存为历史记录,0 或者没有该参数: 默认保存 ; 1 : 保存序列号和账号,不保存密码;  不等于0 或1 ：不保存
     * valid : 1 缓存请求结果，有效期内直接返回；0或其它值 不缓存  可选参数
     * dev_name : 设备名  可选参数
     * 请求结果类 {@link com.gzch.lsplat.work.mode.event.SerialNumberDeviceInfoEvent}
     */
    int GET_DEVICE_INFO_BY_SERIAL_NUMBER_CMD = 2039;

    /**
     * 获取演示设备列表
     * 不需要参数
     * 请求结果类 {@link com.gzch.lsplat.work.mode.event.DemoDeviceList}
     */
    int GET_DEMO_DEVICE_LIST_CMD = 2040;

    /**
     * 获取某台演示设备的播放路径
     * 请求参数示例:{"device_name":"","type":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 返回的URL 在 {@link Result#getObj()}
     */
    int GET_DEMO_PLAY_URL_CMD = 2041;

    /**
     * 播放成功后，增加演示设备的播放量
     * 请求参数示例:{"device_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int ADD_DEMO_DEVICE_PLAY_NUMBER_CMD = 2042;

    /**
     * 修改分组排序
     * 请求参数示例:{"new_order":"","cate_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_GROUP_ORDER_CMD = 2043;

    /**
     * 修改设备排序
     * 请求参数示例:{"device_id":"","cate_id":"","order_num":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_DEVICE_ORDER_CMD = 2044;

    /**
     * 修改通道排序
     * 请求参数示例:{"device_id":"","channel":"","order_num":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_CHANNEL_ORDER_CMD = 2045;

    /**
     * 修改通道名
     * 请求参数示例:{"device_id":"","channel":"","channel_name":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_CHANNEL_NAME_CMD = 2046;

    /**
     * 保存直连设备
     * 请求参数示例:{"deviceId":"","deviceType":"","deviceName":"","ip":"","port":"","user":"","password":"","channelNumber":""}
     * deviceId 可为空(支持FreeIp)，old_url 可为空,其他参数都不能为空
     * deviceId 默认为 ip + port
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int INSERT_DIRECT_DEVICE_CMD = 2047;

    /**
     * 修改直连设备
     * 请求参数示例:{"old_url":"","deviceType":"","deviceName":"","ip":"","port":"","user":"","password":"","channelNumber":""}
     * 参数都不能为空
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int UPDATE_DIRECT_DEVICE_CMD = 2048;

    /**
     * 删除直连设备
     * 请求参数示例:{"deviceType":"","deviceName":"","ip":"","port":"","user":"","password":"","channelNumber":""}
     * 参数都不能为空
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int DELETE_DIRECT_DEVICE_CMD = 2049;

    /**
     * 提交意见反馈
     * 请求参数示例:{"message":"","email":"","phone":"","pic":""}
     * message : 意见内容
     * pic : 图片路径
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int COMMIT_SUGGEST_CMD = 2050;

    /**
     * 检测设备是否需要验证码
     * {"device_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHECK_DEVICE_CODE_CMD = 2051;

    /**
     * 获取直连设备
     * 无需参数
     * 请求结果类DataKey {@link DataKey}
     * 判断请求成功后可通过{@link com.longse.lsapc.lsacore.BitdogInterface#getData(String)}
     */
    int GET_DIRECT_DEVICE_LIST = 2052;

    /**
     * 连接某个WiFi
     * 请求参数示例:{"account":"","password":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CONNECT_WIFI_CMD = 2053;

    /**
     * 检测设备账号密码是否正确,同时会检测设备是否在线
     * 如果设备不在线会直接返回
     * 请求参数示例:{"device_id":"","account":"","password":""}
     * 请求参数示例:{"device_id":"","account":"","password":"","locale":0,"ip":"","port":100}
     * 请求参数示例:{"device_id":"","account":"","password":"","save":0,"locale":0}
     * //save : 是否需要保存序列号为历史记录,0  保存 ，1 : 保存序列号和账号,不保存密码;  不等于0 或1 ：不保存；SN 登录时需要保存
     * locale : 0 或者没有该参数 表示不是locale，否则执行locale函数
     * valid : 1 缓存请求结果，有效期内直接返回；0或其它值 不缓存  可选参数
     * dev_name : 设备名  可选参数
     * 不支持同步调用
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 只需要处理 ErrorCode.DEVICE_OFF_LINE 和 ErrorCode.DEVICE_ACCOUNT_PASSWORD_ERROR
     * 服务器错误 2001  设备不存在
     * 其他错误可不管
     */
    int TEST_DEVICE_ACCOUNT_PASSWORD_CMD = 2054;

    /**
     * 调用SDK方法搜索设备
     * 无需参数
     * 请求结果类 SearchDeviceResultEvent {@link com.gzch.lsplat.work.mode.event.SearchDeviceResultEvent}
     */
    int SEARCH_DEVICE_CMD = 2055;

    /**
     * WiFi配网绑定流程：
     * 1.用户选择一个需要配的WiFi，并输入WiFi密码（先连接该网络测试可用性）;
     * 2.APP通过SDK接口将WiFi账号密码和加密方式发给设备;
     * 3.APP尝试搜索设备，搜到后跳转到绑定界面去绑定设备
     * 请求参数示例:{"account":"","password":""};wifi 账号和密码
     * 请求结果类  {@link com.gzch.lsplat.work.mode.event.WifiSettingResultEvent}
     */
    int DEVICE_WIFI_SETTING_CMD = 2056;

    /**
     * AP配网绑定流程：
     * 1.连接设备WiFi;
     * 2.搜索设备信息
     * 3.用户输入设备账号和密码
     * 4.用户选择一个需要配的WiFi，并输入WiFi密码
     * 5.将该WiFi信息发送给设备（这里会校验账号密码，如有错误需提示用户重新输入,然后重试）
     * 6.收到设备回复后,尝试连接用户配置的WiFi
     * 7.跳转到绑定界面
     * 请求参数示例:{"account":"","password":"","device_id":"","ip":"","port": ,"dev_user":"","dev_pwd":""}
     * account : 需要配置的WiFi账号
     * password : 需要配置的WiFi密码
     * device_id : 设备序列号
     * ip : 第2步中 搜索到的设备ip+
     * port : 第2步中 搜索到的设备 port  int
     * dev_user : 用户输入的设备用户名
     * dev_pwd : 用户输入的设备密码
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 该命令是执行AP配网流程中的第5步
     * 不支持同步调用
     */
    int AP_WIFI_SETTING_CMD = 2057;

    /**
     * 获取序列号登录记录
     * 无需参数
     * 请求结果类 {@link com.gzch.lsplat.work.mode.event.SerialLoginRecordEvent}
     */
    int GRT_SERIAL_LOGIN_RECORD_CMD = 2058;

    /**
     * 删除序列号登录记录
     * 请求参数示例: {"device_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int DELETE_SERIAL_LOGIN_RECORD_CMD = 2059;

    /**
     * 获取报警设备列表
     * 请求参数示例: 无
     * 请求结果类AlarmDeviceList {@link com.gzch.lsplat.work.mode.event.AlarmDeviceList}
     */
    int EVENT_MSG_DEVICES_CMD = 3001;

    /**
     * 获取单台设备报警信息
     * 请求参数示例: {"device_id":"","timeStamp":"","am_id":""}
     * device_id: 设备ID（序列号）
     * timeStamp : 指定日期的时间戳
     * am_id: 报警ID
     * 参数都不能为空
     * 请求结果类EventMsgEvent {@link com.gzch.lsplat.work.mode.event.EventMsgEvent}
     */
    int EVENT_MSG_SINGLE_CMD = 3002;

    /**
     * 获取云设备列表
     * 请求参数示例: 无
     * 请求结果类CloudDeviceEvent {@link com.gzch.lsplat.work.mode.event.CloudDeviceEvent}
     */
    int CLOUD_DEVICES_CMD = 3003;

    /**
     * 获取云设备云端录像
     * 请求参数示例: {"device_id":"","vl_id":"","channel":""}
     * device_id: 设备ID（序列号）
     * vl_id: 服务端返回用于刷新（加载）列表
     * channel: 通道号
     * 参数都不能为空
     * 请求结果类CloudVideoEvent {@link com.gzch.lsplat.work.mode.event.CloudVideoEvent}
     */
    int CLOUD_VIDEOS_CMD = 3004;

    /**
     * 获取云设备时间点云端录像
     * 请求参数示例: {"device_id":"","time":"","channel": ""}
     * device_id: 设备ID（序列号）
     * time: 时间戳
     * channel：通道号
     * 参数都不能为空
     * 请求结果类CloudVideoEvent {@link com.gzch.lsplat.work.mode.event.CloudVideoEvent}
     */
    int CLOUD_VIDEOS_LOCATION_CMD = 3005;

    /**
     * 获取订单类型列表类型
     * 请求参数示例: {"device_id":"", "channel": ""}
     * device_id: 设备ID（序列号）
     * channel：通道号从1开始， 若没有通道则是1
     * 参数都不能为空
     * 请求结果类OrderItemEvent {@link com.gzch.lsplat.work.mode.event.OrderItemEvent}
     */
    int ORDER_TYPES_CMD = 3006;

    /**
     * 使用赠送订单
     * 请求参数示例: {"device_id":"","pfe_id":"","channel":""}
     * device_id: 设备ID（序列号）
     * pfe_id: 套餐类型ID
     * channel: 通道号
     * 参数都不能为空
     * 请求结果类 PayResultEvent {@link com.gzch.lsplat.work.mode.event.PayResultEvent}
     */
    int ORDER_GIFT_TO_USER = 3007;

    /**
     * 删除报警信息
     * 请求参数示例:{"am_ids":[30236399,30235224],"null_data":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.longse.lsapc.lsacore.interf.ErrorCode#CONNECT_UNABLE} 提示用户
     *  2.{@link com.longse.lsapc.lsacore.interf.ErrorCode#PARAMS_ERROR} 检查代码
     *  3.{@link com.gzch.lsplat.work.ErrorCode#RESPONSE_ERROR} 服务器响应错误，提示用户
     *  4.{@link com.gzch.lsplat.work.ErrorCode#UNKNOWN_ERROR} 调试，检查代码
     *  另外还需要处理平台返回的code值做相应的处理
     */
    int DELETE_EVENT_MSG_CMD = 3008;

    /**
     * 查找录像和截图
     * 不需要参数
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 列表数据 List<ImageInfo> imgs = (List<ImageInfo>) result.getObj();
     */
    int QUERY_MEDIA_FILE_CMD = 2060;

    /**
     * 删除截图或录像
     * 请求参数示例:{"path":"","file_name":"","playMode":}
     * 请求参数示例:[{"path":"","file_name":"","playMode":},{"path":"","file_name":"","playMode":}]
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int DELETE_MEDIA_FILE_CMD = 2061;

    /**
     * WeChat授权
     * 请求参数示例: 无
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 可能会返回的错误码以及处理方式:
     *  1.{@link com.gzch.lsplat.work.ErrorCode#APK_NOT_FOUND} 未安装应用
     *  2.{@link com.gzch.lsplat.work.ErrorCode#AUTHORIZATION_FAILURE} 第三方授权失败
     *  3.{@link com.gzch.lsplat.work.ErrorCode#AUTHORIZATION_CANCEL} 第三方授权取消
     */
//    int WECHAT_LOGIN_AUTHORIZE_CMD = 3009;

    /**
     * paypal支付操作
     * 请求参数示例: {"could_type_bean":"","user_id":"","device_id":"","channelID":""}
     * could_type_bean：订单信息
     * user_id：用户ID
     * device_id：设备ID
     * channelID: 通道号
     * 请求结果类 PayResultEvent {@link com.gzch.lsplat.work.mode.event.PayResultEvent}
     * 可能会返回的错误码以及处理方式:
     * 1.{@link com.gzch.lsplat.work.ErrorCode#PAY_CANCEL} 订单取消
     * 2.{@link com.gzch.lsplat.work.ErrorCode#PAY_INVALID} 订单支付验证无效
     * 3.{@link com.gzch.lsplat.work.ErrorCode#PAY_INVALID} 网络异常或者json返回有问题
     */
    int PAYPAL_DOPAY = 3010;

    /**
     * 微信支付
     * 请求参数示例: {"pfe_id":"","device_id":"","channel":""}
     * pfe_id：订单编号
     * device_id：设备序列号
     * channel：设备通道号
     */
    int WECHAT_DOPAY = 3011;

    /**
     * 微信支付
     * 请求参数示例: {"order_id":"","device_id":"","channel":""}
     * pfe_id：订单ID
     * device_id：设备序列号
     * channel：设备通道号
     */
    int CHECK_WECHAT_DOPAY = 3012;

    /**
     * 获取FREEIP订单类型列表类型
     * 请求参数示例: {"device_id":"", "channel": ""}
     * device_id: 设备ID（序列号）
     * channel：通道号从1开始， 若没有通道则是1
     * 参数都不能为空
     * 请求结果类OrderItemEvent {@link com.gzch.lsplat.work.mode.event.OrderItemFreeIPEvent}
     */
    int ORDER_FREEIP_TYPES_CMD = 3013;

    /**
     * 检测app版本更新
     * 不需要参数
     * 请求结果类 VersionBean {@link com.gzch.lsplat.work.mode.VersionBean}
     */
    int CHECK_APP_VERSION_CMD = 4001;

    /**
     * 获取平台登录IP 和 port
     * 无需参数
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int GET_LOCALE_IP = 2062;

    /**
     * 删除一个登录记录
     * 请求参数示例: {"account":""}
     */
    int DELETE_LOGIN_RECORD = 2063;

    /**
     * 申请解绑某台设备
     * 请求参数示例:{"device_id":"","email":"","pic":""}
     * device_id : 设备序列号
     * pic : 图片路径
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int APPLY_UNBIND_CMD = 4002;

    /**
     * 摇一摇设备播放后保存账号密码
     * 请求参数示例:{"device_id":"","account":"","password":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int INSERT_LOCALE_DEVICE_ACCOUNT = 2064;

    /**
     * 查询摇一摇设备账号
     * 请求参数示例:{"device_id":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int QUERY_LOCALE_DEVICE_ACCOUNT = 2065;


    /**
     * 请求 Smart NVR 通道信息
     * 请求参数示例:{"dev_username":"admin","device_id":"9874911115770","cmd_type":"channel_info","cmd":"cmd_get_param","channel_id":"0","dev_passwd":"12345","channel":"0"}
     * 不接受同步调用
     * 请求结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     * 返回结果bean类为{@link com.gzch.lsplat.work.mode.NvrChannelInfo} 通过Result#getObj获取
     */
    int REQUEST_SMARTNVR_CHANNEL_PARAMS_CMD = 4003;
    /**
     * 设置 Smart NVR 通道信息
     * 请求参数示例:{"device_id":"9874911115770","dev_username":"admin","cmd_type":"channel_info","cmd":"cmd_set_param","motswitch":false,"channel_id":0,"dev_passwd":"12345","channel":0}
     * 不接受同步调用
     * 请求结果通过EventBus#post Result {@link com.longse.lsapc.lsacore.mode.Result} 返回
     * 返回结果bean类为{@link com.gzch.lsplat.work.mode.NvrChannelInfo} 通过Result#getObj获取
     */
    int SETTING_SMARTNVR_CHANNEL_PARAMS_CMD = 4004;

    /**
     * 极光获取单台设备报警信息
     * 请求参数示例: {"am_id":""}
     * am_id: 报警ID
     * 参数都不能为空
     * 请求结果类Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int EVENT_MSG_AMID_CMD = 4005;

    /**
     * 获取单台设备报警信息(不需要时间)
     * 请求参数示例: {"device_id":"","am_id":""}
     * device_id: 设备ID（序列号）
     * am_id: 报警ID
     * 参数都不能为空
     * 请求结果类EventMsgEvent {@link com.gzch.lsplat.work.mode.event.EventMsgEvent}
     */
    int EVENT_MSG_BY_ID_CMD = 4006;

    /**
     * 修改收藏设备设备名
     * 请求参数示例: {"device_id":"","channel_id":,"name":""}
     * 请求结果类Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int UPDATE_FAVORITES_DEVICE_CMD = 2066;

    /**
     * 删除收藏设备
     * 请求参数示例: {"device_id":"","channel_id":}
     * 请求结果类Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int DELETE_FAVORITES_DEVICE_CMD = 2067;
    /**
     * 修改设备通道回放的码流类型
     * 请求参数示例: {"device_id":,"channel":,"data_rate":}
     * 请求结果类Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_REPLAY_CODE_TYPE_CMD = 4007;
    /**
     * 修改设备通道回放的录像类型
     * 请求参数示例: {"device_id":,"channel":,"video_type":}
     * 请求结果类Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHANGE_REPLAY_VIDEO_TYPE_CMD = 4008;

    /**
     * 登录记录详情，用于切换账号功能
     * 无需参数
     * 请求结果类 {@link com.gzch.lsplat.work.mode.event.LoginAccountInfoEvent}
     */
    int LOGIN_RECORD_DETAIL_CMD = 2068;

    /**
     * 切换登录
     * 请求参数示例: {"account":"","password":""}
     */
    int SWITCH_LOGIN_CMD = 2069;

    /**
     * 删除所有密码
     */
    int DELETE_ALL_PASSWORD = 2070;
    /**
     * 发送邮箱验证码（用户注册）
     * 请求参数示例:{"username":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int SEND_REGISTER_EMAIL_CMD = 4009;

    /**
     * 清除播放缓存
     * 不需要参数
     */
    int CLEAR_CACHE_CMD = 2071;

    /**
     * 请求协议链接
     * 请求参数示例:{"agree_type":0}
     * agree_type : 0 代表云存储协议
     *              1 代表隐私政策
     *              2 代表用户协议
     */
    int REQUEST_AGREEMENT_CMD = 2072;


    /**
     * 请求分享设备token
     * 请求参数示例:{"device_id":""}
     */
    int REQUEST_SHARER_DEVICE_TOKEN_CMD = 3014;

    /**
     * 绑定分享设备
     * 请求参数示例:{"device_id":"","local_user":"","local_pwd":""}
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int BIND_OLD_SHARE_DEVICE_CMD = 3015;

    /**
     * 删除保存序列号设备
     * 请求参数示例:{"device_id":""}
     * locale : 设备保存类型可选参数; locale 不为空 为绑定设备，其他为本地设备
     */
    int DELETE_SN_PLAY_CACHE_CMD = 2073;

    /**
     * 设备返回701 或者 402 时需要调该接口
     * 检查设备本地账号是否存在
     * 请求参数示例:{"device_id":"%s","local_user":"%s"}
     * local_user : 设备本地账号
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int CHECK_DEVICE_LOCALE_ACCOUNT_CMD = 2074;

    /**
     * 获取用户平台推送消息
     */
    int GET_MESSAGE_PUSH_PLATFORM_CMD = 3016;

    /**
     * 设置用户平台推送消息
     * 请求参数示例:{"switch":"%s","platform":"%s"}
     */
    int SET_MESSAGE_PUSH_PLATFORM_CMD = 3017;

    /**
     * 查找录像和截图
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 列表数据 List<FaceAttrInfo> imgs = (List<FaceAttrInfo>) result.getObj();
     */
    int QUERY_CAPTURE_FILE_CMD = 3018;

    /**
     * 查找录像和截图
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     * 列表数据 List<FaceAttrInfo> imgs = (List<FaceAttrInfo>) result.getObj();
     */
    int QUERY_COMPARED_FILE_CMD = 3019;

    /**
     * 删除 AI Face 数据
     * 请求参数示例:{"path":"","file_name":"","playMode":}
     * 请求参数示例:[{"path":"","file_name":"","playMode":},{"path":"","file_name":"","playMode":}]
     * 请求结果类 Result {@link com.longse.lsapc.lsacore.mode.Result}
     */
    int DELETE_AI_FACE_ATTR_CMD = 3020;
}