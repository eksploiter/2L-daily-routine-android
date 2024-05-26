package com.example.forcapstone2;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class Stat extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 액티비티가 화면에 표시되기 전에 상태 표시줄을 숨깁니다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // stat.xml 레이아웃 로드
        setContentView(R.layout.stat);

        // Setting up the back button to navigate back to MainActivity
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Stat.this, MainActivity.class);
                startActivity(intent);
                finish(); // Finish the current activity to remove it from the back stack
            } // 이 부분이 추가되었습니다.

        }); // 이 부분이 추가되었습니다.

        ImageButton statButton = (ImageButton) findViewById(R.id.statButton);

        statButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업창을 띄우는 코드
                Dialog dialog = new Dialog(Stat.this, R.style.RoundedDialog);
                dialog.setContentView(R.layout.popup_info);
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
        });


        // 인텐트로부터 물 섭취 데이터를 받아옵니다.
        // 실제 데이터 대신 모의 데이터를 사용합니다.
        int[] waterIntakeData = {2000, 1500, 1800, 2200, 2100, 1900, 1600}; // 예시 데이터

        // 요일 이름 배열
        String[] daysOfWeek = {"월", "화", "수", "목", "금", "토", "일"};

        // ListView에 표시할 데이터를 준비합니다.
        ArrayList<String> displayList = new ArrayList<>();
        for (int i = 0; i < waterIntakeData.length; i++) {
            String displayText = daysOfWeek[i] + " : " + waterIntakeData[i] + "ml";
            displayList.add(displayText);
        }

        int totalIntake = 0;
        for (int dailyIntake : waterIntakeData) {
            totalIntake += dailyIntake;
        }

        // ProgressBar 설정
        ProgressBar progressBar = findViewById(R.id.progressBarWaterIntake);
        int weeklyGoal = 14000; // 일주일 목표량 (2000ml x 7일)
        progressBar.setMax(weeklyGoal); // ProgressBar의 최대치를 일주일 목표량으로 설정
        progressBar.setProgress(totalIntake); // 현재 진행 사항을 계산된 총 섭취량으로 설정

        // ArrayAdapter를 사용하여 데이터를 ListView에 바인딩합니다.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.list_item_water_intake, displayList);

        ListView listView = findViewById(R.id.listViewWaterIntake);
        listView.setAdapter(adapter);

        TextView tvTotalIntake = findViewById(R.id.tvTotalIntake);
        tvTotalIntake.setText("  주간 총 섭취량 : " + totalIntake + "ml / 14000ml"); // 실시간으로 변하는 총 섭취량 표시
    }
}