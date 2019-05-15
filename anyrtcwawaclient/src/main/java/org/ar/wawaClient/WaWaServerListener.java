package org.ar.wawaClient;

/**
 * Created by liuxiaozhong on 2017/12/22.
 */

public interface WaWaServerListener {
    //连接服务成功
    void onConnectServerSuccess();
    //与服务断开连接
    void onDisconnect();
    //重连服务
    void onReconnect();
    //初始化anyRTC成功
    void onInitAnyRTCSuccess();
    //初始化anyRtc失败
    void onInitAnyRTCFaild();

    /**
     * 获取房间列表
     * @param strRoomList 房间列表Json
     */
    void onGetRoomList(String strRoomList);

    /**
     * 加入房间
     * @param code 状态码
     * @param videoInfo 视频流信息
     * @param strMemberNum 房间内人数
     */
    void onJoinRoom(int code,String videoInfo,String strMemberNum);

    /**
     * 离开房间
     * @param code 状态码
     */
    void onLeaveRoom(int code);

    void onWaWaLeave(int code);

    /**
     * 预约结果
     * @param code 状态码
     * @param strBookNum 预约人数
     */
    void onBookResult(int code,String strBookNum);

    /**
     * 取消预约结果
     * @param code 状态码
     * @param strBookNum 预约人数
     */
    void onUnBookResult(int code,String strBookNum);

    /**
     * 指令回掉
     * @param code 状态码
     * @param cmd 指令 left up ...
     */
    void onControlCmd(int code, String cmd);

    void onRoomUrlUpdate(String strVideoInfo);

    /**
     * 预约人数更新
     * @param strBookMemberNum 预约人数
     */
    void onBookMemberUpdate(String strBookMemberNum);

    /**
     * 房间人数更新
     * @param strMemberNum 房间人数
     */
    void onRoomMemberUpdate(String strMemberNum);

    /**
     * 准备开始通知
     */
    void onReadyStart();

    /**
     * 准备超时通知
     */
    void onReadyTimeout();

    /**
     * 抓娃娃超时
     */
    void onPlayTimeout();

    /**
     * 抓娃娃结果
     * @param result true get false not get
     */
    void onResult(boolean result);
}
