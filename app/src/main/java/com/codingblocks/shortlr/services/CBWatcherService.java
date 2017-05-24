package com.codingblocks.shortlr.services;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.codingblocks.shortlr.R;
import com.codingblocks.shortlr.Utils;
import com.codingblocks.shortlr.activities.GetPermissionActivity;
import com.codingblocks.shortlr.api.ShortenApi;
import com.codingblocks.shortlr.models.PostBody;
import com.codingblocks.shortlr.models.Result;
import com.codingblocks.shortlr.watcher.HomePressWatcher;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class CBWatcherService extends Service {
    public static final String URL_REGEX = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
    private OnPrimaryClipChangedListener listener = new OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(CBWatcherService.this)) {
                    performClipboardCheck();
                } else {
                    Intent i = new Intent(CBWatcherService.this, GetPermissionActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            } else {
                performClipboardCheck();
            }
        }
    };
    private WindowManager manager = null;
    private View view = null;

    @Override
    public void onCreate() {
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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
                    if (!Utils.getHost(clipboardText).equals(getString(R.string.host))) {
                        showView(clipboardText);
                    }
                }

            }
        }
    }

    public void showView(final String url) {
        manager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.alpha = 1.0f;
        layoutParams.packageName = getPackageName();
        layoutParams.buttonBrightness = 1f;
        layoutParams.windowAnimations = android.R.style.Animation_Dialog;

        view = View.inflate(getApplicationContext(), R.layout.window_layout, null);
        ScaleAnimation scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setDuration(300);
        scale.setInterpolator(new AccelerateDecelerateInterpolator());
        view.startAnimation(scale);
        view.setBackgroundColor(Color.parseColor("#ffffff"));
        final HomePressWatcher homePressWatcher = new HomePressWatcher(view.getContext());
        homePressWatcher.setInterceptor(new HomePressWatcher.onHomePressed() {
            @Override
            public void onHomeButtonPressed() {
                homePressWatcher.stopWatch();
                if (view != null)
                    manager.removeView(view);
            }
        });
        homePressWatcher.startWatch();

        ImageView yesButton = (ImageView) view.findViewById(R.id.yesButton);
        ImageView noButton = (ImageView) view.findViewById(R.id.noButton);
        yesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PostBody postBody = new PostBody(url, null, null);

                String urlToPost = getString(R.string.api_endpoint);
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
                ShortenApi shortenApi = retrofit.create(ShortenApi.class);

                shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        String shortUrl = getString(R.string.short_code_prepend) + response.body().getShortcode();
                        Utils.saveToClipboard(shortUrl, CBWatcherService.this);
                        manager.removeView(view);
                        view = null;
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
                view = null;
            }
        });
        manager.addView(view, layoutParams);
    }


}

