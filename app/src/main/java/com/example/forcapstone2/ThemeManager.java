package com.example.forcapstone2;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class ThemeManager {
    private Activity activity;
    private FrameLayout lightThemeLayout, darkThemeLayout;
    private Switch switch1, switch2;
    private ImageView buttonSetting, statisticsIcon, bluetoothIcon, reloadIcon;
    private TextView nowAmount, waterAmountText, soFarText, farText, settingTextView, bluetoothTextView, statsTextView, refreshTextView;

    public ThemeManager(Activity activity, FrameLayout lightThemeLayout, FrameLayout darkThemeLayout, Switch switch1, Switch switch2, ImageView buttonSetting, ImageView statisticsIcon, ImageView bluetoothIcon, ImageView reloadIcon, TextView nowAmount, TextView waterAmountText) {
        this.activity = activity;
        this.lightThemeLayout = lightThemeLayout;
        this.darkThemeLayout = darkThemeLayout;
        this.switch1 = switch1;
        this.switch2 = switch2;
        this.buttonSetting = buttonSetting;
        this.statisticsIcon = statisticsIcon;
        this.bluetoothIcon = bluetoothIcon;
        this.reloadIcon = reloadIcon;
        this.nowAmount = nowAmount;
        this.waterAmountText = waterAmountText;
        this.soFarText = soFarText;
        this.farText = farText;
        this.settingTextView = settingTextView;
        this.bluetoothTextView = bluetoothTextView;
        this.statsTextView = statsTextView;
        this.refreshTextView = refreshTextView;

        setInitialTheme(); // Set the initial theme during initialization
    }

    private void setInitialTheme() {
        lightThemeLayout.setVisibility(View.VISIBLE);
        darkThemeLayout.setVisibility(View.GONE);
        switch1.setChecked(false);
        switch2.setChecked(false);
        updateIconsForLightTheme();
    }

    public void switchToDarkTheme() {
        lightThemeLayout.setVisibility(View.GONE);
        darkThemeLayout.setVisibility(View.VISIBLE);
        switch1.setChecked(true);
        switch2.setChecked(true);
        updateIconsForDarkTheme();
    }

    public void switchToLightTheme() {
        lightThemeLayout.setVisibility(View.VISIBLE);
        darkThemeLayout.setVisibility(View.GONE);
        switch1.setChecked(false);
        switch2.setChecked(false);
        updateIconsForLightTheme();
    }

    private void updateIconsForLightTheme() { // 라이트 모드
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

    private void updateIconsForDarkTheme() { // 다크 모드
        buttonSetting.setImageResource(R.drawable.settings_w);
        statisticsIcon.setImageResource(R.drawable.chart_w);
        bluetoothIcon.setImageResource(R.drawable.bluetooth_w);
        reloadIcon.setImageResource(R.drawable.reload_w);

        nowAmount.setTextColor(Color.parseColor("#cadeed")); // 아이콘 밑에 글자 색상
        waterAmountText.setTextColor(Color.parseColor("#cadeed"));
        farText.setTextColor(Color.parseColor("#91aec4"));
        soFarText.setTextColor(Color.parseColor("#91aec4"));

        settingTextView.setTextColor(Color.parseColor("#cadeed")); //물 양 표기 색상
        bluetoothTextView.setTextColor(Color.parseColor("#cadeed"));
        statsTextView.setTextColor(Color.parseColor("#cadeed"));
        refreshTextView.setTextColor(Color.parseColor("#cadeed"));
    }
}
