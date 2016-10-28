package com.example.skuo.happyassist.Javis.http;

/**
 * Created by Administrator on 16-7-14.
 */
public final class Interface {
    /**
     * 登陆
     */
    public final static String Login = "http://192.168.16.172:8099/Account/Login";
    /**
     * 获取小区列表
     */
    public final static String GetEstateList = "http://192.168.16.172:8099/Repair/GetEstateList?";
    /**
     * 获取组团列表
     */
    public final static String GetGroupList = "http://192.168.16.172:8099/UserAccount/GetGroupList?";
    /**
     * 获取楼幢列表
     */
    public final static String GetBuildingList = "http://192.168.16.172:8099/UserAccount/GetBuildingList?";
    /**
     * 获取单元列表
     */
    public final static String GetCellList = "http://192.168.16.172:8099/UserAccount/GetCellList?";
    /**
     * 获取住户列表
     */
    public final static String GetHouseList = "http://192.168.16.172:8099/UserAccount/GetHouseList?";
    /**
     * 获取维修工单列表
     */
    public final static String GetRepairList = "http://192.168.16.172:8099/Repair/GetRepairList?";
    public final static String GetRepairImageList = "http://192.168.16.172:8099/Repair/GetRepairImageList?";
    public final static String GetRepairHandleImageList = "http://192.168.16.172:8099/Repair/GetRepairHandleImageList?";
    public final static String UploadRepairPhoto = "http://192.168.16.172:8099/Repair/UploadRepairPhoto";
    public final static String SubmitRepairHandle = "http://192.168.16.172:8099/Repair/SubmitRepairHandle";
    public final static String GetCommonManagerList = "http://192.168.16.172:8099/Repair/GetCommonManagerList?";
    public final static String SubmitAssignHandle = "http://192.168.16.172:8099/Repair/SubmitAssignHandle";
    public final static String GetMyOrderList = "http://192.168.16.172:8099/MyOrder/GetMyOrderList?";
    public final static String GetGoodsOrderList = "http://192.168.16.172:8099/GoodsOrder/GetGoodsOrderList?";
    public final static String GetOrderDetail = "http://192.168.16.172:8099/GoodsOrder/GetOrderDetail?";
    public final static String SendGoods = "http://192.168.16.172:8099/GoodsOrder/SendGoods";
    public final static String Refund = "http://192.168.16.172:8099/GoodsOrder/Refund";
    public final static String Refuse = "http://192.168.16.172:8099/GoodsOrder/Refuse";
    public final static String GetComplaintList = "http://192.168.16.172:8099/Complaint/GetComplaintList?";
    public final static String GetComplaintImageList = "http://192.168.16.172:8099/Complaint/GetComplaintImageList?";
    public final static String GetComplaintHandleImageList = "http://192.168.16.172:8099/Complaint/GetComplaintHandleImageList?";
    public final static String SubmitComplaintHandle = "http://192.168.16.172:8099/Complaint/SubmitComplaintHandle";
    public final static String UploadComplaintPhoto = "http://192.168.16.172:8099/Complaint/UploadComplaintPhoto";
    public final static String SubmitAssignHandle1 = "http://192.168.16.172:8099/Complaint/SubmitAssignHandle";
    public final static String GetAppointmentList = "http://192.168.16.172:8099/Neighborhood/GetAppointmentList?";
    public final static String AppointmentDispatch = "http://192.168.16.172:8099/Neighborhood/AppointmentDispatch";
    public final static String GetUserAccountList = "http://192.168.16.172:8099/UserAccount/GetUserAccountList?";
    public final static String GetHouseholdInfo = "http://192.168.16.172:8099/UserAccount/GetHouseholdInfo?";
    public final static String GetUserAccountInfo = "http://192.168.16.172:8099/UserAccount/GetUserAccountInfo?";
    public final static String SubmitHouseholdInfo = "http://192.168.16.172:8099/UserAccount/SubmitHouseholdInfo";
    public final static String Authen = "http://192.168.16.172:8099/UserAccount/Authen";
    public final static String UnAuthen = "http://192.168.16.172:8099/UserAccount/UnAuthen";
    public final static String GetSignInList = "http://192.168.16.172:8099/SignIn/GetSignInList?";
    public final static String SignIn = "http://192.168.16.172:8099/SignIn/SignIn";
    public final static String GetAccountPicture = "http://192.168.16.172:8099/Account/GetAccountPicture?";
    public final static String UploadAccountPhoto = "http://192.168.16.172:8099/Account/UploadAccountPhoto";
    public final static String GetPhoneAppVersionCode = "http://192.168.16.172:8099/AppVersion/GetPhoneAppVersionCode?";
    public final static String GetPhoneAppVersionInfo = "http://192.168.16.172:8099/AppVersion/GetPhoneAppVersionInfo?";

    public Interface() {

    }
}
