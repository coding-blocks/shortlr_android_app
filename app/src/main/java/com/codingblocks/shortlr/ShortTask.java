//package com.codingblocks.shortlr;
//
//import android.os.AsyncTask;
//
//import java.io.IOException;
//
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
///**
// * Created by abhishekyadav on 19/04/17.
// */
//
//public class ShortTask extends AsyncTask<PostBody, Void, String> {
//    @Override
//    protected String doInBackground(PostBody... params) {
//        PostBody postBody = params[0];
//
//        String urlToPost = "http://cb.lk/api/v1/";
//        Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(urlToPost).build();
//        ShortenApi shortenApi = retrofit.create(ShortenApi.class);
//        Response<Result> resultResponse = null;
//        try {
//            resultResponse = shortenApi.getResult(postBody).execute();
//            String retVal =  resultResponse.body().shortcode;
//            Utils.saveToClipboard(retVal, context);
//            return retVal;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
