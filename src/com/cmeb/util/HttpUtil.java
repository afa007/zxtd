package com.cmeb.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.cmeb.ConstantBean;

public class HttpUtil {

    private final Log logger = LogFactory.getLog(getClass());

    public String excute(int method, String url) throws Exception {
        String respMsg = null;
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse mRes = null;
        switch (method) {
            case 0:
                mRes = client.execute(new HttpGet(url));
                break;
            case 1:
                mRes = client.execute(new HttpPost(url));
                break;
            default:
                break;
        }
        if (mRes.getEntity() != null) {
            respMsg = EntityUtils.toString(mRes.getEntity());
        }

        return respMsg;
    }

    /**
     * 获取或者推送数据调用
     * 
     * @param parameters
     * @return
     */
    public String post(String uri) throws Exception {

        String body = null;
        HttpClient client = HttpClientBuilder.create().build();
        if (uri == null || "".equals(uri)) {
            return null;
        }
        HttpPost method = new HttpPost(uri);

        if (method != null) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("appid", ConstantBean.APPID));
            params.add(new BasicNameValuePair("token", ""));
            params.add(new BasicNameValuePair("msisdn", ""));
            method.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

            HttpResponse response = client.execute(method);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                logger.info("Method failed:"
                        + response.getStatusLine());

                return null;
            }

            body = EntityUtils.toString(response.getEntity());
            logger.info("body" + body);

        }
        return body;
    }
}
