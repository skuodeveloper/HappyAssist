package com.example.skuo.happyassist.Class.Result;

/**
 * Created by Administrator on 16-7-18.
 */
public class HouseInfo {
    public int HouseID;
    public String HouseCode;

    @Override
    public String toString() {
        // 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
        // TODO Auto-generated method stub
        return HouseCode;
    }

    public int GetID() {
        return HouseID;
    }

    public String GetValue() {
        return HouseCode;
    }
}
