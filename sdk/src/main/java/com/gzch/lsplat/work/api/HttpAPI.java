package com.gzch.lsplat.work.api;

/**
 * Created by lw on 2017/12/7.
 */

import com.gzch.lsplat.work.WorkContext;

/**
 * 网络请求接口
 */
public final class HttpAPI {

    /**
     * 平台主接口
     */
//    private static final String BaseApi = "http://as.freeip.com/";
    //http://52.80.41.111/bitdog/index.php?r=as/
//    private static final String BaseApi = "http://52.80.41.111/bitdog/index.php?r=as/";
//    private static final String BaseApi = "http://as.bitdog.djingwu.com:3480/as/";
    private static final String BaseApi;
    private static final String newBaseApi;
//    private static final String BaseApi = "http://172.18.192.199/bitdog/index.php?r=as/";

    /**
     * 平台公共主接口
     */
//    private static final String commonBaseApi = "http://www.freeip.com/common/";
//    private static final String commonBaseApi = "http://52.80.41.111/bitdog/index.php?r=common/";
//    private static final String commonBaseApi = "http://www.bitdog.djingwu.com:3480/index.php?r=common/";
    private static final String commonBaseApi;
//    private static final String commonBaseApi = "http://172.18.192.199/bitdog/index.php?r=common/";

    /**
     * 版本检测
     */
    public static final String getServerVersionUrl;

    static {
        if (WorkContext.CONTEXT_APP == WorkContext.BITDOG_APP) {
            BaseApi = "https://www.bitdog.com/as/";
            newBaseApi = "https://www.bitdog.com/origin/";
            commonBaseApi = "https://www.bitdog.com/common/";
//            BaseApi = "http://bitdog.freeip.com/as/";
//            commonBaseApi = "http://bitdog.freeip.com/common/";
//            BaseApi = "http://www.bitdog.djingwu.com:3480/as/";
//            newBaseApi = "http://www.bitdog.djingwu.com:3480/origin/";
//            commonBaseApi = "http://www.bitdog.djingwu.com:3480/common/";

            getServerVersionUrl = BaseApi + "version/bitdog";
        } else {
            BaseApi = "https://www.freeip.com/as/";
            newBaseApi = "https://www.freeip.com/as/";
            commonBaseApi = "https://www.freeip.com/common/";

            getServerVersionUrl = BaseApi + "version/version";
        }
    }

    /**
     * 请求当地匹配的服务器IP
     * 登录平台使用
     */
    public static final String getLocalIP = commonBaseApi + "account/getUserServerIP";

    /**
     * login
     */
    public static final String login = BaseApi + "account/reAuthority";

    /**
     * 微信登录
     */
    public static final String wechatLogin = BaseApi + "account/WxLogin";

    /**
     * facebook登录
     */
    public static final String facebookLogin = BaseApi + "account/FbLogin";

    /**
     * line登录
     */
    public static final String lineLogin = newBaseApi + "account/line-login";

    /**
     * Twitter 登录
     */
    public static final String twitterLogin = BaseApi + "account/Twitter";

    /**
     * Google+ 登录
     */
    public static final String googleLogin = newBaseApi + "account/google";

    /**
     * 请求用户信息
     */
    public static final String requestUserInfo = BaseApi + "user/getUserInfo";

    /**
     * 请求所有设备 列表
     */
    public static final String requestAllDevices = BaseApi + "device/getAllDevices";

    /**
     * 请求VR设备 列表
     */
    public static final String requestVRDevices = BaseApi + "device/getVrDevices";

    /**
     * 请求非VR设备  列表
     */
    public static final String requestNOVRDevices = BaseApi + "device/getLocaleDevices";

    /**
     * 请求分组列表
     */
    public static final String requestGroups = BaseApi + "deviceCate/getFenZuList";

    /**
     * 修改分组名
     */
    public static final String updateGroups = BaseApi + "deviceCate/updateFenZu";

    /**
     * 删除分组
     */
    public static final String deleteGroups = BaseApi + "deviceCate/deleteFenZu";

    /**
     * 显示验证码
     */
    public static final String showVerificationCode = BaseApi + "account/getCaptcha";

    /**
     * 新增分组
     */
    public static final String createGroup = BaseApi + "deviceCate/saveFenzu";

    /**
     * 注冊新账号时，检测用户名是否可用
     */
    public static final String checkUserName = BaseApi + "account/checkUsername";

