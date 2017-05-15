package com.codingblocks.shortlr.widget;

import android.app.PendingIntent;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.codingblocks.shortlr.models.PostBody;
import com.codingblocks.shortlr.R;
import com.codingblocks.shortlr.models.Result;
import com.codingblocks.shortlr.api.ShortenApi;
import com.codingblocks.shortlr.Utils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by piyush0 on 11/04/17.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "MyWidgetProvider";
    private static final String SHORTENCLICK = "myOnClickTag1";
    private static final String PASTECLICK = "myOnClickTag2";
    public static final String MyPREFERENCES = "Prefs";


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        Log.d(TAG, "onUpdate: ");

        // Get all ids
        ComponentName thisWidget = new ComponentName(context,
                MyWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        for (int widgetId : allWidgetIds) {

            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            remoteViews.setInt(R.id.widget_container, "setBackgroundColor", Color.BLACK);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            Drawable drawable = wallpaperManager.getDrawable();

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            // Async couldn't change the bitmap, going with synchronous pallete check
            Palette p = Palette.from(bitmap).generate();
            int colorToShow = Color.BLACK;
            int paletteColor = p.getDarkMutedColor(colorToShow);
            if (paletteColor == colorToShow) {
                paletteColor = p.getDarkVibrantColor(Color.BLACK);
            }
            Log.d("Widget", "Color is" + paletteColor);
            remoteViews.setInt(R.id.widget_container, "setBackgroundColor", paletteColor);


            remoteViews.setOnClickPendingIntent(R.id.widget_btn_shorten, getPendingSelfIntent(context, SHORTENCLICK));
            remoteViews.setOnClickPendingIntent(R.id.widget_btn_paste, getPendingSelfIntent(context, PASTECLICK));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        super.onReceive(context, intent);
        SharedPreferences sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String url = sharedPreferences.getString("url", "");
        setTextOnTv(context, url);
        if (SHORTENCLICK.equals(intent.getAction())) {
            if (!Utils.isUrl(url)) {
                Toast.makeText(context, "Not a url", Toast.LENGTH_SHORT).show();
            } else {
                if (Utils.getHost(url).equals("cb.lk")) {
                    Toast.makeText(context, "Already a short url", Toast.LENGTH_SHORT).show();
                } else {
                    getShortenedUrl(url, context);
                }
            }

        } else if (PASTECLICK.equals(intent.getAction())) {

            String text = Utils.getFromClip(context);
            setTextOnTv(context, text);
            saveToSharedPrefs(text, context);
        }
    }

    private void saveToSharedPrefs(String str, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("url", str);
        editor.apply();
    }

    private void setTextOnTv(Context context, String text) {
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.widget_layout);
        views.setTextViewText(R.id.widget_tv_url, text);
        AppWidgetManager.getInstance(context).updateAppWidget(
                new ComponentName(context, MyWidgetProvider.class), views);
    }

    public void getShortenedUrl(String url, final Context context) {
        PostBody postBody = new PostBody(url, "", "");
        final String[] shortURL = {""};
        String urlToPost = "http://cb.lk/api/v1/";
        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
        ShortenApi shortenApi = retrofit.create(ShortenApi.class);

        shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {

                shortURL[0] = "cb.lk/" + response.body().getShortcode();

                Log.d(TAG, "onResponse: " + response.body().getLongURL());
                Log.d(TAG, "onResponse: " + shortURL[0]);


                saveToSharedPrefs(shortURL[0], context);
                setTextOnTv(context, shortURL[0]);
                Utils.saveToClipboard(shortURL[0], context);

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }


}