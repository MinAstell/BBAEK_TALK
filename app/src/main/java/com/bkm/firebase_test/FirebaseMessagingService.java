package com.bkm.firebase_test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private DatabaseReference mDatabase;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("myToken", "Token : " + s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getData().size() > 0) {

            mDatabase = FirebaseDatabase.getInstance().getReference("UserInfo");

            String title = remoteMessage.getData().get("title");
            String body = remoteMessage.getData().get("body");
            String friendName = remoteMessage.getData().get("sendingUser");

            sendNotification(title, body);
        }
    }

    private void sendNotification(String title, String body) {

        // 0. Pending Intent
        Intent notificationIntent = new Intent(this, LoginActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // 1. ?????? ???????????? ???????????? notificationManager ?????? ??????
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = getNotificationBuilder(notificationManager, "chennal id", "????????? ???????????????");

        builder.setContentTitle(title)       // ???????????? ????????? ?????????
                .setContentText(body)         // ???????????? ????????? ??????
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)// ???????????? ????????????????????? ?????? ResultActivity??? ??????????????? ??????
                .setAutoCancel(true);             // ???????????? ???????????? ???????????? ???????????? ?????????

        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // ??????????????? ?????????????????? ????????????
    }


    /**
     * @?????? : ??????????????? 8.0 ???????????? Notification??? ???????????? ??????????????? ???
     * (channel??????????????? Notification??? ??????????????? ?????????????????? ????????? ????????? ??????????????? ???????????? ?????? ??? )
     **/
    protected NotificationCompat.Builder getNotificationBuilder(NotificationManager notificationManager, String channelId, CharSequence channelName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2. NotificationChannel?????? ?????? ?????? (????????? ??????: ??????id, ????????? ??????: ??????????????? ????????? ?????? ??????)
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            // 3. ?????? ???????????? ???????????? ????????? ?????????????????? ????????? ??????
            notificationManager.createNotificationChannel(channel);
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap ????????? Oreo ???????????? ????????? UI ?????????
            return builder;

        } else { // Oreo ???????????? mipmap ???????????? ????????? Couldn't create icon: StatusBarIcon ?????????

            builder.setSmallIcon(R.mipmap.ic_launcher);
            return builder;
        }
    }
}