    /**
     * 注册
     */
    public static final String register = BaseApi + "account/signupAdd";

    /**
     * 发送邮箱验证码(忘记密码)
     */
    public static final String sendEmailCode = BaseApi + "account/sendForgetPwdMail";
    /**
     * 发送邮箱验证码（注册）
     */
    public static final String sendRegisterEmailCode = BaseApi + "account/getRegisterCode";

    /**
     * 重设密码
     */
    public static final String resetPassword = BaseApi + "account/resetPwd";

    /**
     * 设置昵称
     */
    public static final String setNickName = BaseApi + "user/setNickName";

    /**
     * 设置真实名字
     */
    public static final String setRealName = BaseApi + "user/setRealName";

    /**
     * 修改密码
     */
    public static final String setPassword = BaseApi + "user/setPassword";

    /**
     * 上传头像
     */
    public static final String uploadIcon = BaseApi + "user/SetUserImg";

    /**
     * 设置设备名
     */
    public static final String setDeviceName = BaseApi + "device/setDeviceName";

    /**
     * 解绑设备
     */
    public static final String unbindDevice = BaseApi + "device/breakDevice";

    /**
     * 修改设备分组
     */
    public static final String changeDeviceGroup = BaseApi + "deviceCate/changDeviceCate";

    /**
     * 获取设备的所有分享用户列表
     */
//    public static final String getDeviceShareUserList = BaseApi + "device/getBindDeviceUserList";
    public static final String getDeviceShareUserList = BaseApi + "device/getDeviceGuessListAndChannelInfo";

    /**
     * 绑定设备
     */
    public static final String bindDevice = BaseApi + "device/bindDevice";

    /**
     * 同时设置设备名和分组
     */
    public static final String setDeviceNameAndGroup = BaseApi + "device/setDeviceNameAndType";

    /**
     * 修改设备用户名和密码
     */
    public static final String changeDeviceUserPassword = BaseApi + "device/checkDeviceLocalUserPwd";

    /**
     * 获取分享设备token
     */
    public static final String getShareDeviceToken = BaseApi + "device/setShareDeviceInfo";


    /**
     * 绑定分享设备
     */
    public static final String bindShareDevice = BaseApi + "device/bindSharedDeviceWithToken";


    public static final String bindOldShareDevice = BaseApi + "device/BindSharedDevice";

    /**
     * 取消分享给某个用户的分享设备
     */
    public static final String unbindShareDevice = BaseApi + "device/cancleBindDeviceByUserIds";

    /**
     * 通过序列号获取设备信息
     */
    public static final String getDeviceInfoBySerialNumber = BaseApi + "account/getDeviceInfoByDid";
//    public static final String getDeviceInfoBySerialNumber = BaseApi + "snDevice/getDeviceInfoByDid";

    /**
     * 演示设备列表
     */
    public static final String demoDeviceList = BaseApi + "testDevice/getDeviceList";

    /**
     * 获取演示设备播放地址
     */
    public static final String getDemoDeviceUrl = BaseApi + "testDevice/getDeviceVideo";

    /**
     * 播放成功后，增加演示设备的播放量
     */
    public static final String addDemoDevicePlayNumber = BaseApi + "testDevice/ScanDeviceByName";

    /**
     * 修改分组排序
     */
    public static final String changeDeviceGroupOrder = BaseApi + "deviceCate/changeCateOrder";

    /**
     * 修改设备排序
     */
    public static final String changeDeviceOrder = BaseApi + "device/changeDeviceOrderNumInCate";

    /**
     * 修改通道排序
     */
    public static final String changeDeviceChannelOrder = BaseApi + "device/changeChannelOrder";

    /**
     * 修改通道名
     */
    public static final String changeDeviceChannelName = BaseApi + "device/setDeviceChannelName";

    /**
     * 上传反馈图片
     */
    public static final String uploadSuggestPic = BaseApi + "account/setPic";

    /**
     * 提交反馈内容
     */
    public static final String postSuggestText = commonBaseApi + "account/feedbackMessage";

    /**
     * 设备是否有机身验证码
     * 绑定时需要
     */
    public static final String checkDeviceCode = commonBaseApi + "account/checkDevice";

    /**
     * 请求报警设备列表
     */
    public static final String getEventMsgDeviceList = BaseApi + "device/getAlarmMessageDeviceList";

