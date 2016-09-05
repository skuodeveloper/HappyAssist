package com.example.skuo.happyassist.Class.Result;

import java.io.Serializable;

/**
 * Created by Administrator on 16-7-18.
 */
public class AppVersionInfo implements Serializable {
    public int ID;

    /**
     * APP版本号
     */
    public String AppVersion;

    /**
     * 是否需要强制更新
     */
    public int Update;

    /**
     * 下载路径
     */
    public String DownloadUrl;
}
