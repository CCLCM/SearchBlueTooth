package com.example.searchbluetoot;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends Activity implements View.OnClickListener {
    private  static final String TAG = "chencl_";
    // 本地蓝牙适配器
    private BluetoothAdapter mBluetoothAdapter;

    // 搜索到蓝牙添加
    private TextView tvDevices;

    // 搜索蓝牙的按钮
    private Button btnSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        tvDevices = (TextView) findViewById(R.id.tvDevices);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        // 获取本地蓝牙适配器
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // 判断手机是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 判断是否打开蓝牙
        if (!mBluetoothAdapter.isEnabled()) {
        // 不做提示，强行打开
          mBluetoothAdapter.enable();
        }
        Log.i(TAG, " 本机器蓝牙名称 " +  mBluetoothAdapter.getName() +"   "+  mBluetoothAdapter.getAddress());

        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (bondedDevices.size() > 0) {
            for (BluetoothDevice device : bondedDevices ) {
                Log.i(TAG, " 已配对蓝牙 : " +  device.getName() +"   "+  device.getAddress());
            }
        }

        /**
         * 异步搜索蓝牙设备——广播接收
         */
        // 找到设备的广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        // 注册广播
        registerReceiver(receiver, filter);
    }

    @Override

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                Log.i(TAG, " btnSearch " );
                // 设置进度条
                // 判断是否在搜索,如果在搜索，就取消搜索
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                // 开始搜索
                mBluetoothAdapter.startDiscovery();
                break;
        }

    }

// 广播接收器

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 收到的广播类型
            String action = intent.getAction();

            // 发现设备的广播
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // 从intent中获取设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 判断是否配对过
                Log.i(TAG, device.getName() + " " + device.getAddress() );
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    // 添加到列表
                    tvDevices.append(device.getName() + ": " + device.getAddress() + "\n");

                }
                // 搜索完成
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                // 关闭进度条
                Log.i(TAG,  " 搜索完成 ") ;
            }
        }
    };
}
