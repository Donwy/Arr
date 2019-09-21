package com.longse.lsapc.lsacore.sapi.http;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import com.longse.lsapc.lsacore.BitdogInterface;
import com.longse.lsapc.lsacore.interf.ErrorCode;
import com.longse.lsapc.lsacore.mode.BitmapHttpResult;
import com.longse.lsapc.lsacore.mode.HttpResult;
import com.longse.lsapc.lsacore.mode.StringHttpResult;
import com.longse.lsapc.lsacore.sapi.log.KLog;
import com.longse.lsapc.lsacore.sapi.sharedPreferences.StringCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by lw on 2017/12/5.
 */

public class HttpHelper {

    private static final String TAG = "HttpHelper";

    private static final HttpHelper ourInstance = new HttpHelper();

    private OkHttpClient okHttpClient;

    private HttpClient httpClient;

    public static final String COOKIE_KEY = "cookie_key";

    private static final String REQUEST_Cookie;

    private Object lock = new Object();

    /**
     * 登录之后保证COOKIE值不变，退出登录后解除
     */
    private volatile String lockCookieValue = "";

    static {
        final String packageName = BitdogInterface.getInstance().getApplicationContext().getPackageName();
        if ("com.gzch.lsplat.bitdog".equals(packageName)) {
            REQUEST_Cookie = "https://www.bitdog.com/as/account/getCookie";
        } else if ("com.xc.hdscreen".equals(packageName)) {
            REQUEST_Cookie = "https://www.freeip.com/as/account/getCookie";
        } else {
            REQUEST_Cookie = "https://www.bitdog.com/as/account/getCookie";
        }
    }

    public static HttpHelper getInstance() {
        return ourInstance;
    }

    private HttpHelper() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20, TimeUnit.SECONDS);//20s 超时
        /**************支持https**********/
        builder.sslSocketFactory(createSSLSocketFactory(), new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        });
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });
        okHttpClient = builder.build();
        httpClient = new HttpClient(okHttpClient);
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ssfFactory;
    }

    /**
     * 清空Cookie
     */
    public void clearCookie() {
        synchronized (lock) {
            StringCache.getInstance().addCache(COOKIE_KEY, "");
            lockCookieValue = "";
        }
    }

