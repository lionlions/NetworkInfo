package com.sample.cindy.networkinfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;

public class NetworkStatusDetector extends Service {

    private final String TAG = getClass().getSimpleName();
    private Handler mUiHandler;

    public NetworkStatusDetector() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mUiHandler = MainActivity.mUiHandler;
        mHandler.post(mRunnable);

        return super.onStartCommand(intent, flags, startId);
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            int connectType = getConnectType();
            if(connectType==ConnectivityManager.TYPE_WIFI){
                WifiManager wifiManager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
                int linkSpeed = wifiManager.getConnectionInfo().getLinkSpeed();
                boolean isWifiSlow = isWifiSlow(linkSpeed);
                if(mUiHandler!=null){
                    Message message = new Message();
                    message.what = MainActivity.SHOW_STATUS;
                    message.obj = "NetworkStatus: WIFI / Speed: " + linkSpeed + WifiInfo.LINK_SPEED_UNITS + " / "
                                    + (isWifiSlow? "Slow":"Fast");
                    mUiHandler.sendMessage(message);
                }
            }else if(connectType==ConnectivityManager.TYPE_MOBILE){
                TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                int networkType = telephonyManager.getNetworkType();
                String networkTypeName = getMobileNetworkType(networkType);
                boolean isNetworkSlow = isNetworkSlow(networkType);
                if(mUiHandler!=null){
                    Message message = new Message();
                    message.what = MainActivity.SHOW_STATUS;
                    message.obj = "NetworkStatus: Mobile Network / TypeName: " + networkTypeName + " / "
                                     + (isNetworkSlow? "Slow":"Fast");
                    mUiHandler.sendMessage(message);
                }
            }
            mHandler.postDelayed(mRunnable, 5000);

        }
    };

    private int getConnectType() {
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connectivityManager!=null){
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo==null? 0:networkInfo.getType();
    }

    @Override
    public void onDestroy() {
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }

    public String getMobileNetworkType(int aNetworkType) {
        return NetworkType.getNetworkTypeName(aNetworkType);
    }

    public boolean isNetworkSlow(int aNetworkType){
        return NetworkType.isNetworkSlow(aNetworkType);
    }

    public boolean isWifiSlow(int aLinkSpeed){
        if (aLinkSpeed > 10) {
            return false;
        } else {
            return true;
        }
    }
}
