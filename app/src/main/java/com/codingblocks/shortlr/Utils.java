package com.codingblocks.shortlr;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by piyush0 on 11/04/17.
 */

public class Utils {
    public static final String TAG = "Utils";

    public static String getFromClip(Context context) {
        String retVal = null;
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData abc = myClipboard.getPrimaryClip();
        if (abc != null) {
            ClipData.Item item = abc.getItemAt(0);
            String text = item.getText().toString();
            retVal = text;
        }
        return retVal;
    }

    public static String saveToClipboard(String text, Context context) {
        ClipData myClip;

        myClip = ClipData.newPlainText("text", text);
        ClipboardManager myClipboard;
        myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        myClipboard.setPrimaryClip(myClip);
        Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show();
        return text;
    }



}
