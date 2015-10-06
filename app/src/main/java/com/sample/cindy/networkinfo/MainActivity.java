package com.sample.cindy.networkinfo;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * @author Cindy
 * */

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private static TextView vStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        processView();
        startService();

    }

    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, NetworkStatusDetector.class);
        startService(intent);
    }

    private void processView() {
        vStatus = (TextView)findViewById(R.id.status);
    }

    public final static int SHOW_STATUS = 0x159;
    private static int mLineCount = 0;
    public static Handler mUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch(msg.what){
                case SHOW_STATUS:
                    if(mLineCount<10){
                        mLineCount++;
                    }else{
                        mLineCount = 0;
                        vStatus.setText("");
                    }
                    vStatus.setText(vStatus.getText() + (String)msg.obj + "\n");
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {

        Intent intent = new Intent();
        intent.setClass(this, NetworkStatusDetector.class);
        stopService(intent);

        super.onDestroy();
    }
}
