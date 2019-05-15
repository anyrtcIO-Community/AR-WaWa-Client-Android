package org.anyrtc.wawa;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuxiaozhong on 2017/12/7.
 */

public class RoomBean implements Serializable{

    /**
     * code : 0
     * data : {"roomlist":[{"roomid":79,"room_name":"849905","room_appid":"anyrtcy0UkK1PZydLX","room_anyrtcid":"849905","room_userid":"ffffffff-9936-70d7-0000-00002c6edcf5","room_username":"娃娃机","room_usericon":"","room_url":"{\"RtcpUrl\":\"X000mXCaPuK2k0o9nqV2cOlzNCg0\",\"H5LiveUrl\":\"ws://101.132.132.166:8082/849905\"}","room_member":8,"room_state":0,"room_leave_time":"0000-00-00 00:00:00","room_create_at":"2017-12-06T16:30:51.000Z"},{"roomid":81,"room_name":"126892","room_appid":"anyrtcy0UkK1PZydLX","room_anyrtcid":"126892","room_userid":"ffffffff-9936-70d7-0000-00002c6edcf5","room_username":"娃娃机","room_usericon":"","room_url":"{\"RtcpUrl\":\"X00055DyTnZI0DKgXNZAlmNV6yRS\",\"H5LiveUrl\":\"ws://101.132.132.166:8082/126892\"}","room_member":8,"room_state":0,"room_leave_time":"0000-00-00 00:00:00","room_create_at":"2017-12-06T16:23:44.000Z"},{"roomid":83,"room_name":"617824","room_appid":"anyrtcy0UkK1PZydLX","room_anyrtcid":"617824","room_userid":"ffffffff-9936-70d7-0000-00002c6edcf5","room_username":"娃娃机","room_usericon":"","room_url":"{\"RtcpUrl\":\"X000RzZ4q8wVxVSzLjM9CSFQEm0o\",\"H5LiveUrl\":\"ws://101.132.132.166:8082/617824\"}","room_member":8,"room_state":0,"room_leave_time":"0000-00-00 00:00:00","room_create_at":"2017-12-06T15:50:07.000Z"}]}
     * message : get room list success
     */
    public static final int TYPE = 1;
    private int code;
    private DataBean data;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataBean {
        private List<RoomlistBean> roomlist;

        public List<RoomlistBean> getRoomlist() {
            return roomlist;
        }

        public void setRoomlist(List<RoomlistBean> roomlist) {
            this.roomlist = roomlist;
        }

        public static class RoomlistBean implements Serializable{
            /**
             * roomid : 79
             * room_name : 849905
             * room_appid : anyrtcy0UkK1PZydLX
             * room_anyrtcid : 849905
             * room_userid : ffffffff-9936-70d7-0000-00002c6edcf5
             * room_username : 娃娃机
             * room_usericon :
             * room_url : {"RtcpUrl":"X000mXCaPuK2k0o9nqV2cOlzNCg0","H5LiveUrl":"ws://101.132.132.166:8082/849905"}
             * room_member : 8
             * room_state : 0
             * room_leave_time : 0000-00-00 00:00:00
             * room_create_at : 2017-12-06T16:30:51.000Z
             */

            private int roomid;
            private String room_name;
            private String room_appid;
            private String room_anyrtcid;
            private String room_userid;
            private String room_username;
            private String room_usericon;
            private String room_url;
            private int room_member;
            private int room_state;
            private String room_leave_time;
            private String room_create_at;

            public int getRoomid() {
                return roomid;
            }

            public void setRoomid(int roomid) {
                this.roomid = roomid;
            }

            public String getRoom_name() {
                return room_name;
            }

            public void setRoom_name(String room_name) {
                this.room_name = room_name;
            }

            public String getRoom_appid() {
                return room_appid;
            }

            public void setRoom_appid(String room_appid) {
                this.room_appid = room_appid;
            }

            public String getRoom_anyrtcid() {
                return room_anyrtcid;
            }

            public void setRoom_anyrtcid(String room_anyrtcid) {
                this.room_anyrtcid = room_anyrtcid;
            }

            public String getRoom_userid() {
                return room_userid;
            }

            public void setRoom_userid(String room_userid) {
                this.room_userid = room_userid;
            }

            public String getRoom_username() {
                return room_username;
            }

            public void setRoom_username(String room_username) {
                this.room_username = room_username;
            }

            public String getRoom_usericon() {
                return room_usericon;
            }

            public void setRoom_usericon(String room_usericon) {
                this.room_usericon = room_usericon;
            }

            public String getRoom_url() {
                return room_url;
            }

            public void setRoom_url(String room_url) {
                this.room_url = room_url;
            }

            public int getRoom_member() {
                return room_member;
            }

            public void setRoom_member(int room_member) {
                this.room_member = room_member;
            }

            public int getRoom_state() {
                return room_state;
            }

            public void setRoom_state(int room_state) {
                this.room_state = room_state;
            }

            public String getRoom_leave_time() {
                return room_leave_time;
            }

            public void setRoom_leave_time(String room_leave_time) {
                this.room_leave_time = room_leave_time;
            }

            public String getRoom_create_at() {
                return room_create_at;
            }

            public void setRoom_create_at(String room_create_at) {
                this.room_create_at = room_create_at;
            }
        }
    }
}
