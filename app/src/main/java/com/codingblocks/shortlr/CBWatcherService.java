package com.codingblocks.shortlr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import pl.tajchert.nammu.Nammu;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CBWatcherService extends Service {
    private final String TAG = "MyWatcherService";
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            Intent i=new Intent(CBWatcherService.this,DummyActivity.class);
            startActivity(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(Settings.canDrawOverlays(CBWatcherService.this)){
                performClipboardCheck();}
            }
        }
    };

    @Override
    public void onCreate() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb.hasPrimaryClip()) {
            ClipData cd = cb.getPrimaryClip();

            if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                String clipboardText = cd.getItemAt(0).getText().toString();
                Pattern p = Pattern.compile(URL_REGEX);
                Matcher m = p.matcher(clipboardText);
                if (m.find()) {
                    if (!Utils.getHost(clipboardText).equals("cb.lk")) {
                        showView(clipboardText);
                    }
                }

            }
        }
    }

    public void showView(final String url) {
        final WindowManager manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.alpha = 1.0f;
        layoutParams.packageName = getPackageName();
        layoutParams.buttonBrightness = 1f;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        final View view = View.inflate(getApplicationContext(), R.layout.window_layout, null);
        view.setBackgroundColor(Color.parseColor("#ffffff"));
        Button yesButton = (Button) view.findViewById(R.id.yesButton);
        Button noButton = (Button) view.findViewById(R.id.noButton);
        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String urlToShort = url;
                PostBody postBody = new PostBody(urlToShort, null, null);

                String urlToPost = "http://cb.lk/api/v1/";
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
                ShortenApi shortenApi = retrofit.create(ShortenApi.class);

                shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        String shortUrl = "cb.lk/" + response.body().shortcode;
                        Utils.saveToClipboard(shortUrl, CBWatcherService.this);
                        manager.removeView(view);
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        t.printStackTrace();
                    }
                });


            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                manager.removeView(view);
            }
        });
        manager.addView(view, layoutParams);
    }
}

