package com.example.forcapstone2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private ImageButton informationButton;
    private Switch switch1;
    private boolean isDarkTheme = false; // 현재 테마 상태를 추적합니다.

    private WaterCupDark waterCupDark;
    private int currentAmount = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 앱 객체 가져오기
        MyApp myApp = (MyApp) getApplication();

        // 알림 채널 생성
        createNotificationChannel();

        // XML에서 뷰들을 찾아서 변수에 할당
        waterCupDark = findViewById(R.id.waterCupDark);
        ImageView button = findViewById(R.id.button);
        ImageView button2 = findViewById(R.id.button2);
        ImageView buttonSetting = findViewById(R.id.buttonSetting);
        ImageView statisticsIcon = findViewById(R.id.statisticsIcon);
        ImageView bluetoothIcon = findViewById(R.id.bluetoothIcon);
        ImageView reloadIcon = findViewById(R.id.reloadIcon);
        switch1 = findViewById(R.id.switch1);

        // 버튼 클릭 리스너 설정
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApp.getTodayAmount();
                if (myApp.getTodayAmount() > myApp.getGoalAmount()) {
                    createNotification();
                    myApp.drainAmount();
                }
                Toast.makeText(getApplicationContext(), "물을 버렸어요!", Toast.LENGTH_SHORT).show();
            }
        });

        // 버튼2 클릭 리스너 설정
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.getTodayAmount();
                if (myApp.getTodayAmount() > myApp.getGoalAmount()) {
                    createNotification();
                    myApp.drinkAmount();
                }
                Toast.makeText(getApplicationContext(), "물을 추가했어요!", Toast.LENGTH_SHORT).show();
            }
        });

        // 스위치 클릭 리스너 설정
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleTheme();
                Toast.makeText(getApplicationContext(), "화면 변환 성공!", Toast.LENGTH_SHORT).show();
            }
        });

        // 설정 버튼 클릭 리스너 설정
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // 통계 아이콘 클릭 리스너 설정
        statisticsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Stat.class);
                startActivity(intent);
            }
        });

        // 블루투스 아이콘 클릭 리스너 설정
        bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothConnect.class);
                startActivity(intent);
            }
        });

        // 새로고침 아이콘 클릭 리스너 설정
        reloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "새로고침 성공!", Toast.LENGTH_SHORT).show();
                myApp.reloadAmount();
            }
        });

        // 정보 버튼 클릭 리스너 설정
        informationButton = findViewById(R.id.informationButton);
        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformationPopup();
            }
        });
    }

    // 테마 전환 메서드
    private void toggleTheme() {
        isDarkTheme = !isDarkTheme;
        if (isDarkTheme) {
            setContentView(new WaterCupDark(this));
        } else {
            setContentView(new WaterCupView(this));
        }
    }

    // 정보 팝업창 표시 메서드
    private void showInformationPopup() {
        Dialog dialog = new Dialog(MainActivity.this, R.style.RoundedDialog);
        dialog.setContentView(R.layout.popup_activity);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        params.width = (int) (width * 0.85);
        params.height = (int) (height * 0.7);
        params.gravity = Gravity.CENTER;

        window.setAttributes(params);
        dialog.show();
    }

    // 알림 채널 생성 메서드
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("GOALATTAINMENT_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    // 알림 생성 메서드
    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "GOALATTAINMENT_CHANNEL_ID")
                .setSmallIcon(R.drawable.hirue)
                .setContentTitle("목표 달성 알림")
                .setContentText("축하합니다~!! 일일 목표 달성했습니다!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(1, builder.build());
    }
}
