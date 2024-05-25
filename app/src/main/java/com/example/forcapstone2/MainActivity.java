package com.example.forcapstone2;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private ImageButton informationButton; // 정보 팝업 버튼 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MyApp myApp = (MyApp) getApplication(); // 앱 전체에서 사용되는 변수에 접근하기 위해 MyApp 인스턴스 생성

        createNotificationChannel(); // 알림 채널 생성 메소드 호출

        ImageView button = findViewById(R.id.button); // 알림 발생 버튼
        ImageView button2 = findViewById(R.id.button2); // 화면 전환 버튼
        ImageView buttonSetting = findViewById(R.id.buttonSetting); // 설정창 액티비티로 이동하는 버튼
        ImageView statisticsIcon = findViewById(R.id.statisticsIcon); // 통계 화면으로 이동하는 아이콘
        ImageView bluetoothIcon = findViewById(R.id.bluetoothIcon); // 블루투스 설정 액티비티로 이동하는 아이콘
        ImageView reloadIcon = findViewById(R.id.reloadIcon); // 새로고침 아이콘

        final WaterCupView waterCupView = findViewById(R.id.waterCupView); // 물컵 뷰

        // 알림 발생 버튼 클릭 이벤트
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myApp.setCurrentAmount(); // 현재 물 섭취량 설정
                if (myApp.getCurrentAmount() > myApp.getGoalAmount()) {
                    createNotification(); // 목표를 초과하면 알림 생성
                }
            }
        });

        // 화면 전환 버튼 클릭 이벤트
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myApp.setCurrentAmount(); // 현재 물 섭취량 설정
                if (myApp.getCurrentAmount() > myApp.getGoalAmount()) {
                    waterCupView.setVisibility(View.GONE); // 목표를 초과하면 물컵 뷰 숨김
                }
            }
        });

        // 설정창 버튼 클릭 이벤트
        buttonSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent); // 설정 액티비티 시작
            }
        });

        // 통계 아이콘 클릭 이벤트
        statisticsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Stat.class);
                startActivity(intent); // 통계 액티비티 시작
            }
        });

        // 블루투스 아이콘 클릭 이벤트
        bluetoothIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BluetoothConnect.class);
                startActivity(intent); // 블루투스 연결 액티비티 시작
            }
        });

        // 새로고침 아이콘 클릭 이벤트
        reloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "지금까지 마신 양이에요", Toast.LENGTH_SHORT).show(); // 새로고침 성공 토스트 메시지
            }
        });

        informationButton = findViewById(R.id.informationButton); // 정보 팝업 버튼

        // 정보 팝업 버튼 클릭 이벤트
        informationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInformationPopup(); // 정보 팝업창 표시 메소드 호출
            }
        });
    }

    /**
     * 정보 팝업창을 표시하는 메소드
     */
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

    /**
     * 알림 채널을 생성하는 메소드 (Android 8.0 이상에서 필요)
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name); // 채널 이름
            String description = getString(R.string.channel_description); // 채널 설명
            int importance = NotificationManager.IMPORTANCE_DEFAULT; // 중요도 설정
            NotificationChannel channel = new NotificationChannel("GOALATTAINMENT_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel); // 시스템에 채널 등록
        }
    }

    /**
     * 목표 달성 시 알림을 생성하는 메소드
     */
    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "GOALATTAINMENT_CHANNEL_ID")
                .setSmallIcon(R.drawable.hirue) // 알림에 보여질 아이콘 설정
                .setContentTitle("목표 달성 알림") // 알림 제목 설정
                .setContentText("축하합니다~!! 일일 목표 달성했습니다!") // 알림 내용 설정
                .setPriority(NotificationCompat.PRIORITY_DEFAULT); // 알림의 우선순위 설정

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 없으면 리턴
            return;
        }
        notificationManager.notify(1, builder.build()); // 알림 발생
    }
}