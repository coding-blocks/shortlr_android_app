package com.codingblocks.shortlr.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import com.codingblocks.shortlr.R;

public class GetPermissionActivity extends Activity {
    SharedPreferences sharedPreferences;
    private static final String SHARED_PREFS = "sp_permission_count";
    private static final String PERMISSION = "Permission";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_permission);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(GetPermissionActivity.this)) {
                int num = sharedPreferences.getInt(PERMISSION, 0);
                if (num == 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PERMISSION, num + 1);
                    editor.apply();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 0);
                    finish();
                }
            }
        }
    }
}