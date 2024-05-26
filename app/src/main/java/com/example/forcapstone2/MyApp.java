package com.example.forcapstone2;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyApp extends Application {
    private int currentAmount; // 지금 마신량; 아두이노에서 현재 마신량 받아올 때 쓰는 변수
    private int todayAmount; // 오늘 마신량
    private int weekAmount; // 이번 주 마신량
    private int mon, tue, wed, thu, fri, sat, sun; // 요일별 마신량; 주간 통계에 필요한 변수
    private int goalAmount; // 목표치

    @Override
    public void onCreate() {
        super.onCreate();
        // 초기화 코드
        currentAmount = 0;
        todayAmount = getBeforeAmount();
        goalAmount = getGoalAmount();
    }

    public int getCurrentAmount(int x){
        // 아두이노에서 받아오는 값 return
        return x;
    }

    public int getTodayAmount() { // 저장된 sharedPreferences를 받아오는 메소드
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sharedPreferences.getInt("savedTodayAmount", 0); // 이전 무게값
    }

    public void setTodayAmount(int currentWeight) { // sharedPreferences를 이용해 현재 물 무게 저장하는 메소드
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedTodayAmount", currentWeight); // 현재 물 무게 sharedPreferences에 저장
        editor.apply();
    }
    public int getBeforeAmount() { // 저장된 sharedPreferences를 받아오는 메소드
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sharedPreferences.getInt("savedBeforeAmount", 0); // 이전 무게값
    }

    public void setBeforeAmount(int currentWeight) { // sharedPreferences를 이용해 현재 물 무게 저장하는 메소드
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedBeforeAmount", currentWeight); // 현재 물 무게 sharedPreferences에 저장
        editor.apply();
    }

    public int reloadAmount() { // 새로고침 버튼 - 물을 그냥 마신 경우
        int beforeWeight = getBeforeAmount(); // 저장된 값 불러오기
        setBeforeAmount(getCurrentAmount(100)); // 현재 무게 sharedPreferences에 저장

        if (beforeWeight < getCurrentAmount(100)) { // 물 양이 늘어나면 마신 것이 아니니 그냥 오늘 물 값 반환
            return getTodayAmount();
        } else { // 그게 아니라면 줄어든만큼 마신량으로 계산
            setTodayAmount(getTodayAmount() + (beforeWeight - getCurrentAmount(50)));
            return getTodayAmount();
        }

    }

    public int drainAmount() { // 물버림 버튼 - 물을 버리고 새로 따른 경우
        setBeforeAmount(getCurrentAmount(180)); // 현재 무게 sharedPreferences에 저장

        return getTodayAmount();
    }

    public int drinkAmount() { // 물 채움 버튼 - 물을 다 마시고 새로 물을 따른 경우
        int beforeWeight = getBeforeAmount(); // 저장된 값 불러오기
        setBeforeAmount(getCurrentAmount(20)); // 현재 무게 sharedPreferences에 저장

        setTodayAmount(getTodayAmount() + beforeWeight); // 기존 무게를 먹은 걸로 계산
        return getTodayAmount();
    }

    public void initializeToday(int day){
        switch (day){
            case 1: mon = todayAmount;
                break;
            case 2: tue = todayAmount;
                break;
            case 3: wed = todayAmount;
                break;
            case 4: thu = todayAmount;
                break;
            case 5: fri = todayAmount;
                break;
            case 6: sat = todayAmount;
                break;
            case 7: sun = todayAmount;
                break;
            default:
                break;
        }
    }

    public int getGoalAmount() {
        // 값 불러오기
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        goalAmount = sharedPreferences.getInt("savedGoalAmount", 0);

        return goalAmount;
    }

    public void setCurrentAmount() {
    }

    public void setGoalAmount(int goalAmount) { // 사용자가 알림 설정 화면에서 설정한 값을 받아오는 메소드

        // 값 저장하기 - SharedPreferences의 key인 'savedGoalAmount'에 설정 액티비티에서 스피너로 사용자가 입력한 값 저장.
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedGoalAmount", goalAmount);
        editor.apply();
    }

    public void setMon(int todayAmount){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("monAmount", todayAmount);
        editor.apply();
    }
    public void setTue(int todayAmount){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tueAmount", todayAmount);
        editor.apply();
    }
}