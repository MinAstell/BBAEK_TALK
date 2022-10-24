package com.bkm.firebase_test;

import java.util.HashMap;
import java.util.Map;

public class ChatTestRoom_Data2 {

    public static class Comment {
        public String userName = "";
        public String userContents = "";
        public Map<String, Object> readUsers = new HashMap<>();
        public String createBy = "";
        public String timestamp = "";
        public String userProfileImageUrl = "";

        public String getUserName() {
            return userName;
        }
    }

    public ChatTestRoom_Data2() {

    }
}