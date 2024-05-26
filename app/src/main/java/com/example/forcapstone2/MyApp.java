package com.example.forcapstone2;

import android.app.Application;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyApp extends Application {
    private int currentAmount; // 현재 마신 물의 양
    private int todayAmount; // 오늘 마신 물의 양
    private int weekAmount; // 이번 주 총 마신 물의 양
    private int mon, tue, wed, thu, fri, sat, sun; // 요일별 마신 물의 양
    private int goalAmount; // 목표 마실 물의 양

    @Override
    public void onCreate() {
        super.onCreate();
        // 초기화 코드
        currentAmount = 0;
        todayAmount = getBeforeAmount();
        goalAmount = getGoalAmount();
    }

    // 아두이노에서 현재 마신 물의 양을 받아오는 메소드
    public int getCurrentAmount(int x){
        return x;
    }

    // SharedPreferences에서 오늘 마신 물의 양을 가져오는 메소드
    public int getTodayAmount() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sharedPreferences.getInt("savedTodayAmount", 0);
    }

    // 현재 마신 물의 양을 SharedPreferences에 저장하는 메소드
    public void setTodayAmount(int currentWeight) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedTodayAmount", currentWeight);
        editor.apply();
    }

    // 이전에 저장된 물의 양을 가져오는 메소드
    public int getBeforeAmount() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        return sharedPreferences.getInt("savedBeforeAmount", 0);
    }

    // 현재 물의 양을 SharedPreferences에 저장하는 메소드
    public void setBeforeAmount(int currentWeight) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedBeforeAmount", currentWeight);
        editor.apply();
    }

    // 새로고침 버튼을 눌렀을 때 호출되는 메소드
    public int reloadAmount() {
        int beforeWeight = getBeforeAmount();
        setBeforeAmount(getCurrentAmount(100));

        if (beforeWeight < getCurrentAmount(100)) {
            return getTodayAmount();
        } else {
            setTodayAmount(getTodayAmount() + (beforeWeight - getCurrentAmount(50)));
            return getTodayAmount();
        }
    }

    // 물을 버렸을 때 호출되는 메소드
    public int drainAmount() {
        setBeforeAmount(getCurrentAmount(180));
        return getTodayAmount();
    }

    // 물을 마셨을 때 호출되는 메소드
    public int drinkAmount() {
        int beforeWeight = getBeforeAmount();
        setBeforeAmount(getCurrentAmount(20));

        setTodayAmount(getTodayAmount() + beforeWeight);
        return getTodayAmount();
    }

    // 오늘의 마신 물의 양을 요일별 변수에 초기화하는 메소드
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

    // SharedPreferences에서 목표 마실 물의 양을 가져오는 메소드
    public int getGoalAmount() {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        goalAmount = sharedPreferences.getInt("savedGoalAmount", 0);
        return goalAmount;
    }

    // 목표 마실 물의 양을 SharedPreferences에 저장하는 메소드
    public void setGoalAmount(int goalAmount) {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("savedGoalAmount", goalAmount);
        editor.apply();
    }

    // 월요일에 마신 물의 양을 SharedPreferences에 저장하는 메소드
    public void setMon(int todayAmount){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("monAmount", todayAmount);
        editor.apply();
    }

    // 화요일에 마신 물의 양을 SharedPreferences에 저장하는 메소드
    public void setTue(int todayAmount){
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("tueAmount", todayAmount);
        editor.apply();
    }
}
