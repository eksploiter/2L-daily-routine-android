package com.example.forcapstone2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BluetoothConnect extends AppCompatActivity {

    private BluetoothService bluetoothService;
    private boolean isBound = false;

    private TextView connectionStatus;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @SuppressLint({"MissingPermission", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connection);

        connectionStatus = findViewById(R.id.connection_status);

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(BluetoothConnect.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        ImageButton bleButton = findViewById(R.id.bleButton);
        bleButton.setOnClickListener(v -> {
            Dialog dialog = new Dialog(BluetoothConnect.this, R.style.RoundedDialog);
            dialog.setContentView(R.layout.popup_ble);
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
        });

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }
}
