package com.example.skuo.happyassist.Javis.http;

import com.example.skuo.happyassist.Javis.Data.USERINFO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class GetHttp {
    private final static int REQUEST_SUCCESS = 200;

    public static String RequstGetHttp(String strUrl, Map<String, Object> params) {
        int code = 0;
        URL url = null;
        String result = "";
        try {
            StringBuffer stringBuffer = new StringBuffer();
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    stringBuffer
                            .append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("&");
                }

                // 删掉最后一个 & 字符
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }

            url = new URL(strUrl + stringBuffer);
            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setConnectTimeout(1000);
            urlconn.setReadTimeout(15000);
            urlconn.setRequestProperty("Authorization", "Bearer " + USERINFO.Token);

            urlconn.connect();

            code = urlconn.getResponseCode();

            if (code == REQUEST_SUCCESS) {
                InputStreamReader is = new InputStreamReader(urlconn.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(is);
                String readLine = "";
                while ((readLine = bufferedReader.readLine()) != null) {
                    result += readLine;
                }

                is.close();
            }
            urlconn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
