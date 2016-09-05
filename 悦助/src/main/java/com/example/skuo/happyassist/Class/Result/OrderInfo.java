package com.example.skuo.happyassist.Class.Result;

import java.io.Serializable;

/**
 * Created by Administrator on 16-7-18.
 */
public class OrderInfo implements Serializable {
    public int ID;
    public String OrderCode;
    public String OrderTime;
    public float Amount;
    public int Status;
    public String GoodsNames;
}
