package com.nk.androidserialport;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends Activity {
    private String TAG = "MainActivity";
    private Button button;
    private TextView tv;
    private SerialPortUtil serialPortUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn);
        tv = findViewById(R.id.tv);
        serialPortUtil = new SerialPortUtil();
        serialPortUtil.openSerialPort();
        //注册EventBus
        EventBus.getDefault().register(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                serialPortUtil.sendSerialPort(Cmd.OPEN_DOOR);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (serialPortUtil != null) {
            serialPortUtil.closeSerialPort();
        }
    }

    /**
     * 用EventBus进行线程间通信，也可以使用Handler
     * @param string
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(String string){
        Log.d(TAG,"获取到了从传感器发送到Android主板的串口数据");
        tv.setText(string);
    }
}
