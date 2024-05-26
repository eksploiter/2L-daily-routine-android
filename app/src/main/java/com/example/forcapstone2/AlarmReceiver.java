package com.example.forcapstone2;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        // 노티피케이션 매니저 생성
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // PendingIntent 생성
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, FLAG_IMMUTABLE);

        // 노티피케이션 빌더 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.hirue);

            // 채널 생성 및 설정
            String channelName = "매일 알람 채널";
            String description = "매일 정해진 시간에 알람";
            int importance = notificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            // 채널을 시스템에 등록
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        } else {
            // 오래된 버전을 위한 아이콘 설정
            builder.setSmallIcon(R.mipmap.hirue_icon);
        }

        // 노티피케이션 설정
        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("{Time to watch soem cool stuff!}")
                .setContentTitle("목표치 미달성 알림")
                .setContentText("오늘 섭취한 양이 목표치에 도달하지 못했습니다 ㅠㅠ \n얼른 물을 마셔주세요~~!!")
                .setContentInfo("INFO")
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            // 노티피케이션 발생
            notificationManager.notify(1234, builder.build());

            // 다음 알람 시간 설정
            Calendar nextNotifyTime = Calendar.getInstance();
            nextNotifyTime.add(Calendar.DATE, 1);

            // Preference에 다음 알람 시간 저장
            SharedPreferences.Editor editor = context.getSharedPreferences("daily alarm", Context.MODE_PRIVATE).edit();
            editor.putLong("nextNotifyTime", nextNotifyTime.getTimeInMillis());
            editor.apply();

            // 다음 알람 시간 토스트 메시지로 표시
            Date currentDateTime = nextNotifyTime.getTime();
            String date_text = new SimpleDateFormat("yyyy년 MM월 dd일 EE요일 a hh시 mm분 ", Locale.getDefault()).format(currentDateTime);
            Toast.makeText(context.getApplicationContext(), "다음 알람은 " + date_text + "으로 알람이 설정되었습니다!", Toast.LENGTH_SHORT).show();
        }
    }
}
