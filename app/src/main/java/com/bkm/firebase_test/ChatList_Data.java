package com.bkm.firebase_test;

public class ChatList_Data {

    private String iv_friendProfile;
    private String tv_friendName;
    private String tv_lastMessage;

    public ChatList_Data(String iv_friendProfile, String tv_friendName, String tv_lastMessage) {
        this.iv_friendProfile = iv_friendProfile;
        this.tv_friendName = tv_friendName;
        this.tv_lastMessage = tv_lastMessage;
    }

    public String getIv_friendProfile() {
        return iv_friendProfile;
    }

    public void setIv_friendProfile(String iv_friendProfile) {
        this.iv_friendProfile = iv_friendProfile;
    }

    public String getTv_friendName() {
        return tv_friendName;
    }

    public void setTv_friendName(String tv_friendName) {
        this.tv_friendName = tv_friendName;
    }

    public String getTv_lastMessage() {
        return tv_lastMessage;
    }

    public void setTv_lastMessage(String tv_lastMessage) {
        this.tv_lastMessage = tv_lastMessage;
    }
}