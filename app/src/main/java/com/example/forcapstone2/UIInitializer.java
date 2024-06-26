// UIInitializer.java
package com.example.forcapstone2;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

public class UIInitializer {
    private Activity activity;
    private Switch switch1, switch2;
    private ImageView buttonSetting, statisticsIcon, bluetoothIcon, reloadIcon;

    public UIInitializer(Activity activity) {
        this.activity = activity;
    }

    public void initializeViews() {
        switch1 = activity.findViewById(R.id.switch1);
        switch2 = activity.findViewById(R.id.switch2);
        buttonSetting = activity.findViewById(R.id.buttonSetting);
        statisticsIcon = activity.findViewById(R.id.statisticsIcon);
        bluetoothIcon = activity.findViewById(R.id.bluetoothIcon);
        reloadIcon = activity.findViewById(R.id.reloadIcon);

        // Set click listeners
        setClickListeners();
    }

    private void setClickListeners() {
        buttonSetting.setOnClickListener(v -> {
            // 페이지 이동
        });

        statisticsIcon.setOnClickListener(v -> {
            // 페이지 이동
        });

        bluetoothIcon.setOnClickListener(v -> {
            // 페이지 이동
        });

        reloadIcon.setOnClickListener(v -> {
            // 데이터 새로 고침
        });
    }
}
