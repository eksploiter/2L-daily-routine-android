// ThemeManager.java
package com.example.forcapstone2;

import android.app.Activity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

public class ThemeManager {
    private Activity activity;
    private FrameLayout lightThemeLayout, darkThemeLayout;
    private Switch switch1, switch2;

    public ThemeManager(Activity activity) {
        this.activity = activity;
    }

    public void initializeThemes() {
        lightThemeLayout = activity.findViewById(R.id.lightThemeLayout);
        darkThemeLayout = activity.findViewById(R.id.darkThemeLayout);
        switch1 = activity.findViewById(R.id.switch1);
        switch2 = activity.findViewById(R.id.switch2);

        // 초기 테마 설정
        setInitialTheme();
    }

    private void setInitialTheme() {
        lightThemeLayout.setVisibility(View.VISIBLE);
        darkThemeLayout.setVisibility(View.GONE);
        switch1.setChecked(false);
        switch2.setChecked(false);
    }

    public void switchToDarkTheme() {
        lightThemeLayout.setVisibility(View.GONE);
        darkThemeLayout.setVisibility(View.VISIBLE);
        switch1.setChecked(true);
        switch2.setChecked(true);
    }

    public void switchToLightTheme() {
        lightThemeLayout.setVisibility(View.VISIBLE);
        darkThemeLayout.setVisibility(View.GONE);
        switch1.setChecked(false);
        switch2.setChecked(false);
    }
}
