package com.codingblocks.shortlr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainAct";
    EditText etUrl, etSecret, etCustom;
    Button btnShorten, btnCopy, btnPaste;
    TextView tvResult;
    ClipboardManager myClipboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(this,CBWatcherService.class);
        startService(i);

        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);


        etUrl = (EditText) findViewById(R.id.et_url);
        etSecret = (EditText) findViewById(R.id.et_secret);
        etCustom = (EditText) findViewById(R.id.et_customShortcode);
        btnShorten = (Button) findViewById(R.id.btn_Shorten);
        btnCopy = (Button) findViewById(R.id.btn_copyToclip);
        btnPaste = (Button) findViewById(R.id.btn_Paste);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btnShorten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String urlToShort = etUrl.getText().toString();
                PostBody postBody = new PostBody(urlToShort, etSecret.getText().toString(), etCustom.getText().toString());

                String urlToPost = "http://cb.lk/api/v1/";
                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
                ShortenApi shortenApi = retrofit.create(ShortenApi.class);

                shortenApi.getResult(postBody).enqueue(new Callback<Result>() {

                    @Override
                    public void onResponse(Call<Result> call, Response<Result> response) {
                        tvResult.setText("cb.lk/" + response.body().shortcode);

                    }

                    @Override
                    public void onFailure(Call<Result> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
            }
        });

        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData myClip;
                String text = tvResult.getText().toString();
                myClip = ClipData.newPlainText("text", text);
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(MainActivity.this, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        btnPaste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipData abc = myClipboard.getPrimaryClip();
                if (abc != null) {
                    ClipData.Item item = abc.getItemAt(0);
                    String text = item.getText().toString();
                    Log.d(TAG, "onClick: " + text);
                    etUrl.setText(text);
                }
            }
        });
    }
}