    /**
     * Bitdog 项目
     * 请求单个设备报警信息
     */
//    public static final String getEventMsgById = BaseApi + "device/CheckDeviceAlarmMessageByDeviceId";
    public static final String getEventMsgById = BaseApi + "device/checkDeviceAlarmMessageByAmIdAndDate";
    /**
     * FreeIP 项目
     * 请求单个设备报警信息
     */
    public static final String getEventMsgByIds = BaseApi + "device/CheckDeviceAlarmMessageByDeviceId";
//    public static final String getEventMsgById = BaseApi + "device/checkDeviceAlarmMessageByAmIdAndDate";
    /**
     * 极光请求单个设备报警信息
     */
    public static final String getEventMsgByAmId = BaseApi + "device/checkDeviceAlarmMessageByAmId";

    /**
     * 请求云服务设备列表
     */
    public static final String actionGetAllDevcieCloud = BaseApi + "device/getAllDevicesHadTurnOnCloudStorageForAllType";

    /**
     * 请求单台云设备云端录像
     */
    public static final String actionGetDeviceCloudStorageVideoList = BaseApi + "device/getDeviceCloudStorageVideoList";

    /**
     * 请求定位查询云视频
     */
    public static final String actionGetDevcieCloudByTime = BaseApi + "device/getDeviceCloudStorageVideoListByTime";

    /**
     * 请求订单列表
     */
    public static final String actionGetOrderType = BaseApi + "order/getBitDogOrderType";

    /**
     * 获取订单列表
     */
    public static final String actionGetFreeipOrderType = BaseApi + "order/getOrderType";

    /**
     * 使用赠送套餐
     */
    public static final String actionUsingTheGiftForDeviceFromUs = BaseApi + "order/usingTheGiftForDeviceFromUs";

    /**
     * 删除事件消息
     */
    public static final String delEventMsg = BaseApi + "device/deleteAlarmMessage";

    /**
     * 服务端验证PayPal支付是否成功，开启云服务
     */
    public static final String actionCheckPayPalOrder = BaseApi + "order/checkOrder";

    /**
     * 微信支付
     */
    public static final String actionPayCloudStorageOrderByWeChat = BaseApi + "order/payCloudStorageOrderByWeChat";

    /**
     * 校验微信支付
     */
    public static final String actionCheckWeChatOrder = BaseApi + "order/checkWeChatOrder";

    /**
     * 上传二维码设备图片
     */
    public static final String actionSinglePic = commonBaseApi + "img/singlePic";

    /**
     * 申请解绑设备
     */
    public static final String actionApplyForUnitingDevice = commonBaseApi + "device/applyForUnitingDevice";
    /**
     * 修改设备通道回放的码流类型
     */
    public static final String changeDeviceRepalyDataRate = BaseApi + "device/changeDeviceRepalyDataRate";

    /**
     * 修改设备通道回放的录像类型
     */
    public static final String changeDeviceRepalyVideoType = BaseApi + "device/changeDeviceRepalyVideoType";

    /**
     *
     */
    public static final String accountInfo = BaseApi + "account/getUserFormByUserNames";

    /**
     * 隐私政策
     */
    public static final String privacyPolicy = commonBaseApi + "agreement/privacyPolicy";

    /**
     * 服务协议
     */
    public static final String serviceAgreement = commonBaseApi + "agreement/serviceAgreement";

    public static final String cloudService = commonBaseApi + "agreement/cloudStorageAgreement";

    /**
     * BD服务协议
     */
    public static final String privacyPolicyBD = commonBaseApi + "agreement/privacy-policy";

    public static final String serviceAgreementBD = commonBaseApi + "agreement/service-agreement";

    public static final String cloudServiceBD = commonBaseApi + "agreement/cloud-storage-agreement";

//    public static final String cloudServiceZh = commonBaseApi + "agreement/cloudStorageAgreementInChinese";

//    public static final String cloudServiceEn = commonBaseApi + "agreement/cloudStorageAgreementInEnglish";

    /**
     * 设备返回701 或者 402 时需要调该接口
     * 检查设备本地账号是否存在
     */
    public static final String checkDeviceLocaleAccount = newBaseApi + "device/check-device-user-is-exist";

    public static final String getMessagePush = newBaseApi + "user/get-message-platform";

    public static final String setMessagePush = newBaseApi + "user/set-message-push";

}
