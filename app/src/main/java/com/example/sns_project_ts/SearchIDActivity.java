package com.example.sns_project_ts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SearchIDActivity extends AppCompatActivity {
    private static final String TAG = "SearchIDActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_id);

        findViewById(R.id.checkIDButton).setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v.getId() == R.id.checkIDButton){

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
                                    String userEmail =  userData.get("email").toString();
                                    sendSMS(userEmail, userNumber);
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

    private void sendSMS(String email, String number){
        View dialogView = (View) View.inflate(SearchIDActivity.this,R.layout.search_id_dialog,null);
        AlertDialog.Builder dlg = new AlertDialog.Builder(this)
                .setTitle("코드 입력")
                .setView(dialogView)
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startToast("취소했습니다");
                    }
                });
        dlg.show();
    }
}