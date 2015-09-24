package com.guan.o2o.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.guan.o2o.net.MyAsyncHttpClient;
import com.guan.o2o.common.Contant;
import com.guan.o2o.exception.ServiceRulesException;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Guan
 * @file com.guan.o2o.utils
 * @date 2015/9/19
 * @Version 1.0
 */
public class HttpUtil {

    /**
     * HttpURLConnection发送Get请求
     * 获取字符串返回
     *
     * @param httpPath
     * @return
     * @throws Exception
     */
    public static String getStringURLRequest(String httpPath) throws Exception {

        HttpURLConnection connection = null;
        URL url = new URL(httpPath);
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(8000);
        connection.setReadTimeout(8000);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder response = new StringBuilder();
        String line;
        if (connection.getResponseCode() == 200) {
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } else {
            throw new ServiceRulesException(Contant.MSG_SEIVICE_ERROR);
        }

        in.close();
        reader.close();
        connection.disconnect();
        return response.toString();
    }

    /**
     * HttpClient发送Post请求
     * 获取json数据返回
     *
     * @param httpPath
     * @return
     * @throws Exception
     */
    public static String getJsonStringRequest(String httpPath, String registerData) throws Exception {

        String response = null;
        HttpClient httpClient = new DefaultHttpClient();

        /**
         * Get请求
         * 将请求的参数名和值转换成字符串，再并集结成URL
         */
//        HttpGet httpGet = new HttpGet(httpPath);
//        httpClient.execute(httpGet);

        /**
         * Post请求
         * 将请求内容封装在正文中，用户看不到请求参数（起到保密作用）
         */
        HttpPost httpPost = new HttpPost(httpPath);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Data", registerData));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
        httpPost.setEntity(entity);
        HttpResponse httpResponse = httpClient.execute(httpPost);
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            // 请求和响应都成功了
            HttpEntity httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity, "utf-8");
        } else {
            throw new ServiceRulesException(Contant.MSG_SEIVICE_ERROR);
        }

        return response.toString();
    }

    /**
     * 获取网络图片
     *
     * @param httpPath
     * @return
     */
    public static Bitmap getBitmapURLRequest(String httpPath) throws IOException {
        Bitmap map = null;
        InputStream input = null;
        URL url = new URL(httpPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setConnectTimeout(5000);
        if (conn.getResponseCode() == 200) {
            input = conn.getInputStream();
            map = BitmapFactory.decodeStream(input);
        }
        input.close();
        conn.disconnect();
        return map;
    }

    /**
     * 上传图片文件
     * android-async-http 框架
     *
     * @param httpPath
     * @param imageFilePath
     */
    public static void uploadFileURLRequest(String httpPath, String imageFilePath) throws Exception {
        //判断上次路径是否为空
        if (TextUtils.isEmpty(imageFilePath.trim())) {
            throw new ServiceRulesException(Contant.MSG_FILEPATH_NULL);
        } else {
            //封装文件上传的参数
            RequestParams params = new RequestParams();
            //根据路径创建文件
            File file = new File(imageFilePath);
            try {
                //放入文件
                params.put("profile_picture", file);
            } catch (Exception e) {
                throw new ServiceRulesException(Contant.MSG_FILE_NULL);
            }

            //执行post请求
            MyAsyncHttpClient.post(httpPath, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers,
                                      byte[] responseBody) {
                    if (statusCode == 200) {
                        LogUtil.v(Contant.TAG, "上传成功");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers,
                                      byte[] responseBody, Throwable error) {
                    error.printStackTrace();
                    LogUtil.v(Contant.TAG, "上传失败");
                }
            });
        }
    }
}
