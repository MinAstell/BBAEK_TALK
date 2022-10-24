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

        // 1. 알림 메시지를 관리하는 notificationManager 객체 추출
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = getNotificationBuilder(notificationManager, "chennal id", "첫번째 채널입니다");

        builder.setContentTitle(title)       // 콘솔에서 설정한 타이틀
                .setContentText(body)         // 콘솔에서 설정한 내용
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)// 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                .setAutoCancel(true);             // 메시지를 터치하면 메시지가 자동으로 제거됨

        notificationManager.notify((int) System.currentTimeMillis(), builder.build()); // 고유숫자로 노티피케이션 동작시킴
    }


    /**
     * @내용 : 안드로이드 8.0 이상부터 Notification은 채널별로 관리해야만 함
     * (channel별이라함은 Notification을 보낼때마다 막쌓이는것이 아니고 앱별로 그룹지어서 쌓이도록 하는 것 )
     **/
    protected NotificationCompat.Builder getNotificationBuilder(NotificationManager notificationManager, String channelId, CharSequence channelName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 2. NotificationChannel채널 객체 생성 (첫번재 인자: 관리id, 두번째 인자: 사용자에게 보여줄 채널 이름)
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);

            // 3. 알림 메시지를 관리하는 객체에 노티피케이션 채널을 등록
            notificationManager.createNotificationChannel(channel);
            builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
            return builder;

        } else { // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

            builder.setSmallIcon(R.mipmap.ic_launcher);
            return builder;
        }
    }
}
