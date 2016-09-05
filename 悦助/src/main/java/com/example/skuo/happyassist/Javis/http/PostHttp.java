package com.example.skuo.happyassist.Javis.http;

import com.example.skuo.happyassist.Javis.Data.USERINFO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class PostHttp {

    public static String RequstPostHttp(String strUrl, Map<String, String> params) {
        URL url = null;
        String result = "";
        try {
            url = new URL(strUrl);

            StringBuffer stringBuffer = new StringBuffer();

            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    stringBuffer
                            .append(entry.getKey())
                            .append("=")
                            .append(entry.getValue())
                            .append("&");
                }
            }
            // 删掉最后一个 & 字符
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            System.out.println("-->>" + stringBuffer.toString());

            HttpURLConnection urlconn = (HttpURLConnection) url.openConnection();
            urlconn.setDoInput(true);// 设置输入流采用字节流模式
            urlconn.setDoOutput(true);
            urlconn.setRequestMethod("POST");
            urlconn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            urlconn.setRequestProperty("Charset", "UTF-8");
            urlconn.setConnectTimeout(10000);
            urlconn.setReadTimeout(15000);
            urlconn.setRequestProperty("Authorization", "Bearer " + USERINFO.Token);

            urlconn.connect();// 链接服务器并发送消息

            // 得到请求的输出流对象
            OutputStreamWriter out = new OutputStreamWriter(urlconn.getOutputStream());
            // 把数据写入请求的Body
            out.write(stringBuffer.toString());
            out.flush();//清楚缓存
            out.close();//关闭

            // 开始接收返回的数据
            InputStream is = urlconn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String readLine = "";
            while ((readLine = bufferedReader.readLine()) != null) {
                result += readLine;
            }

            bufferedReader.close();
            urlconn.disconnect();


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
