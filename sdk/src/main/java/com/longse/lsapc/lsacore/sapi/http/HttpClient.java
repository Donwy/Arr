package com.longse.lsapc.lsacore.sapi.http;

import android.text.TextUtils;

import com.longse.lsapc.lsacore.sapi.log.KLog;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by lw on 2017/12/5.
 */

class HttpClient {

    private OkHttpClient okHttpClient;

    public static final MediaType JSON = MediaType.parse("text/json; charset=utf-8");

    public static final MediaType STRING = MediaType.parse("text/*; charset=utf-8");

    public HttpClient(OkHttpClient okHttpClient){
        this.okHttpClient = okHttpClient;
    }

    /**
     * 普通 GET 请求
     * @param url : 请求链接
     * @return
     */
    public Response get(String url, Map<String, String> header){
        if (TextUtils.isEmpty(url)){
            return null;
        }
        try {
            Request.Builder request = new Request.Builder().url(url);
            if (request == null)return null;
            if (header != null){
                for (Map.Entry<String, String> m : header.entrySet()){
                    request.addHeader(m.getKey(),m.getValue());
                }
            }
            Response response = okHttpClient.newCall(request.build()).execute();
            if (response != null){
                return response;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 表单提交键值对
     * @param url
     * @param kv 键值对
     * @param header
     * @return
     */
    public Response postKeyValue(String url, Map<String, String> kv, Map<String, String> header){
        if (TextUtils.isEmpty(url)){
            return null;
        }
        FormBody.Builder formBodyBuild = new FormBody.Builder();
        if (kv != null){
            for (Map.Entry<String, String> m : kv.entrySet()){
                formBodyBuild.add(m.getKey(),m.getValue());
            }
        }
        Request.Builder request = new Request.Builder()
                .url(url)
                .post(formBodyBuild.build());
        if (header != null){
            for (Map.Entry<String, String> m : header.entrySet()){
                request.addHeader(m.getKey(),m.getValue());
            }
        }
        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            if (response != null) {
                return response;
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 以JSON格式发送数据
     * @param url
     * @param jsonString
     * @return
     */
    public Response postJSON(String url, String jsonString, Map<String, String> header){
        return postString(url,jsonString,header,JSON);
    }

    /**
     * POST 发送指定格式字符串
     * @param url
     * @param string
     * @param header
     * @param contentType
     * @return
     */
    public Response postString(String url, String string, Map<String, String> header, MediaType contentType){
        if (TextUtils.isEmpty(url))return null;
        if (string == null){
            string = "";
        }
        if (contentType == null){
            contentType = STRING;
        }
        RequestBody body = RequestBody.create(contentType, string);
        if (body == null)return null;
        KLog.getInstance().d("request url = %s , params = %s , header = %s",url,string,header).print();
        Request.Builder request = new Request.Builder()
                .url(url)
                .post(body);
        if (request != null){
            if (header != null){
                for (Map.Entry<String, String> m : header.entrySet()){
                    request.addHeader(m.getKey(),m.getValue());
                    KLog.getInstance().d("request header key = %s , value = %s",m.getKey(),m.getValue()).print();
                }
            }
            try {
                KLog.getInstance().d("request = %s",request.build()).print();
                Response response = okHttpClient.newCall(request.build()).execute();
                if (response != null) {
                    return response;
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    public Response postFile(String url, File file, MediaType contentType, Map<String, String> header, String fileKey){
        if (TextUtils.isEmpty(url) || file == null || !file.exists()){
            return null;
        }
        if (contentType == null){
            contentType = MediaType.parse("application/octet-stream; charset=utf-8");
        }
        MultipartBody.Builder requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(fileKey,file.getName(),RequestBody.create(contentType, file));
        if (requestBody != null){
            try {
                Request.Builder request = new Request.Builder()
                        .url(url)
                        .post(requestBody.build());
                if (header == null){
                    header = new HashMap<>();
                }
                for (Map.Entry<String, String> m : header.entrySet()){
                    request.addHeader(m.getKey(),m.getValue());
                }
                Response response = okHttpClient.newCall(request.build()).execute();
                if (response != null){
                    return response;
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = null;

        try {
            contentTypeFor = fileNameMap.getContentTypeFor(URLEncoder.encode(path, "UTF-8"));
        } catch (UnsupportedEncodingException var5) {
            var5.printStackTrace();
        }

        if(contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }

        return contentTypeFor;
    }
}
