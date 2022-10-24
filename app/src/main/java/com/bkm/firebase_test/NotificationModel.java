package com.bkm.firebase_test;

import android.app.Notification;

public class NotificationModel {

    public String to;

    public Notification notification = new Notification();
    public Data data = new Data();

    public static class Notification {

        public String title;
        public String body;
    }

    public static class Data {
        public String title;
        public String body;
        public String sendingUser;
    }
}
