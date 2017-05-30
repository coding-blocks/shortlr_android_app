package com.codingblocks.shortlr;

import android.app.Application;
import android.content.Intent;

import com.codingblocks.shortlr.services.CBWatcherService;

/**
 * Created by piyush0 on 30/05/17.
 */

public class ShortlrApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startWatcherService();
    }

    private void startWatcherService() {
        Intent i = new Intent(this, CBWatcherService.class);
        startService(i);
    }
}
