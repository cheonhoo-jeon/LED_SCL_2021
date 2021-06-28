package kr.co.biomed.Activity;

import android.Manifest;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kr.co.biomed.Adapter.DeviceAdapter;
import kr.co.biomed.Data.Data;
import kr.co.biomed.Interface.ConnectDeviceInterface;
import kr.co.biomed.Interface.DataDeviceFinish;
import kr.co.biomed.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ConnectDeviceInterface {
    final String TAG = "MAIN_ACTIVITY";
    View scanView, scanningView, cancelButton;
    RecyclerView deviceRecyclerView;
    DeviceAdapter deviceAdapter;
    Context mContext;
    Intent intent;
    DataDeviceFinish dataDeviceFinish = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getID();
        setListener();
        setRecycler();
        setBle();
        setPermission();
//        test();
    }

    private void setPermission() {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {

            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setPermissions(Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    private void setBle() {
        BleManager.getInstance().init(getApplication());

        if(!BleManager.getInstance().isSupportBle()){
            Toast.makeText(this, "블루투스를 사용할수없는 모델입니다.", Toast.LENGTH_SHORT).show();
        }

        BleManager.getInstance().enableBluetooth();

        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);

        Data.bleScanCallback = new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }

            @Override
            public void onScanStarted(boolean success) {
                Data.bleDevices = new ArrayList<>();
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Data.bleDevices.add(bleDevice);
                deviceAdapter.setList(Data.bleDevices);
                deviceAdapter.notifyDataSetChanged();
            }
        };

        Data.bleGattCallback = new BleGattCallback() {
            @Override
            public void onStartConnect() {
                Toast.makeText(MainActivity.this, "연결중...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                Toast.makeText(MainActivity.this, "블루투스와 연결을 실패했습니다.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                Data.bleDevice = bleDevice;
                intent = new Intent(mContext, DataActivity.class);
                startActivity(intent);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {
                Log.d("testss", "disConnect");
                if(Data.dataActivity != null){
                    Data.dataActivity.finish();
                    Data.dataActivity = null;
                    Toast.makeText(MainActivity.this, "연결 끊김", Toast.LENGTH_SHORT).show();
                }
            }
        };



    }

    private void setRecycler() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        deviceAdapter  = new DeviceAdapter(new ArrayList<BleDevice>());
        deviceRecyclerView.setLayoutManager(layoutManager);
        deviceRecyclerView.setAdapter(deviceAdapter);
        deviceAdapter.setListener(this);
    }

    private void test() {
        BleManager.getInstance().init(getApplication());

        if(!BleManager.getInstance().isSupportBle())
        {
            Log.d(TAG, "서포트 안된데;");
        }

        BleManager.getInstance().enableBluetooth();

        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setSplitWriteNum(20)
                .setConnectOverTime(10000)
                .setOperateTimeout(5000);

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {

            }

            @Override
            public void onScanStarted(boolean success) {

            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                Log.d(TAG, bleDevice.getName() + " : " + bleDevice.getMac());
                if(bleDevice.getName() != null) {
                    if (bleDevice.getName().equals("JUNIQ-sn00")) {
                        BleManager.getInstance().cancelScan();
                        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
                            @Override
                            public void onStartConnect() {

                            }

                            @Override
                            public void onConnectFail(BleDevice bleDevice, BleException exception) {

                            }

                            @Override
                            public void onConnectSuccess(final BleDevice bleDevice, BluetoothGatt gatt, int status) {
                                Log.d(TAG, "CONNECT_ SUCCESS");
                                for(int i = 0; i < gatt.getServices().size(); i++){
                                    Log.d(TAG, gatt.getServices().get(i).getUuid().toString());
                                }

                                BluetoothGattService service = gatt.getService(UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb"));

                                for(int i= 0;i < service.getCharacteristics().size(); i++){
                                    Log.d(TAG, "test" + service.getCharacteristics().get(i).getUuid().toString());
                                }

                                BleManager.getInstance().notify(
                                        bleDevice,
                                        "0000ffe0-0000-1000-8000-00805f9b34fb",
                                        "0000ffe1-0000-1000-8000-00805f9b34fb",
                                        new BleNotifyCallback() {
                                            @Override
                                            public void onNotifySuccess() {
                                                Log.d(TAG, "NOTIFTY_SUCCESS");
                                            }

                                            @Override
                                            public void onNotifyFailure(BleException exception) {
                                                Log.d(TAG, "NOTIFTY_FAIL");
                                            }

                                            @Override
                                            public void onCharacteristicChanged(byte[] data) {
                                                for(int i =0;i < data.length; i++){
                                                    Log.d(TAG, "Data" + data[i]);

                                                }

                                                BleManager.getInstance().write(bleDevice,
                                                        "0000ffe0-0000-1000-8000-00805f9b34fb",
                                                        "0000ffe1-0000-1000-8000-00805f9b34fb",
                                                        new byte[]{65, 65},
                                                        new BleWriteCallback() {
                                                            @Override
                                                            public void onWriteSuccess(int current, int total, byte[] justWrite) {

                                                            }

                                                            @Override
                                                            public void onWriteFailure(BleException exception) {

                                                            }
                                                        }
                                                );
                                            }
                                        }
                                );

                            }

                            @Override
                            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

                            }
                        });
                    }
                }
            }
        });

    }

    private void setListener() {
        scanView.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    private void getID() {
        mContext = this;
        scanView = findViewById(R.id.scan_layout);
        scanningView = findViewById(R.id.scanning_layout);
        deviceRecyclerView = findViewById(R.id.device_recycler_view);
        cancelButton = findViewById(R.id.cancel_button);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scan_layout :
                clickScan();
                break;
            case R.id.cancel_button :
                clickCancelButton();
        }
    }

    private void clickCancelButton() {
        scanView.setVisibility(View.VISIBLE);
        scanningView.setVisibility(View.GONE);

        BleManager.getInstance().cancelScan();
    }

    private void clickScan() {
        scanView.setVisibility(View.GONE);
        scanningView.setVisibility(View.VISIBLE);

        BleManager.getInstance().scan(Data.bleScanCallback);
    }

    @Override
    public void connect(BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, Data.bleGattCallback);
    }
}
