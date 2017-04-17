package com.codingblocks.shortlr;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by piyush0 on 11/04/17.
 */

public class MyWidgetProvider extends AppWidgetProvider {

    public static final String TAG = "MyWidgetProvider";
    private static final String ShortenClick = "myOnClickTag1";
    private static final String PasteClick = "myOnClickTag2";
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

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

            remoteViews.setOnClickPendingIntent(R.id.widget_btn_shorten, getPendingSelfIntent(context, ShortenClick));
            remoteViews.setOnClickPendingIntent(R.id.widget_btn_paste, getPendingSelfIntent(context, PasteClick));



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
        if (ShortenClick.equals(intent.getAction())) {
            // your onClick action is here

            Log.d(TAG, "onReceive: " + sharedPreferences.getString("url", "default"));
            getShortenedUrl(url, context);

        } else if (PasteClick.equals(intent.getAction())) {

            String text = Utils.getFromClip(context);
            setTextOnTv(context, text);
            saveToSharedPrefs(text, context);
        }
    }

    private void saveToSharedPrefs(String str, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("url", str);
        editor.commit();
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

                shortURL[0] = "cb.lk/" + response.body().shortcode;

                Log.d(TAG, "onResponse: " + response.body().longURL);
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