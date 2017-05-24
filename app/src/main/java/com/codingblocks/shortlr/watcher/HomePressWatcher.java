package com.codingblocks.shortlr.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by panross on 22/5/17.
 */

public class HomePressWatcher {

    public interface onHomePressed {
        void onHomeButtonPressed();
    }


    private Context context;
    private onHomePressed interceptor;
    private InterceptReceiver receiver;

    public HomePressWatcher(Context context) {
        this.context = context;
    }

    public void setInterceptor(onHomePressed interceptor) {
        this.interceptor = interceptor;
        receiver = new InterceptReceiver();
    }

    public void startWatch() {
        context.registerReceiver(receiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }


    public void stopWatch() {
        context.unregisterReceiver(receiver);
    }

    class InterceptReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {

                String reason = intent.getStringExtra("reason");
                if (reason.equals("homekey")) {
                    interceptor.onHomeButtonPressed();
                }
            }
        }
    }


}