//    public void addCookie(String cookie){
//        if (TextUtils.isEmpty(cookie)){
//            synchronized (lock){
//                StringCache.getInstance().addCache(COOKIE_KEY, "");
//            }
//            return;
//        }
//        synchronized (lock){
//            StringCache.getInstance().addCache(COOKIE_KEY, cookie,6L,TimeUnit.DAYS);
//        }
//    }

    /**
     * @return Cookie
     */
    private String checkCookie() {
        return StringCache.getInstance().queryCache(COOKIE_KEY, "");
    }

    private String requestCookie() {
        synchronized (lock) {
            if (!TextUtils.isEmpty(lockCookieValue)) {
                return lockCookieValue;
            }
            String ck = checkCookie();
            if (!TextUtils.isEmpty(ck)) {
                return ck;
            }
            String params = "";
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("requestNo", System.currentTimeMillis() + "");
                jsonObject.put("liveTime", "30");
                jsonObject.put("param", "");
                params = jsonObject.toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Map<String, String> header = new HashMap<>();
            header.put("Cookie", buildCookie(""));
            Response response = httpClient.postJSON(REQUEST_Cookie, params, header);
            HttpResult<String> stringHttpResult = response2String(response);
            if (stringHttpResult != null) {
                String result = stringHttpResult.getResult();
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jobject = new JSONObject(result);
                        int code = jobject.getInt("code");
                        if (code == 0) {
                            String cookie = jobject.getString("data");
                            if (!TextUtils.isEmpty(cookie)) {
                                StringCache.getInstance().addCache(COOKIE_KEY, cookie, 6L, TimeUnit.DAYS);
                                lockCookieValue = cookie;
                                return cookie;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "";
    }

    //服务器支持语言'en', 'cn', 'por', 'py', 'jp', 'pl', 'cs', 'it', 'es'
    private String getAppLanguage() {
        String local_language = "";
        String type = StringCache.getInstance().queryCache("language_key", String.valueOf(-1));
//        KLog.getInstance().d("HttpHelper---getAppLanguage--type--    %s", type).print();
        switch (type) {
            case "0":
                local_language = Locale.getDefault().getLanguage();
//                if (Locale.getDefault())
                break;
            case "1":
                local_language = "cn";
                break;
            case "2":
                local_language = "cn";
                break;
            case "3":
                local_language = "en";
                break;
            case "4":
                local_language = "hu";
                break;
            case "5":
                local_language = "vi";
                break;
            case "6":
                local_language = "it";
                break;
            case "7":
                local_language = "ru";
                break;
            case "8":
                local_language = "fr";
                break;
            case "9":
                local_language = "por";
                break;
            case "10":
                local_language = "de";
                break;
            case "11":
                local_language = "lt";
                break;
            case "12":
                local_language = "pl";
                break;
            case "13":
                local_language = "es";
                break;
            case "14":
                local_language = "ko";
                break;
            case "15":
                local_language = "iw";
                break;
            case "16":
                local_language = "ar";
                break;
            default:
                local_language = Locale.getDefault().getLanguage();
                break;
        }
        return local_language;

    }

    private String buildCookie(String cookie) {
        String local_language = getAppLanguage();
//        String local_language = Locale.getDefault().getLanguage();
//        KLog.getInstance().d("HttpHelper---buildCookie--local_language--%s , getCountry = %s", local_language,Locale.getDefault().getCountry()).print();
        if ((local_language.equals("zh") || local_language.equals("cn")) && "CN".equals(Locale.getDefault().getCountry())) {
            local_language = "cn";
        } else if ((local_language.equals("zh") || local_language.equals("cn")) && "TW".equals(Locale.getDefault().getCountry())) {
            local_language = "cn";
        } else if ((local_language.equals("zh") || local_language.equals("cn")) && "HK".equals(Locale.getDefault().getCountry())) {
            local_language = "cn";
        } else if (local_language.equals("hu")) {
            local_language = "hu";
        } else if (local_language.equals("vi")) {
            local_language = "vi";
        } else if (local_language.equals("it")) {
            local_language = "ita";
        } else if (local_language.equals("ru")) {
            local_language = "py";
        } else if (local_language.equals("fr")) {
            local_language = "fr";
        } else if (local_language.equals("por")) {
            local_language = "por";
        } else if (local_language.equals("de")) {
            local_language = "de";
        } else if (local_language.equals("lt")) {
            local_language = "lt";
        } else if (local_language.equals("ko")) {
            local_language = "ko";
        } else if (local_language.equals("en")) {
            local_language = "en";
        } else if (local_language.equals("es")) {
            local_language = "es";
        } else if (local_language.equals("pl")) {
            local_language = "pl";
        } else {
            local_language = "en";
        }
        if (!TextUtils.isEmpty(cookie)) {
            return "language=" + local_language + ";PHPSESSID=" + cookie;
        } else {
            return "language=" + local_language;
        }
    }

    /**
     * 是否已经连上网络
     *
     * @return
     */
    private HttpResult<String> checkIntentEnable() {
        StringHttpResult stringHttpResult = new StringHttpResult();
        stringHttpResult.setResultCode(ErrorCode.CONNECT_UNABLE);
        if (BitdogInterface.getInstance().getApplicationContext() != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) BitdogInterface.getInstance()
                    .getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    stringHttpResult.setResultCode(ErrorCode.SUCCESS);
                    return stringHttpResult;
                }
            }
        }
        return stringHttpResult;
    }

    /**
     * 如果返回字符串数据太大（超过1M），不要使用该方法
     *
     * @param url
     * @param header 请求头，没有直接传null
     * @return
     */
    public HttpResult<String> get(String url, Map<String, String> header) {
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                return connect;
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                StringHttpResult result = new StringHttpResult();
                result.setResultCode(ErrorCode.NO_COOKIE);
                return result;
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        Response response = httpClient.get(url, header);
        return response2String(response);
    }

    /**
     * 提交键值对
     *
     * @param url
     * @param kv     键值对数据
     * @param header 请求头，没有直接传null
     * @return
     */
    public HttpResult<String> postKeyValue(String url, Map<String, String> kv, Map<String, String> header) {
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                return connect;
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                StringHttpResult result = new StringHttpResult();
                result.setResultCode(ErrorCode.NO_COOKIE);
                return result;
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        Response response = httpClient.postKeyValue(url, kv, header);
        return response2String(response);
    }

    /**
     * POST 提交JSON参数
     *
     * @param url
     * @param jsonString
     * @param header     请求头，没有直接传null
     * @return
     */
    public HttpResult<String> postJSON(String url, String jsonString, Map<String, String> header) {
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                return connect;
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                StringHttpResult result = new StringHttpResult();
                result.setResultCode(ErrorCode.NO_COOKIE);
                return result;
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        Response response = httpClient.postJSON(url, jsonString, header);
        return response2String(response);
    }

    /**
     * POST 提交字符串
     *
     * @param url
     * @param string
     * @param header 请求头，没有直接传null
     * @return
     */
    public HttpResult<String> postString(String url, String string, Map<String, String> header) {
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                return connect;
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                StringHttpResult result = new StringHttpResult();
                result.setResultCode(ErrorCode.NO_COOKIE);
                return result;
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        Response response = httpClient.postString(url, string, header, HttpClient.STRING);
        return response2String(response);
    }

    /**
     * 上传图片文件
     *
     * @param url
     * @param file
     * @param header 请求头，没有直接传null
     * @return
     */
    public HttpResult<String> postFile(String url, File file, Map<String, String> header, String fileKey) {
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                return connect;
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                StringHttpResult result = new StringHttpResult();
                result.setResultCode(ErrorCode.NO_COOKIE);
                return result;
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        header.put("Content-Type", "multipart/form-data;boundary=******");
        Response response = httpClient.postFile(url, file, MediaType.parse("image/*"), header, fileKey);
        return response2String(response);
    }

    /**
     * 请求图片
     *
     * @param url
     * @param header
     * @return
     */
    public HttpResult<Bitmap> getBitmap(String url, String params, Map<String, String> header) {
        BitmapHttpResult result = new BitmapHttpResult();
        result.setResultCode(ErrorCode.RESPONSE_ERROR);
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                return result.setResultCode(connect.resultCode());
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                result.setResultCode(ErrorCode.NO_COOKIE);
                return result;
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        Response response = httpClient.postString(url, params, header, HttpClient.STRING);
        if (response == null) return result;
        if (response.isSuccessful()) {
            if (response.body() != null) {
                try {
                    byte[] bytes = response.body().bytes();
                    if (bytes != null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bitmap == null) {
                            KLog.getInstance().e("request = %s , result bitmap is null", response.request()).tag(TAG).print();
                        }
                        KLog.getInstance().e("request = %s , http Response Code = %d ",
                                response.request(), response.code()).tag(TAG).print();
                        result.setResponse(bitmap);
                        result.setResultCode(ErrorCode.SUCCESS);
                        return result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.setResultCode(ErrorCode.HTTP_REQUEST_ERROR);
                }
            }
        } else {
            result.setResponse(null);
            result.setResultCode(ErrorCode.HTTP_REQUEST_ERROR);
            KLog.getInstance().e("request = %s , http Response Code = %d , error msg = %s",
                    response.request(), response.code(), response.message()).tag(TAG).print();
        }
        response.close();
        return result;
    }

    private StringHttpResult response2String(Response response) {
        StringHttpResult stringHttpResult = new StringHttpResult();
        if (response == null) return stringHttpResult;
        if (response.isSuccessful()) {
            stringHttpResult.setResponse("");
            if (response.body() != null) {
                try {
                    String result = response.body().string();
                    KLog.getInstance().e("request = %s , http Response Code = %d , body = %s",
                            response.request(), response.code(), result).tag(TAG).print();
                    stringHttpResult.setResponse(result);
                } catch (IOException e) {
                    e.printStackTrace();
                    stringHttpResult.setResponse(e != null ? e.getMessage() : "");
                    stringHttpResult.setResultCode(ErrorCode.HTTP_RESPONSE_IO_EXCEPTION);
                    response.close();
                    KLog.getInstance().e("request = %s , IOException , body = %s",
                            response.request(), stringHttpResult.getResult()).tag(TAG).print();
                    return stringHttpResult;
                }
            }
            stringHttpResult.setResultCode(ErrorCode.SUCCESS);
        } else {
            stringHttpResult.setResponse("HTTP ERROR CODE is " + response.code());
            stringHttpResult.setResultCode(ErrorCode.HTTP_REQUEST_ERROR);
            KLog.getInstance().e("request = %s , http Response Code = %d , error msg = %s",
                    response.request(), response.code(), response.message()).tag(TAG).print();
        }
        response.close();
        return stringHttpResult;
    }

    /**
     * GET 下载文件
     *
     * @param url          下载链接
     * @param header       需要添加的响应头
     * @param downloadFile 指定下载文件
     * @param listener     下载进度监听
     */
    public void downloadFile(String url, Map<String, String> header, File downloadFile, DownloadProgressListener listener) {
        if (TextUtils.isEmpty(url) || downloadFile == null) {
            if (listener != null) {
                listener.onFailure(ErrorCode.PARAMS_ERROR, null);
                return;
            }
        }
        HttpResult<String> connect = checkIntentEnable();
        if (connect != null) {
            if (connect.resultCode() != ErrorCode.SUCCESS) {
                if (listener != null) {
                    listener.onFailure(connect.resultCode(), null);
                    return;
                }
            }
        }
        String cookie = checkCookie();
        if (TextUtils.isEmpty(cookie)) {
            cookie = requestCookie();
            if (TextUtils.isEmpty(cookie)) {
                if (listener != null) {
                    listener.onFailure(ErrorCode.NO_COOKIE, null);
                    return;
                }
            }
        }
        if (header == null) {
            header = new HashMap<>();
        }
        header.put("Cookie", buildCookie(cookie));
        Response response = httpClient.get(url, header);
        if (response != null) {
            if (response.isSuccessful()) {
                InputStream in = null;
                byte[] buf = new byte[1024 * 4];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    in = response.body().byteStream();
                    if (in == null) {
                        if (listener != null) {
                            listener.onFailure(ErrorCode.RESPONSE_ERROR, null);
                        }
                        response.close();
                        return;
                    }
                    long total = response.body().contentLength();
                    fos = new FileOutputStream(downloadFile);
                    long sum = 0;
                    while ((len = in.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        int progress = (int) (sum * 1.0f / total * 100);
                        // 下载进度
                        listener.progress(total, sum, progress);
                    }
                    fos.flush();
                    // 下载完成
                    listener.onSuccess();
                } catch (Exception e) {
                    listener.onFailure(ErrorCode.RESPONSE_ERROR, e);
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                    }
                }
            } else {
                if (listener != null) {
                    listener.onFailure(ErrorCode.HTTP_REQUEST_ERROR, new IOException("HTTP ERROR CODE is " + response.code()));
                }
            }
            response.close();
        } else {
            if (listener != null) {
                listener.onFailure(ErrorCode.UNKNOWN_ERROR, null);
            }
        }
    }

    public interface DownloadProgressListener {

        /**
         * 下载进度
         *
         * @param total   文件总长度
         * @param length  当前下载进度
         * @param percent 进度百分比
         */
        void progress(long total, long length, int percent);

        void onSuccess();

        void onFailure(int errorCode, Exception e);
    }

}
