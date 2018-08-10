package com.santossingh.bluetoothfiletransfer;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Create Objects-------------------------------------------------------
    static final int CUSTOM_DIALOG_ID = 0;
    File root, curFolder;
    private static final int DISCOVER_DURATION = 300;
    private static final int REQUEST_BLU = 1;
    static String path;

    BluetoothAdapter btAdatper = BluetoothAdapter.getDefaultAdapter();
    //---------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("-------------------------in onCreate", "on");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
        curFolder = root;
        path = curFolder.getAbsolutePath().toString();
        Log.d("path----------->", path);
        sendViaBluetooth();
    }


    //exit to application---------------------------------------------------------------------------
    public void exit(View V) {
        btAdatper.disable();
        Toast.makeText(this,"*** Now Bluetooth is off... Thanks. ***",Toast.LENGTH_LONG).show();
        finish(); }

    //Method for send file via bluetooth------------------------------------------------------------
    public void sendViaBluetooth() {
        Log.d("------------------> sendViaBluetooth", "on");
            if (btAdatper == null) {
                Toast.makeText(this, "Device not support bluetooth", Toast.LENGTH_LONG).show();
            } else {
                enableBluetooth();
            }
    }

    public void enableBluetooth() {
        Log.d("->>>>>>>>>>>>>>>>>>>>>>>>>> enableBluetooth", "on");
        Intent discoveryIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoveryIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVER_DURATION);
        startActivityForResult(discoveryIntent, REQUEST_BLU);
    }

    //Override method for sending data via bluetooth availability--------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == DISCOVER_DURATION && requestCode == REQUEST_BLU) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_SEND);
            i.setType("*/*");
            File file = new File(path + "/indigo/out/data.txt");

            i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));

            PackageManager pm = getPackageManager();
            List<ResolveInfo> list = pm.queryIntentActivities(i, 0);
            if (list.size() > 0) {
                String packageName = null;
                String className = null;
                boolean found = false;

                for (ResolveInfo info : list) {
                    packageName = info.activityInfo.packageName;
                    if (packageName.equals("com.android.bluetooth")) {
                        className = info.activityInfo.name;
                        found = true;
                        break;
                    }
                }
                //CHECK BLUETOOTH available or not------------------------------------------------
                if (!found) {
                    Log.d("1", "----------------------------------->");
                    Toast.makeText(this, "Bluetooth not been found", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("2", "----------------------------------->");
                    i.setClassName(packageName, className);
                    startActivity(i);
                }
            }
        } else {
            Log.d("3", "----------------------------------->");
            Toast.makeText(this, "Bluetooth is cancelled", Toast.LENGTH_LONG).show();
        }
    }

}
