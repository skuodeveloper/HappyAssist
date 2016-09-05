package com.example.skuo.happyassist.Class.Result;

/**
 * Created by Administrator on 16-7-18.
 */
public class GroupInfo {
    public int GroupID;
    public String GroupName;

    @Override
    public String toString() {
        // 为什么要重写toString()呢？因为适配器在显示数据的时候，如果传入适配器的对象不是字符串的情况下，直接就使用对象.toString()
        // TODO Auto-generated method stub
        return GroupName;
    }

    public int GetID() {
        return GroupID;
    }

    public String GetValue() {
        return GroupName;
    }
}
