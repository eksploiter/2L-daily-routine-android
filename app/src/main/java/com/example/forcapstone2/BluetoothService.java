package com.example.forcapstone2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final String TAG = "BluetoothService";
    public static final String ACTION_GATT_CONNECTED = "com.example.forcapstone2.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.forcapstone2.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_DATA_AVAILABLE = "com.example.forcapstone2.ACTION_DATA_AVAILABLE";

    public static String receivedMessage = "";
    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private Handler handler = new Handler();
    private boolean isScanning = false;
    private static final long SCAN_PERIOD = 10000;
    private static final String TARGET_MAC_ADDRESS = "1E:41:CF:5C:6F:4E"; // Your Arduino Nano BLE MAC address
    private static final UUID MY_SERVICE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final UUID MY_CHARACTERISTIC_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothGattCharacteristic characteristic;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
        scanLeDevice(true);
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(() -> {
                isScanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);
                Log.d(TAG, "Stopped scanning");
            }, SCAN_PERIOD);

            isScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d(TAG, "Started scanning");
        } else {
            isScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d(TAG, "Stopped scanning");
        }
    }

    // BLE 스캔 콜백 설정
    private final ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice device = result.getDevice();

            // MAC 주소 확인 후 연결 시도
            if (TARGET_MAC_ADDRESS.equals(device.getAddress())) {
                connectToDevice(device);
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        Log.d(TAG, "Connecting to device: " + device.getAddress());
        if (ContextCompat.checkSelfPermission(BluetoothService.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt = device.connectGatt(BluetoothService.this, false, gattCallback);
        } else {
            showToast("Bluetooth Connect permission is required to connect to device");
        }
    }

    public void sendData(String data) {
        if (bluetoothGatt != null && characteristic != null) {
            characteristic.setValue(data.getBytes());
            bluetoothGatt.writeCharacteristic(characteristic);
        } else {
            showToast("Device not connected or characteristic not found");
        }
    }

    public void readData(){
        Log.d(TAG, "readData()");
        if (bluetoothGatt != null && characteristic != null) {
            Log.d(TAG, "readData() if");
            bluetoothGatt.readCharacteristic(characteristic);
        } else {
            showToast("Device not connected or characteristic not found");
        }
    }
    // GATT 콜백 설정
    public final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // 연결 성공 시 서비스 검색
                Log.d(TAG, "onConnectionStateChange");
                gatt.discoverServices();
                broadcastUpdate(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // 원하는 서비스 및 특성(UUID) 찾기
                BluetoothGattService service = gatt.getService(MY_SERVICE_UUID);
                if (service != null) {
                    Log.d(TAG, "Service found: " + MY_SERVICE_UUID);
                    characteristic = service.getCharacteristic(MY_CHARACTERISTIC_UUID);
                    if (characteristic != null) {
                        Log.d(TAG, "Characteristic found: " + MY_CHARACTERISTIC_UUID);
                        gatt.readCharacteristic(characteristic);
                        broadcastUpdate(ACTION_DATA_AVAILABLE);
                    } else {
                        Log.e(TAG, "Characteristic not found: " + MY_CHARACTERISTIC_UUID);
                        showToast("Characteristic not found");
                    }
                } else {
                    Log.e(TAG, "Service not found: " + MY_SERVICE_UUID);
                    showToast("Service not found");
                }
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value, int status) {
            super.onCharacteristicRead(gatt, characteristic, value, status);
//            Log.d(TAG, "Status: "+ status);
//            Log.d(TAG, "value.length: "+ value.length);
//            Log.d(TAG, "value: "+ Arrays.toString(value));
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d(TAG, "onCharacteristicRead");
                String receivedMessage = new String(value, StandardCharsets.UTF_8);
                Log.d(TAG, "data: " + receivedMessage);
                BluetoothService.receivedMessage = receivedMessage;
            }
        }

        @Override
        public void onCharacteristicChanged(@NonNull BluetoothGatt gatt, @NonNull BluetoothGattCharacteristic characteristic, @NonNull byte[] value) {
            byte[] bytes = characteristic.getValue();
            String received = new String(bytes, StandardCharsets.UTF_8);
        }
    };

    @SuppressLint("MissingPermission")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bluetoothGatt != null) {
            bluetoothGatt.close();
            bluetoothGatt = null;
        }
    }

    private void showToast(final String message) {
        handler.post(() -> Toast.makeText(BluetoothService.this, message, Toast.LENGTH_SHORT).show());
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
