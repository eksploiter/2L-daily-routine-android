package com.example.forcapstone2;

import android.app.Application;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MyApp extends Application {
    private int currentAmount;
    private int todayAmount;
    private int goalAmount;

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
}
