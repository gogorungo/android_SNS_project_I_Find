package com.example.sns_project_ts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SearchIDActivity extends AppCompatActivity {
    private static final String TAG = "SearchIDActivity";

    private static RequestQueue requestQueue;
    private static String regId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_id);

        findViewById(R.id.checkIDButton).setOnClickListener(onClickListener);


        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();

            if(id == R.id.checkIDButton){

                String number = ((EditText) findViewById(R.id.searchID)).getText().toString();

                if(number != null){
                    if(number.length() == 11){
                        if(Pattern.matches("^[0-9]*$",number)){
                            searchId(number);
                        }else{
                            startToast("숫자만 입력해주세요(-제외)");
                        }
                    }else{
                        startToast("전화번호는 11자리 숫자입니다(-제외)");
                    }
                }else{
                    startToast("전화번호를 입력해주세요");
                }

            }
        }
    };



    private void searchId(String number){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        boolean flag = true;

                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "find_id_log " + document.getId() + " => " + document.getData() + " userEmail" );

                                Map<String, Object> userData = document.getData();
                                String userNumber = userData.get("phoneNumber").toString();
                                if(userNumber.equals(number)){
                                    String userEmail = userData.get("email").toString();
                                    sendSMS(userEmail);
                                    flag = false;
                                    break;
                                }
                            }
                            if(flag){
                                startToast("해당 번호가 존재하지 않습니다");
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void sendSMS(String email){

        View dialogView = (View) View.inflate(SearchIDActivity.this,R.layout.dialog_search_id,null);
        ProgressBar timerBar = (ProgressBar) dialogView.findViewById(R.id.timerBar);
        TextView timerNumber = (TextView) dialogView.findViewById(R.id.timerNumber);

        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle("코드 입력")
                .setView(dialogView)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startToast("취소했습니다");
                    }
                });

        AlertDialog aDialog = dlg.create();

        int randomCode = (int) (1 + Math.random() * 999999);
        Log.d(TAG, "codeNumber1 "+ randomCode);

        EditText codeText = (EditText) dialogView.findViewById(R.id.inputCode);
        Button codeBtn = (Button) dialogView.findViewById(R.id.checkCodeBtn);


        codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codeA = codeText.getText().toString();
                String codeQ = String.valueOf(randomCode);

                send(codeQ);
                Log.d("codeQ")

                if(codeA.equals(codeQ)){
                    aDialog.cancel();
                    conFirmID(email);


                }else{
                    startToast("코드가 다릅니다");
                }
            }
        });




        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = timerBar.getProgress() ; i > 0 ; i--) {
                    timerBar.setProgress(timerBar.getProgress() - 1);
                    timerNumber.setText("남은 시간 : " + timerBar.getProgress());
                    SystemClock.sleep(1000);
                }
                SearchIDActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(SearchIDActivity.this, "시간이 초과되었습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                aDialog.cancel();
            }
        });

        th.setDaemon(true);
        th.start();

        aDialog.show();
    }




    private void conFirmID(String email){

        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle("아이디 확인")
                .setMessage(email)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startToast("확인되었습니다");
                        finish();
                    }
                });

        AlertDialog alertDialog = dlg.show();
        TextView messageText = (TextView)alertDialog.findViewById(android.R.id.message);
        messageText.setTextSize(25);
        alertDialog.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null) {
            processIntent(intent);
        }
    }

    private void processIntent(Intent intent){
        String from = intent.getStringExtra("from");
        if(from == null){
            return;
        }

        String contents = intent.getStringExtra("contents");
        startToast("contents");
    }

    private void send(String input){
        JSONObject requestData = new JSONObject();

        try {
            requestData.put("priority","high");

            JSONObject dataObj = new JSONObject();
            dataObj.put("contents", input);
            requestData.put("data",dataObj);

            JSONArray idArray = new JSONArray();
            idArray.put(0, regId);
            requestData.put("registration_ids",idArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sendData(requestData, new SendResponseListener(){
            @Override
            public void onRequestCompleted() {

            }

            @Override
            public void onRequestStarted() {

            }

            @Override
            public void onRequestWithError(VolleyError error) {

            }
        });

    }

    public interface SendResponseListener {
        public void onRequestStarted();
        public void onRequestCompleted();
        public void onRequestWithError(VolleyError error);
    }

    public void sendData(JSONObject requestData, final SendResponseListener listener){
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listener.onRequestCompleted();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }

        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();


                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> headers = new HashMap<String, String>();
                headers.put("Authorization",
                        "key=AAAA5VpToT4:APA91bFwnY5WE4bmDN52ENuR1HGk9lKbE8LE_qjxqyuTzS0P9R6QO1yNZfpdv6sJTZ3dj68KkOMLNkL9cwtsv_wQpSrAYh0Bfd_gtCOCT8VVaRwjNYDtJn_869IfSbi8elfYOBtaEdsB");
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);
    }

}
