package com.codingblocks.shortlr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShortenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorten);
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
                    String replacementText = "cb.lk/" + response.body().shortcode;
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
                    String replacementText = "cb.lk/" + response.body().shortcode;
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
