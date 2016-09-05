package com.example.skuo.happyassist.Class.Result;

import java.util.ArrayList;

/**
 * Created by Administrator on 16-7-18.
 */
public class SignInList {
    public int TotalCount;
    public ArrayList<SignInInfo> LastMonthSignInInfos = new ArrayList<SignInInfo>();
    public ArrayList<SignInInfo> CurMonthSignInInfos = new ArrayList<SignInInfo>();
    public ArrayList<SignInInfo> NextMonthSignInInfos = new ArrayList<SignInInfo>();
}
