package com.orion.pos_crushty_android.utility;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.orion.pos_crushty_android.JApplication;
import com.orion.pos_crushty_android.globals.JConst;
import com.orion.pos_crushty_android.ui.sync_printer.DeviceListActivity;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class PrinterUtils {

    private static final String TAG = "PrinterUtil";

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothSocket mBluetoothSocket = null;
    private BluetoothDevice mBluetoothDevice = null;
    private ProgressDialog mBluetoothConnectProgressDialog;
    private Activity activity;

    // UUID for the connection
    private static final UUID applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public PrinterUtils(Activity activity) {
        this.activity = activity;
    }

    public void connectDisconnect() {
        if (!JApplication.isPrinterConnected) {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                Toast.makeText(activity, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            } else {
                // Periksa izin Bluetooth terlebih dahulu
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!hasBluetoothPermissions()) {
                        requestBluetoothPermissions();
                        return; // Keluar untuk menunggu hasil izin
                    }
                }

                // Setelah izin dipastikan ada, lanjutkan ke proses berikutnya
                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    activity.startActivityForResult(enableBtIntent, JConst.REQUEST_ENABLE_BT);
                } else {
                    listPairedDevices();
                    Intent connectIntent = new Intent(activity, DeviceListActivity.class);
                    activity.startActivityForResult(connectIntent, JConst.REQUEST_CONNECT_DEVICE);
                }
            }
        } else {
            disconnectBluetooth();
        }
    }

    private boolean hasBluetoothPermissions() {
        return ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                // Selalu minta izin terlebih dahulu
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                        JConst.REQUEST_PERMISSION_ENABLE_BT);
            }
        }
    }


    private void disconnectBluetooth() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.disable();
        }
        JApplication.isPrinterConnected = false;
        Toast.makeText(activity, "Disconnected", Toast.LENGTH_SHORT).show();
    }

    private void listPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                Log.v(TAG, "PairedDevices: " + device.getName() + "  " + device.getAddress());
            }
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case JConst.REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    Bundle extras = data.getExtras();
                    String deviceAddress = extras.getString("DeviceAddress");
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    mBluetoothConnectProgressDialog = ProgressDialog.show(activity,
                            "Connecting...", mBluetoothDevice.getName() + " : " + mBluetoothDevice.getAddress(), true, false);
                    new Thread(this::connectBluetooth).start();
                }
                break;

            case JConst.REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    listPairedDevices();
                    Intent connectIntent = new Intent(activity, DeviceListActivity.class);
                    activity.startActivityForResult(connectIntent, JConst.REQUEST_CONNECT_DEVICE);
                } else {
                    Toast.makeText(activity, "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == JConst.REQUEST_PERMISSION_ENABLE_BT) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Izin diberikan
                Toast.makeText(activity, "Izin Bluetooth diberikan.", Toast.LENGTH_SHORT).show();
            } else {
                // Izin ditolak
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.BLUETOOTH_CONNECT)) {
                    // Tampilkan dialog untuk meminta izin lagi
                    Toast.makeText(activity, "Izin Bluetooth diperlukan untuk melanjutkan.", Toast.LENGTH_SHORT).show();
                } else {
                    // Pengguna memilih "Don't Ask Again"
                    Toast.makeText(activity, "Aktifkan izin di pengaturan aplikasi.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", activity.getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(intent);
                }
            }
        }
    }

    private void connectBluetooth() {
        try {
            mBluetoothSocket = mBluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID);
            mBluetoothAdapter.cancelDiscovery();
            mBluetoothSocket.connect();
            connectionHandler.sendEmptyMessage(0);
        } catch (IOException eConnectException) {
            Log.d(TAG, "CouldNotConnectToSocket", eConnectException);
            closeSocket(mBluetoothSocket);
        }
    }

    private void closeSocket(BluetoothSocket openSocket) {
        try {
            openSocket.close();
            Log.d(TAG, "SocketClosed");
        } catch (IOException e) {
            Log.d(TAG, "CouldNotCloseSocket", e);
        }
    }

    private final Handler connectionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mBluetoothConnectProgressDialog.dismiss();
            JApplication.isPrinterConnected = true;
            JApplication.getInstance().mBluetoothAdapter = mBluetoothAdapter;
            JApplication.getInstance().mBluetoothSocket = mBluetoothSocket;
            JApplication.getInstance().mBluetoothDevice = mBluetoothDevice;
            Toast.makeText(activity, "Printer Connected", Toast.LENGTH_SHORT).show();
        }
    };

}

