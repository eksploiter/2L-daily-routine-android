package com.example.forcapstone2;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private ImageButton informationButton;
    private Switch switch1, switch2;
    private FrameLayout lightThemeLayout, darkThemeLayout;
    private ImageView buttonSetting, statisticsIcon, bluetoothIcon, reloadIcon;
    private TextView nowAmount, waterAmountText, soFarText, farText, settingTextView, bluetoothTextView, statsTextView, refreshTextView;
    private MyApp myApp;
    private int currentAmount = 0;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (BluetoothService.readValue != null) {
                double readValue = BluetoothService.readValue;
                int converted = (int) (readValue * 1000);
                myApp.reloadAmount(converted);
                waterAmountText.setText(myApp.getTodayAmount() + "mL");
                nowAmount.setText(converted + "mL");
            }
            setMainTextVeiw(myApp.getTodayAmount(), myApp.getGoalAmount(), myApp.getBeforeAmount());
        }
    };

    private final Handler handlerdrain = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (BluetoothService.readValue != null) {
                double readValue = BluetoothService.readValue;
                int converted = (int) (readValue * 1000);
                myApp.drainAmount(converted);
                waterAmountText.setText(myApp.getTodayAmount() + "mL");
                nowAmount.setText(myApp.getBeforeAmount() + "mL");
            }
            setMainTextVeiw(myApp.getTodayAmount(), myApp.getGoalAmount(), myApp.getBeforeAmount());
        }
    };

    private final Handler handlerdrink = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (BluetoothService.readValue != null) {
                double readValue = BluetoothService.readValue;
                int converted = (int) (readValue * 1000);
                myApp.drinkAmount(converted);
                myApp.setTodayAmount(converted);
                waterAmountText.setText(myApp.getTodayAmount() + "mL");
                nowAmount.setText(converted + "mL");
            }
            setMainTextVeiw(myApp.getTodayAmount(), myApp.getGoalAmount(), myApp.getBeforeAmount());
        }
    };

    private BluetoothService bluetoothService;
    private boolean isBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            Log.d("BluetoothService", "ServiceConnection-onServiceConnected");
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private static final int REQUEST_PERMISSIONS = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myApp = (MyApp) getApplication();

        // Request necessary permissions
        requestPermissions();

        createNotificationChannel();

        // Initialize views
        switch1 = findViewById(R.id.switch1);
        switch2 = findViewById(R.id.switch2);
        lightThemeLayout = findViewById(R.id.lightThemeLayout);
        darkThemeLayout = findViewById(R.id.darkThemeLayout);
        nowAmount = findViewById(R.id.nowAmount);
        waterAmountText = findViewById(R.id.waterAmountText);
        soFarText = findViewById(R.id.soFarText);
        farText = findViewById(R.id.farText);
        settingTextView = findViewById(R.id.settingTextView);
        bluetoothTextView = findViewById(R.id.bluetoothTextView);
        statsTextView = findViewById(R.id.statsTextView);
        refreshTextView = findViewById(R.id.refreshTextView);
        ImageView button = findViewById(R.id.button);
        ImageView button2 = findViewById(R.id.button2);
        buttonSetting = findViewById(R.id.buttonSetting);
        statisticsIcon = findViewById(R.id.statisticsIcon);
        bluetoothIcon = findViewById(R.id.bluetoothIcon);
        reloadIcon = findViewById(R.id.reloadIcon);
        informationButton = findViewById(R.id.informationButton);
        Button format = findViewById(R.id.format);

        // activity_main.xml의 TextView에 목표량 연결
        TextView purposewaterAmountText = findViewById(R.id.PurposewaterAmountText);
        String goalAmountText = "목표량 " + String.valueOf((float) myApp.getGoalAmount() / 1000 + "L");
        purposewaterAmountText.setText(goalAmountText);


        waterAmountText.setText(myApp.getTodayAmount() + "mL");
        nowAmount.setText(myApp.getBeforeAmount() + "mL");


        setMainTextVeiw(myApp.getTodayAmount(), myApp.getGoalAmount(), myApp.getBeforeAmount());

        createNotificationChannel();
        resetAlarm(MainActivity.this);

        // Bind to BluetoothService
        Intent serviceIntent = new Intent(this, BluetoothService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Set the initial theme to light mode
        setInitialTheme();

        format.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (myApp.getDayValueforMain()) {
                    case 1:
                        myApp.setMon(myApp.getTodayAmount());
                        myApp.setTue(0);
                        myApp.setWed(0);
                        myApp.setThu(0);
                        myApp.setFri(0);
                        myApp.setSat(0);
                        myApp.setSun(0);
                        break;
                    case 2:
                        myApp.setTue(myApp.getTodayAmount());
                        break;
                    case 3:
                        myApp.setWed(myApp.getTodayAmount());
                        break;
                    case 4:
                        myApp.setThu(myApp.getTodayAmount());
                        break;
                    case 5:
                        myApp.setFri(myApp.getTodayAmount());
                        break;
                    case 6:
                        myApp.setSat(myApp.getTodayAmount());
                        break;
                    case 7:
                        myApp.setSun(myApp.getTodayAmount());
                        break;
                    default:
                        break;
                }
                myApp.setTodayAmount(0);
                myApp.addDayValueForMain();

                TextView waterAmountText = findViewById(R.id.waterAmountText);
                String todayAmountText = String.valueOf(myApp.getTodayAmount()) + "mL";
                waterAmountText.setText(todayAmountText);


                int percentage = (int) ((float) myApp.getTodayAmount() / myApp.getGoalAmount() * 100);
                TextView amountPercent = findViewById(R.id.amountPercent);
                String percent = String.valueOf(percentage) + "%";
                amountPercent.setText(percent);

                //영점 조절
                if (isBound) {
                    bluetoothService.sendData("A");
                }
            }

        });

        // Set click listeners
        button.setOnClickListener(v -> { // 물 버림
            if (!isBound) {
                Toast.makeText(getApplicationContext(), "블루투스 연결부터 해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (true) {
                bluetoothService.sendData("B");
            }

            myApp.getTodayAmount();
            if (bluetoothService != null) {
                bluetoothService.readData();
                Message message = handlerdrain.obtainMessage();
                handlerdrain.sendMessage(message);
            }

            TextView waterAmountText = findViewById(R.id.waterAmountText);
            String todayAmountText = String.valueOf(myApp.getTodayAmount()) + "mL";
            waterAmountText.setText(todayAmountText);

            setMainTextVeiw(myApp.getTodayAmount(), myApp.getGoalAmount(), myApp.getBeforeAmount());

            Toast.makeText(getApplicationContext(), "물을 버렸어요!", Toast.LENGTH_SHORT).show();


            // Send data 'A' to Arduino

        });

        button2.setOnClickListener(view -> { // 물 채움
            if (!isBound) {
                Toast.makeText(getApplicationContext(), "블루투스 연결부터 해주세요.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (true) {
                bluetoothService.sendData("B");
            }

            myApp.getTodayAmount();
            if (bluetoothService != null) {
                bluetoothService.readData();
                Message message = handlerdrink.obtainMessage();
                handlerdrink.sendMessage(message);
            }

            TextView waterAmountText = findViewById(R.id.waterAmountText);
            String todayAmountText = String.valueOf(myApp.getTodayAmount()) + "mL";
            waterAmountText.setText(todayAmountText);

            int percentage = (int) ((float) myApp.getTodayAmount() / myApp.getGoalAmount() * 100);
            TextView amountPercent = findViewById(R.id.amountPercent);
            String percent = String.valueOf(percentage) + "%";
            amountPercent.setText(percent);

            String now = "현재 텀블러 측정값 : " + String.valueOf(myApp.getBeforeAmount());


            Toast.makeText(getApplicationContext(), "물을 추가했어요!", Toast.LENGTH_SHORT).show();

        });

        switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchToDarkTheme();
            } else {
                switchToLightTheme();
            }
        });

        switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchToDarkTheme();
            } else {
                switchToLightTheme();
            }
        });

        buttonSetting.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        });

        statisticsIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Stat.class);
            startActivity(intent);
        });

        bluetoothIcon.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BluetoothConnect.class);
            startActivity(intent);
        });

        reloadIcon.setOnClickListener(v -> {
            // bluetoothService.sendData("B");

            Toast.makeText(getApplicationContext(), "새로고침 성공!", Toast.LENGTH_SHORT).show();

            if (bluetoothService != null) {
                bluetoothService.readData();
                Message message = handler.obtainMessage();
                handler.sendMessage(message);
            }
        });

        informationButton.setOnClickListener(v -> showInformationPopup());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void resetAlarm(MainActivity mainActivity) {
    }

    private void setMainTextVeiw(int todayAmount, int goalAmount, int beforeAmount) {
        // activity_main.xml의 TextView에 연결해서 퍼센트 계산
        int percentage = (int) ((float) todayAmount / goalAmount * 100);
        TextView amountPercent = findViewById(R.id.amountPercent);
        String percent = String.valueOf(percentage) + "%";
        amountPercent.setText(percent);
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.BLUETOOTH_SCAN,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_PERMISSIONS);
            }
        } else {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, REQUEST_PERMISSIONS);
            }
        }
    }

    private void setInitialTheme() {
        lightThemeLayout.setVisibility(View.VISIBLE);
        darkThemeLayout.setVisibility(View.GONE);
        switch1.setChecked(false);
        switch2.setChecked(false);
        switch2.setChecked(false);
        updateIconsForLightTheme();
    }

    private void switchToDarkTheme() {
        lightThemeLayout.setVisibility(View.GONE);
        darkThemeLayout.setVisibility(View.VISIBLE);
        switch1.setChecked(true);
        switch2.setChecked(true);
        updateIconsForDarkTheme();
    }

    private void switchToLightTheme() {
        lightThemeLayout.setVisibility(View.VISIBLE);
        darkThemeLayout.setVisibility(View.GONE);
        switch1.setChecked(false);
        switch2.setChecked(false);
        updateIconsForLightTheme();
    }

    private void updateIconsForLightTheme() {
        buttonSetting.setImageResource(R.drawable.settings);
        statisticsIcon.setImageResource(R.drawable.chart);
        bluetoothIcon.setImageResource(R.drawable.bluetooth);
        reloadIcon.setImageResource(R.drawable.reload);

        nowAmount.setTextColor(Color.parseColor("#194569"));
        waterAmountText.setTextColor(Color.parseColor("#194569"));
        farText.setTextColor(Color.parseColor("#5f84a2"));
        soFarText.setTextColor(Color.parseColor("#5f84a2"));

        settingTextView.setTextColor(Color.parseColor("#194569"));
        bluetoothTextView.setTextColor(Color.parseColor("#194569"));
        statsTextView.setTextColor(Color.parseColor("#194569"));
        refreshTextView.setTextColor(Color.parseColor("#194569"));
    }

    private void updateIconsForDarkTheme() {
        buttonSetting.setImageResource(R.drawable.settings_w);
        statisticsIcon.setImageResource(R.drawable.chart_w);
        bluetoothIcon.setImageResource(R.drawable.bluetooth_w);
        reloadIcon.setImageResource(R.drawable.reload_w);

        nowAmount.setTextColor(Color.parseColor("#cadeed"));
        waterAmountText.setTextColor(Color.parseColor("#cadeed"));
        farText.setTextColor(Color.parseColor("#91aec4"));
        soFarText.setTextColor(Color.parseColor("#91aec4"));

        settingTextView.setTextColor(Color.parseColor("#cadeed"));
        bluetoothTextView.setTextColor(Color.parseColor("#cadeed"));
        statsTextView.setTextColor(Color.parseColor("#cadeed"));
        refreshTextView.setTextColor(Color.parseColor("#cadeed"));
    }

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

    public static void resetAlarm(Context context) {
        AlarmManager resetAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent resetIntent = new Intent(context, Initialize.class);
        PendingIntent resetSender = PendingIntent.getBroadcast(context, 0, resetIntent, PendingIntent.FLAG_IMMUTABLE);

        // 자정 시간
        Calendar resetCal = Calendar.getInstance();
        resetCal.setTimeInMillis(System.currentTimeMillis());
        resetCal.set(Calendar.HOUR_OF_DAY, 0);
        resetCal.set(Calendar.MINUTE, 0);
        resetCal.set(Calendar.SECOND, 0);

        //다음날 0시에 맞추기 위해 24시간을 뜻하는 상수인 AlarmManager.INTERVAL_DAY를 더해줌.
        resetAlarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, resetCal.getTimeInMillis()
                + AlarmManager.INTERVAL_DAY, AlarmManager.INTERVAL_DAY, resetSender);


        SimpleDateFormat format1 = new SimpleDateFormat("MM/dd kk:mm:ss");
        String setResetTime = format1.format(new Date(resetCal.getTimeInMillis() + AlarmManager.INTERVAL_DAY));

        Log.d("resetAlarm", "ResetHour : " + setResetTime);
    }
}