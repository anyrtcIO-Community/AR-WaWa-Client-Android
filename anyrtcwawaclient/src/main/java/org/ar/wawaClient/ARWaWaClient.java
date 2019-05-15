package org.ar.wawaClient;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.emitter.Emitter;

/**
 * Created by liuxiaozhong on 2017/12/22.
 */

public class ARWaWaClient {

    private io.socket.client.Socket socket;

    static ARWaWaClient instance;

    private static String developerid="";

    private static String appid="";

    private static String appkey="";

    private static String apptoken="";

    private static String ROOMID="";

    private static String USERID="";

    private final String SERVER_URL = "http://120.77.18.113:9889";

    public static boolean hadConnSocketServer =false;

    public static boolean hadConnAnyRTCServer =false;

    private WaWaServerListener serverListener;

    public enum CMD {
        CMD_FORWARD (0) ,
        CMD_BACKWARD(1), // VIDEO PRO
        CMD_LEFT(2),     // AUDIO
        CMD_RIGHT(3),
        CMD_GRAB(4),
        CMD_SWITCH_CAMERA(5),;   //VIDEO MONITOR

        public final int type;
        CMD(int type) {
            this.type = type;
        }
    }


    public static ARWaWaClient getInstance() {
        if (instance == null) {
            synchronized (ARWaWaClient.class) {
                if (instance == null) {
                    instance = new ARWaWaClient();
                }
            }
        }
        return instance;
    }


    public void initEngineWithARInfo(String developerid, String appid, String appkey, String apptoken) {
        this.developerid = developerid;
        this.appid = appid;
        this.appkey = appkey;
        this.apptoken = apptoken;
    }


    public void setServerListener(WaWaServerListener serverListener) {
        if (serverListener==null) {
            throw new NullPointerException("WaWaServerListener can not be null !!");
        }
        this.serverListener = serverListener;
    }

