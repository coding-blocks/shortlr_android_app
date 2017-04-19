package com.codingblocks.shortlr;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class DummyActivity extends Activity {
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);

        sharedPreferences = getSharedPreferences("MeaningString", MODE_PRIVATE);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(DummyActivity.this)) {
                int num = sharedPreferences.getInt("Permission", 0);

                if (num == 0) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("Permission", num + 1);
                    editor.commit();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, 0);
                    finish();
                } else {

                }

            } else {
                // Hogya
            }
        } else {
            //Check in emulator TODO
        }
    }
}
