package com.example.forcapstone2;

import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
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

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.UUID;

public class BluetoothService extends Service {

    public static final String ACTION_GATT_CONNECTED = "com.example.forcapstone2.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED = "com.example.forcapstone2.ACTION_GATT_DISCONNECTED";
    public static final String ACTION_DATA_AVAILABLE = "com.example.forcapstone2.ACTION_DATA_AVAILABLE";
    public static final String EXTRA_DATA = "com.example.forcapstone2.EXTRA_DATA";

    private final IBinder binder = new LocalBinder();
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private BluetoothGatt bluetoothGatt;
    private Handler handler = new Handler();
    private boolean isScanning = false;
    private static final long SCAN_PERIOD = 10000;
    private static final String TARGET_MAC_ADDRESS = "76:05:96:D0:E9:3E"; // Your Arduino Nano BLE MAC address 76:05:96:D0:E9:3E, 1E:41:CF:5C:6F:4E

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
                Log.d("BluetoothService", "Stopped scanning");
            }, SCAN_PERIOD);

            isScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
            Log.d("BluetoothService", "Started scanning");
        } else {
            isScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
            Log.d("BluetoothService", "Stopped scanning");
        }
    }

    private final ScanCallback leScanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            Log.d("BluetoothService", "Found device: " + device.getAddress());
            if (TARGET_MAC_ADDRESS.equals(device.getAddress())) {
                Log.d("BluetoothService", "Target device found");
                connectToDevice(device);
                scanLeDevice(false);
            }
        }
    };

    @SuppressLint("MissingPermission")
    private void connectToDevice(BluetoothDevice device) {
        Log.d("BluetoothService", "Connecting to device: " + device.getAddress());
        if (ContextCompat.checkSelfPermission(BluetoothService.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            bluetoothGatt = device.connectGatt(BluetoothService.this, false, gattCallback);
        } else {
            showToast("Bluetooth Connect permission is required to connect to device");
        }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                bluetoothGatt.discoverServices();
                showToast("Connected to " + gatt.getDevice().getName());
                broadcastUpdate(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                showToast("Disconnected");
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                for (BluetoothGattService service : gatt.getServices()) {
                    for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                        if (characteristic.getUuid().equals(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))) {
                            if (ActivityCompat.checkSelfPermission(BluetoothService.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            gatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                // Handle characteristic read
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            if (characteristic.getUuid().equals(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"))) {
                final String weight = characteristic.getStringValue(0);
                broadcastUpdate(ACTION_DATA_AVAILABLE, weight);
            }
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

    private void broadcastUpdate(final String action, final String data) {
        final Intent intent = new Intent(action);
        intent.putExtra(EXTRA_DATA, data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
