package com.codingblocks.shortlr.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class ShortenActivity extends Activity {
    public static final String TAG = "ShortenAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorten);
        if (getIntent().getAction().equals("android.intent.action.MAIN")) {
            //TODO : Nothing to do.
            Log.d(TAG, "onCreate: " + "android.intent.action.MAIN");
        } else {

            CharSequence text = getIntent()
                    .getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            boolean readonly = getIntent()
                    .getBooleanExtra(Intent.EXTRA_PROCESS_TEXT_READONLY, false);


            if (readonly) {
                String urlToShort = text.toString();
                PostBody postBody = new PostBody(urlToShort, "", "");

                String urlToPost = "http://cb.lk/api/v1/";
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
                ShortenApi shortenApi = retrofit.create(ShortenApi.class);

                shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        String replacementText = "cb.lk/" + response.body().getShortcode();
                        Utils.saveToClipboard(replacementText, ShortenActivity.this);
                        Toast.makeText(ShortenActivity.this, "Link Copied To Clipboard", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, replacementText);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            } else {

                String urlToShort = text.toString();
                PostBody postBody = new PostBody(urlToShort, "", "");

                String urlToPost = "http://cb.lk/api/v1/";
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
                ShortenApi shortenApi = retrofit.create(ShortenApi.class);

                shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        String replacementText = "cb.lk/" + response.body().getShortcode();
                        Intent intent = new Intent();
                        intent.putExtra(Intent.EXTRA_PROCESS_TEXT, replacementText);
                        setResult(RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }

        }
    }

}
