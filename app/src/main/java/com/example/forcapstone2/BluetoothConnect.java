package com.example.forcapstone2;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothConnect extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private ListView listView;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private ArrayList<BluetoothDevice> pairedDevicesList;
    private BluetoothSocket bluetoothSocket;

    private static final int REQUEST_BLUETOOTH_CONNECT = 1;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // UUID for SPP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_connection);

        hideSystemUI();

        listView = findViewById(R.id.bluetooth_devices_list);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Set up the back button
        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BluetoothConnect.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Initialize BLE button inside onCreate
        ImageButton bleButton = findViewById(R.id.bleButton);
        bleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 팝업창을 띄우는 코드
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
            }
        });

        if (bluetoothAdapter == null) {
            Toast.makeText(this, "블루투스를 지원하지 않는 기기입니다.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            checkBluetoothPermissionAndDisplayDevices();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = pairedDevicesList.get(position);
                connectToDevice(device);
            }
        });
    }

    /**
     * Hides the system UI for a full-screen experience.
     */
    private void hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    /**
     * Checks for Bluetooth permissions and displays paired devices if granted.
     */
    private void checkBluetoothPermissionAndDisplayDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_BLUETOOTH_CONNECT);
            } else {
                displayPairedDevices();
            }
        } else {
            displayPairedDevices();
        }
    }

    /**
     * Displays the list of paired Bluetooth devices.
     */
    private void displayPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        ArrayList<String> devices = new ArrayList<>();
        pairedDevicesList = new ArrayList<>();
        for (BluetoothDevice device : pairedDevices) {
            devices.add(device.getName());
            pairedDevicesList.add(device);
        }
        pairedDevicesAdapter = new ArrayAdapter<>(this, R.layout.list_item_bluetooth_device, devices);
        listView.setAdapter(pairedDevicesAdapter);
    }

    /**
     * Handles the result of permission requests.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                displayPairedDevices();
            } else {
                Toast.makeText(this, "블루투스 연결 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Attempts to connect to the selected Bluetooth device.
     *
     * @param device The Bluetooth device to connect to.
     */
    private void connectToDevice(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();
            updateConnectionStatus("연결 상태: " + device.getName() + "과(와) 연결됨");
        } catch (IOException e) {
            Toast.makeText(this, "연결을 시도하는 동안 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            updateConnectionStatus("연결 상태: 연결 실패");
        }
    }

    /**
     * Updates the connection status TextView.
     *
     * @param status The connection status message to display.
     */
    private void updateConnectionStatus(String status) {
        TextView connectionStatusTextView = findViewById(R.id.bluetooth_status);
        connectionStatusTextView.setText(status);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the BluetoothSocket when the activity is destroyed to release resources.
        if (bluetoothSocket != null) {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}