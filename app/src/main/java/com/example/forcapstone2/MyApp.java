package com.example.forcapstone2;

import android.app.Application;
import android.content.SharedPreferences;

public class MyApp extends Application {
    private int currentAmount; // 마신량
    private int goalAmount; // 목표치

    @Override
    public void onCreate() {
        super.onCreate();
        // 초기화 코드
        currentAmount = 0;
        goalAmount = 0;
    }

    public int getCurrentAmount() {
        // 앱 내에서 사용하는 변수(currentAmount)에 sharedPreferencse에 있는 값(아두이노에서 받아온 값) 전달
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        currentAmount = sharedPreferences.getInt("savedCurrentAmount", 0);

        return currentAmount;
    }

    // 추가할 사항
    // 현재 값 > 이전 값 => 물을 다 마시고 새로 따른 것으로 알고리즘 구상
    // 현재 값 < 이전 값 => 데이터 차액을 계산해서 그만큼을 마신 양으로 측정
    public void setCurrentAmount() { // 여기에 아두이노에서 값을 받아오는 코드를 작성하던지, 매개변수로 받기
        // 값 저장하기 - SharedPreferences의 key인 'savedCurrentAmount'에 아두이노에서 받아온 값 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedCurrentAmount", 600); // *상수로 관리?*
        editor.apply();
    }

    public int getGoalAmount() {
        // 값 불러오기
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        goalAmount = sharedPreferences.getInt("savedGoalAmount", 0);

        return goalAmount;
    }

    public void setGoalAmount(int goalAmount) { // 사용자가 알림 설정 화면에서 설정한 값을 받아오는 메소드

        // 값 저장하기 - SharedPreferences의 key인 'savedGoalAmount'에 설정 액티비티에서 스피너로 사용자가 입력한 값 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedGoalAmount", goalAmount);
        editor.apply();
    }
}