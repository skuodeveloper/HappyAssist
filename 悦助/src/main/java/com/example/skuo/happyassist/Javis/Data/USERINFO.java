package com.example.skuo.happyassist.Javis.Data;

import com.example.skuo.happyassist.Javis.Tools.MD5;
import com.example.skuo.happyassist.Javis.http.CU_JSONResolve;
import com.example.skuo.happyassist.Javis.http.Interface;
import com.example.skuo.happyassist.Javis.http.PostHttp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 16-7-15.
 */
public class USERINFO {
    /*
    *用户账号ID
    * */
    public static int AccountID;

    /*
    *登录名
     */
    public static String UserName;

    /*
    *中文名
     */
    public static String TrueName;

    /*
    *手机号码
     */
    public static String Phone;

    /*
    账户类型
     */
    public static int AccountType;

    /*
    角色
     */
    public static int Roles;

    public static int PropertyID;

    /*
    小区ID
     */
    public static int EstateID;

    public static String Token;

    /*
    用户登录验证
     */
    public static boolean AuthLogin(String uid, String pwd) {
        HashMap<String, Object> hashMap;

        Map<String, String> param = new HashMap<String, String>();
        param.put("uid", uid);
        param.put("pwd", MD5.Encryption(pwd));

        //请求数据，返回json
        String json = PostHttp.RequstPostHttp(Interface.Login, param);

        //第一层的数组类型字段
        String[] LIST1_field = {"Data"};

        //第二层的对象类型字段
        String[] STR2_field = {"AccountID", "UserName", "TrueName", "Phone", "AccountType", "Roles", "PropertyID", "EstateID", "Token"};
        ArrayList<String[]> aL_STR2_field = new ArrayList<String[]>();
        //第二层的对象类型字段放入第一层的数组类型字段中
        aL_STR2_field.add(STR2_field);
        //解析返回的json
        hashMap = CU_JSONResolve.parseHashMap2(json, null, LIST1_field, aL_STR2_field);

        ArrayList<HashMap<String, Object>> al = (ArrayList<HashMap<String, Object>>) hashMap.get("Data");
        if (al.size() > 0)
            USERINFO.getUserInfo(al);
        else
            return false;

        return true;
    }

    /*
    获取用户登录信息
     */
    private static void getUserInfo(ArrayList<HashMap<String, Object>> al) {
        AccountID = Integer.parseInt(al.get(0).get("AccountID").toString());
        UserName = al.get(0).get("UserName").toString();
        TrueName = al.get(0).get("TrueName").toString();
        Phone = al.get(0).get("Phone").toString();
        AccountType = Integer.parseInt(al.get(0).get("AccountType").toString());
        Roles = Integer.parseInt(al.get(0).get("Roles").toString());
        PropertyID = Integer.parseInt(al.get(0).get("PropertyID").toString());
        EstateID = Integer.parseInt(al.get(0).get("EstateID").toString());
        Token = al.get(0).get("Token").toString();
    }
}
