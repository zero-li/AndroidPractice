package com.zhhli.sip.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class SipNetworkReceiver extends BroadcastReceiver {

    public interface SHNetworkObserver {
        void onChange(boolean connect);
    }

    private static class SHNetworkReceiverHolder {
        private static final SipNetworkReceiver Instance = new SipNetworkReceiver();
    }

    private List<SHNetworkObserver> observers = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnect = isOnline(context);
        for (SHNetworkObserver observer : observers) {
            observer.onChange(isConnect);
        }
    }


    public static void registerReceiver(Context context, SHNetworkObserver observer) {

        if (observer != null && !SHNetworkReceiverHolder.Instance.observers.contains(observer)) {
            SHNetworkReceiverHolder.Instance.observers.add(observer);
        }

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.getApplicationContext().registerReceiver(SHNetworkReceiverHolder.Instance, filter);
    }

    public static void unregisterReceiver(Context context, SHNetworkObserver observer) {
        if (observer != null && !SHNetworkReceiverHolder.Instance.observers.contains(observer)) {
            SHNetworkReceiverHolder.Instance.observers.remove(observer);
        }
        context.getApplicationContext().unregisterReceiver(SHNetworkReceiverHolder.Instance);
    }


    public boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
