package com.bkm.firebase_test;

public class FriendsList_Data {

    private String iv_friendProfileImage;
    private String tv_friendName;
    private String tv_friendConditionMessage;

    public FriendsList_Data(String iv_friendProfileImage, String tv_friendName, String tv_friendConditionMessage) {
        this.iv_friendProfileImage = iv_friendProfileImage;
        this.tv_friendName = tv_friendName;
        this.tv_friendConditionMessage = tv_friendConditionMessage;
    }

    public String getIv_friendProfileImage() {
        return iv_friendProfileImage;
    }

    public void setIv_friendProfileImage(String iv_friendProfileImage) {
        this.iv_friendProfileImage = iv_friendProfileImage;
    }

    public String getTv_friendName() {
        return tv_friendName;
    }

    public void setTv_friendName(String tv_friendName) {
        this.tv_friendName = tv_friendName;
    }

    public String getTv_friendConditionMessage() {
        return tv_friendConditionMessage;
    }

    public void setTv_friendConditionMessage(String tv_friendConditionMessage) {
        this.tv_friendConditionMessage = tv_friendConditionMessage;
    }
}