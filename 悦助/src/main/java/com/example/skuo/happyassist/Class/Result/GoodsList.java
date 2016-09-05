package com.example.skuo.happyassist.Class.Result;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-7-18.
 */
public class GoodsList {
    public int ID;
    public String OrderCode;
    public String OrderTime;
    public int Status;
    public float Amount;
    public String Receriver;
    public String PhoneNo;
    public String RecAddress;
    public ArrayList<GoodsInfo> GoodsInfoExs = new ArrayList<GoodsInfo>();
}
