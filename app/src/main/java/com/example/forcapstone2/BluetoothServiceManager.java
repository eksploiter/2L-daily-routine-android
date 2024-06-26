// BluetoothServiceManager.java
package com.example.forcapstone2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class BluetoothServiceManager {
    private BluetoothService bluetoothService;
    private boolean isBound = false;
    private Context context;

    public BluetoothServiceManager(Context context) {
        this.context = context;
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            Log.d("BluetoothService", "ServiceConnection-onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public void bindService() {
        Intent serviceIntent = new Intent(context, BluetoothService.class);
        context.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection);
            isBound = false;
        }
    }

    public boolean isServiceBound() {
        return isBound;
    }

    public boolean isBluetoothServiceAvailable() {
        return bluetoothService != null;
    }

    public BluetoothService getBluetoothService() {
        return bluetoothService;
    }
}
