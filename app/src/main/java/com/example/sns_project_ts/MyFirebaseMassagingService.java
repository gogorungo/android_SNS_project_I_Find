package com.example.sns_project_ts;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMassagingService extends FirebaseMessagingService {
    private static final String TAG = "FMS";


    public MyFirebaseMassagingService() {

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.e(TAG,"onNewToken 호출됨: "+ token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.e(TAG,"onMessageReceived 호출됨");

        String from = message.getFrom();
        Map<String, String> data = message.getData();
        String contents = data.get("contents");

        Log.d(TAG,"from : " + from + ", contents : "+contents);
        sendToActivity(getApplicationContext(), from, contents);
    }

    private void sendToActivity(Context context, String from, String contents){
        Intent intent = new Intent(context, SearchIDActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("contents", contents);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|
                Intent.FLAG_ACTIVITY_SINGLE_TOP|
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }
}