    public void openServer(){
        try {
            if (socket!=null){
                return;
            }
            socket= IO.socket(SERVER_URL);
            socket.on(io.socket.client.Socket.EVENT_CONNECT,connect);
            socket.on(io.socket.client.Socket.EVENT_DISCONNECT,disconnect);
            socket.on(io.socket.client.Socket.EVENT_RECONNECT,reconnect);
            socket.on("init_success",init_success);
            socket.on("init_failed",init_failed);
            socket.on("on_get_room_list",on_get_room_list);
            socket.on("on_join_room",on_join_room);
            socket.on("on_leave_room",on_leave_room);

            socket.on("on_book_result",on_book_result);
            socket.on("on_send_cmd",on_send_cmd);

            socket.on("on_book_member_update",on_book_member_update);

            socket.on("on_send_ctrl_cmd",on_send_ctrl_cmd);
            socket.on("on_wawa_ready",on_wawa_ready);

            socket.on("on_wawa_result",on_wawa_result);

            socket.on("on_member_update",on_member_update);

            socket.on("on_wawa_action_timeout",on_wawa_action_timeout);
            socket.on("on_wawa_ready_timeout",on_wawa_ready_timeout);
            socket.on("on_wawa_leave_room",on_wawa_leave_room);
            socket.on("on_update_room_url",on_update_room_url);
            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }


    public Emitter.Listener connect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("wawaSocket","连接成功");
            if (serverListener!=null) {
                serverListener.onConnectServerSuccess();
                hadConnSocketServer=true;
                initAnyRTC();
            }
        }
    };


    public Emitter.Listener disconnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {

            Log.d("wawaSocket","连接断开");
            if (serverListener!=null) {
                serverListener.onDisconnect();
                hadConnSocketServer=false;
            }
        }
    };
    public Emitter.Listener reconnect=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("wawaSocket", "重新连接...");
            serverListener.onReconnect();
        }
    };
    public Emitter.Listener on_update_room_url=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("wawaSocket", "视频流更新通知...");
            serverListener.onRoomUrlUpdate("");
        }
    };

    public Emitter.Listener init_success=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("wawaSocket","初始化anyrtc信息成功");
            if (serverListener!=null) {
                serverListener.onInitAnyRTCSuccess();
                hadConnAnyRTCServer=true;
            }
        }
    };

    public Emitter.Listener init_failed=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("wawaSocket","初始化anyrtc信息失败");
            if (serverListener!=null) {
                serverListener.onInitAnyRTCFaild();
                hadConnAnyRTCServer=false;
            }
        }
    };

    public Emitter.Listener on_get_room_list=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","获取房间列表"+args[0].toString());
                if (serverListener!=null) {
                    serverListener.onGetRoomList(args[0].toString());
                }
            }

        }
    };
    public Emitter.Listener on_member_update=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                try {
                    JSONObject one = new JSONObject(args[0].toString());
                    String num=one.getJSONObject("data").getString("book_member");
                    if (serverListener!=null) {
                        serverListener.onRoomMemberUpdate(num);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("wawaSocket","房间人数"+args[0].toString());

            }

        }
    };

    public Emitter.Listener on_join_room=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","加入房间成功"+args[0].toString());
                try {
                    JSONObject jsonObject = new JSONObject(args[0].toString());
                    int code=jsonObject.getInt("code");
                    String member=jsonObject.getJSONObject("data").getString("room_member");
                    String url=jsonObject.getJSONObject("data").getString("url_list");
                    if (serverListener!=null) {
                        serverListener.onJoinRoom(code,url,member);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };

    public Emitter.Listener on_book_member_update=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","预约人数更新"+args[0].toString());
                try {
                    JSONObject one = new JSONObject(args[0].toString());
                    String num=one.getJSONObject("data").getString("book_member");
                    if (serverListener!=null) {
                        serverListener.onBookMemberUpdate(num);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };

    public Emitter.Listener on_book_result=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                try {
                    JSONObject one=new JSONObject(args[0].toString());
                    int code = one.getInt("code");
                    String num = one.getJSONObject("data").getString("book_member");
                    String cmd=new JSONObject(new JSONObject(one.getJSONObject("data").getString("data")).getString("data")).getString("cmd");
                    if (serverListener!=null) {
                        if (!TextUtils.isEmpty(cmd)) {
                            if (cmd.equals("book")) {
                                serverListener.onBookResult(code,num);
                            }else  if (cmd.equals("unbook")){
                                serverListener.onUnBookResult(code,num);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("wawaSocket","预约游戏成功"+args[0].toString());
            }

        }
    };

    public Emitter.Listener on_send_cmd=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","发送命令"+args[0].toString());

            }

        }
    };
    public Emitter.Listener on_wawa_action_timeout =new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","抓娃娃超时"+args[0].toString());
                if (serverListener!=null){
                    serverListener.onPlayTimeout();
                }
            }

        }
    };
    public Emitter.Listener on_wawa_ready_timeout =new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","准备超时"+args[0].toString());
                if (serverListener!=null){
                    serverListener.onReadyTimeout();
                }
            }

        }
    };

    public Emitter.Listener  on_send_ctrl_cmd=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","指令返回"+args[0].toString());
                try {
                    JSONObject jsonObject=new JSONObject(args[0].toString());
                    int code=jsonObject.getInt("code");
                    JSONObject data=new JSONObject(jsonObject.getString("data"));
                    String cmd=data.getJSONObject("data").getString("cmd");
                    if (serverListener!=null){
                        serverListener.onControlCmd(code,cmd);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    };

    public Emitter.Listener  on_wawa_ready=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","排队抓娃娃准备"+args[0].toString());
                if (serverListener!=null) {
                    serverListener.onReadyStart();
                }
            }

        }
    };

    public Emitter.Listener on_wawa_result=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","抓娃娃结果"+args[0].toString());
                try {
                    boolean result=new JSONObject(args[0].toString()).getJSONObject("data").getBoolean("result");
                    if (serverListener!=null) {
                        serverListener.onResult(result);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    };

    public Emitter.Listener  on_leave_room=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","离开房间成功"+args[0].toString());
                try {
                    JSONObject jsonObject=new JSONObject(args[0].toString());
                    int code=jsonObject.getInt("code");
                    if (serverListener!=null) {
                        serverListener.onLeaveRoom(code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    };

    public Emitter.Listener  on_wawa_leave_room=new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args.length>0&& !TextUtils.isEmpty(args[0].toString())){
                Log.d("wawaSocket","娃娃机离开"+args[0].toString());
                try {
                    JSONObject jsonObject=new JSONObject(args[0].toString());
                    int code=jsonObject.getInt("code");
                    if (serverListener!=null) {
                        serverListener.onWaWaLeave(code);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

        }
    };


    //method

    private void initAnyRTC() {
        if (TextUtils.isEmpty(developerid)||TextUtils.isEmpty(appid)||TextUtils.isEmpty(appkey)||TextUtils.isEmpty(apptoken)) {
            throw new NullPointerException("do you have initAnyRTC ? some params is null");
        }
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("developerid", developerid);
            jsonObject.put("appid",appid);
            jsonObject.put("appkey",appkey);
            jsonObject.put("apptoken",apptoken);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("init_anyrtc",jsonObject.toString());
    }



    public void getRoomList(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("appid",appid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("get_room_list",jsonObject.toString());
    }


    public void joinRoom(String roomID,String userid,String username,String userIcon){
        this.ROOMID=roomID;
        this.USERID=userid;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",roomID);
            jsonObject.put("userid", userid);
            jsonObject.put("username",username);
            jsonObject.put("usericon",userIcon);
            jsonObject.put("usertype",1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("join_room",jsonObject.toString());
    }

    public void leaveRoom(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",ROOMID);
            jsonObject.put("userid", USERID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("leave_room",jsonObject.toString());
    }

    public void book(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",ROOMID);
            jsonObject.put("data", getCmd("book"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_cmd",jsonObject.toString());
    }

    public void unbook(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",ROOMID);
            jsonObject.put("data", getCmd("unbook"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_cmd",jsonObject.toString());
    }


    public void sendControlCmd(CMD cmd){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",ROOMID);
            if (cmd==CMD.CMD_FORWARD){
                jsonObject.put("data", getCmd("up"));
            }else if (cmd==CMD.CMD_BACKWARD){
                jsonObject.put("data", getCmd("down"));
            }else if (cmd==CMD.CMD_LEFT){
                jsonObject.put("data", getCmd("left"));
            }else if (cmd==CMD.CMD_RIGHT){
                jsonObject.put("data", getCmd("right"));
            }else if (cmd==CMD.CMD_GRAB){
                jsonObject.put("data", getCmd("action"));
            }else if (cmd==CMD.CMD_SWITCH_CAMERA){
                jsonObject.put("data", getCmd("transform"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_ctrl_cmd",jsonObject.toString());

    }








    public void play(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",ROOMID);
            jsonObject.put("data", getCmd("go"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_cmd",jsonObject.toString());
    }

    public void canclePlay(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("anyrtcid",ROOMID);
            jsonObject.put("data", getCmd("ungo"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        socket.emit("send_cmd",jsonObject.toString());
    }

    public JSONObject getCmd(String cmd){
        JSONObject jsonObject=new JSONObject();
        try {
            jsonObject.put("cmd",cmd);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public void closeServer(){
        socket.off();
        socket.close();
    }
}
