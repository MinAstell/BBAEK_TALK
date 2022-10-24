package com.bkm.firebase_test;

public class ChatHome_Data {
    private int iv_profile;
    private String tv_nick;
    private String tv_talk;

    public ChatHome_Data(int iv_profile, String tv_nick, String tv_talk) {
        this.iv_profile = iv_profile;
        this.tv_nick = tv_nick;
        this.tv_talk = tv_talk;
    }

    public int getIv_profile() {
        return iv_profile;
    }

    public void setIv_profile(int iv_profile) {
        this.iv_profile = iv_profile;
    }

    public String getTv_nick() {
        return tv_nick;
    }

    public void setTv_nick(String tv_nick) {
        this.tv_nick = tv_nick;
    }

    public String getTv_talk() {
        return tv_talk;
    }

    public void setTv_talk(String tv_talk) {
        this.tv_talk = tv_talk;
    }
}