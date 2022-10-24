package com.bkm.firebase_test;

import java.util.HashMap;
import java.util.Map;

public class DataList {
    private String userId;
    private String userPassword;
    private String userName;
    private String profileImageUrl;
    private String userToken;
    private String conditionMessage;
    private String userUid;
//    private Map<String, Object> friends = new HashMap<>();

    public DataList() {

    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getConditionMessage() {
        return conditionMessage;
    }

    public void setConditionMessage(String conditionMessage) {
        this.conditionMessage = conditionMessage;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

//    public Map<String, Object> getFriends() {
//        return friends;
//    }
//
//    public void setFriends(Map<String, Object> friends) {
//        this.friends = friends;
//    }
}
