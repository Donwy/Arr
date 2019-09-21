package com.longse.lsapc.lsacore.sapi.log.formatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lw on 2017/7/5.
 */

/**
 * JSON字符串格式化显示
 * {
 *     "key1":value,
 *     "key2":[
 *     "value2",
 *     "value3"
 *     ]
 * }
 */
public class StringFormatterJSON implements Formatter<String> {

    private static final int INDENT_SPACES_SIZE = 4;

    @Override
    public String formatter(String data) {
        if (data == null)return EMPTY;
        String result = "";
        try {
            if (data.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(data);
                result = jsonObject.toString(INDENT_SPACES_SIZE);
            } else if (data.startsWith("[")) {
                JSONArray array = new JSONArray(data);
                result = array.toString(INDENT_SPACES_SIZE);
            }
            if (result != null && result.length() > 1 && result.indexOf("\n") > 0){
                char end = result.charAt(result.length() - 1);
                result = result.substring(0,result.length() - 1);
                result += "   " + end;
            }
        }catch (JSONException e){
            e.printStackTrace();
            result = data;
        }
        return result;
    }

}
