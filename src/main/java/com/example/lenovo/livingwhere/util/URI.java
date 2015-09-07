package com.example.lenovo.livingwhere.util;

/**
 * Created by Halo on 2015/8/31.
 */
public class URI {
    private final static String ServerAddr = "http://115.28.85.146/Zhunaer/";
    public static final String RegisterAddr = ServerAddr + "action/user_register";// 用户注册
    public static final String LoginAddr = ServerAddr + "action/user_login";//用户登录
    public static final String FindHouseNearByAddr = ServerAddr+"action/msg_findHousesNearby";//推荐附近房子
    public static final String MatchHousesAddr = ServerAddr+"action/msg_matchHouses";//根据条件筛选房子
    public static final String GetCommentsAddr = ServerAddr+"action/msg_getComments";//查看评论
    public static final String GetMyBookHistoryAddr = ServerAddr+"action/msg_getMyBookHistory";//查看我的预约历史
    public static final String GetMyReleasedHousesAddr = ServerAddr+"action/msg_getMyReleasedHouses";//查看我发布的房子
    public static final String GetOtherHousesAddr = ServerAddr+"action/msg_getOtherHouses";//查看房东其他房子
    public static final String UpdateMyInfoAddr = ServerAddr+"action/modify_updateMyInfo";//用户修改个人信息
    public static final String AddToBookHistoryAddr = ServerAddr+"action/modify_addToBookHistory";//用户添加进预约列表
    public static final String ChangeBookStateAddr = ServerAddr+"action/modify_changeBookState";//房主接受预约，用户确认完成交易
    public static final String StarScaleAddr = ServerAddr+"action/modify_starScale";//用户评论星级
    public static final String AddCommentsAddr = ServerAddr+"action/modify_addComments";//添加评论
    public static final String ReleaseHousesAddr = ServerAddr+"action/modify_releaseHouses";//房主发布住房
    public static final String UpdateMyHouseInfoAddr = ServerAddr+"action/modify_updateMyHouseInfo";//房主修改房间信息
    public static final String ToBeOwnerAddr = ServerAddr+"action/modify_toBeAOwner";//成为房主
    public static final String ChangeStateOfHousesAddr = ServerAddr+"action/modify_changeStateOfHouses";//控制房子上下架
    public static final String HeadPic  = ServerAddr+"upload/headPic/";//用户头像前缀
    public static final String CommentsPic  = ServerAddr+"upload/commentsPic/";//评论图片前缀
    public static final String HousesPic  = ServerAddr+"upload/housesPic/";//房子图片前缀
}
