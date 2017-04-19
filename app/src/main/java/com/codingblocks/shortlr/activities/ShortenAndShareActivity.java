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

public class ShortenAndShareActivity extends Activity {
    public static final String TAG = "SnSAct";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shorten_and_share);


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {

                String urlToShort = intent.getStringExtra(Intent.EXTRA_TEXT);
                String hostName = Utils.getHost(urlToShort);
                Log.d(TAG, "onCreate: " + hostName);
                if (hostName.equals("cb.lk")) {
                    Toast.makeText(this, "Please use another app to share the link!", Toast.LENGTH_SHORT).show();
                } else {
                    PostBody postBody = new PostBody(urlToShort, "", "");

                    String urlToPost = "http://cb.lk/api/v1/";
                    Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
                    ShortenApi shortenApi = retrofit.create(ShortenApi.class);

                    shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

                        @Override
                        public void onResponse(Call<Result> call, Response<Result> response) {

                            String shortenedURL = "cb.lk/" + response.body().shortcode;
                            Intent i = new Intent(Intent.ACTION_SEND);
                            i.setType("text/plain");
                            i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                            i.putExtra(Intent.EXTRA_TEXT, shortenedURL);
                            startActivity(Intent.createChooser(i, "Share URL"));
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
}
