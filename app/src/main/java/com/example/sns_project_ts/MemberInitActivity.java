package com.example.sns_project_ts;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

public class MemberInitActivity extends AppCompatActivity {
    private static final String TAG = "MemberInitActivity";
    private String dbName, dbPhoneNumber, dbBirthDay, dbAddress, dbEmail;
    private boolean flag = true;
    String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_init);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            userUid = user.getUid();
        }


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(user.getUid());



        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if(document != null){
                        if (document.exists()) {
                            flag = false;
                            // ??????????????? ????????? ?????? ??????
                            Map<String, Object> hm = document.getData();
                            dbName = hm.get("name").toString();
                            dbPhoneNumber = hm.get("phoneNumber").toString();
                            dbBirthDay = hm.get("birthDay").toString();
                            dbAddress = hm.get("address").toString();
                            dbEmail = hm.get("email").toString();
//                            dbToken = hm.get("userToken").toString();


                            EditText etName = (EditText) findViewById(R.id.nameEditText);
                            EditText etPhoneNumber = (EditText) findViewById(R.id.phoneNumberEditText);
                            EditText etBirthDay = (EditText) findViewById(R.id.birthDayEditText);
                            EditText etAddress = (EditText) findViewById(R.id.addressEditText);

                            etName.setText(dbName);
                            etPhoneNumber.setText(dbPhoneNumber);
                            etBirthDay.setText(dbBirthDay);
                            etAddress.setText(dbAddress);
                        }
                    }
                }
            }
        });

        findViewById(R.id.checkButton).setOnClickListener(onClickListener);
        findViewById(R.id.logoutButton).setOnClickListener(onClickListener);
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        if(flag){
            finish();
            onDestroy();
        }else{
            finish();
        }
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.checkButton) {
                profileUpdate();
            } else if (id == R.id.logoutButton) {
                FirebaseAuth.getInstance().signOut();
                myStartActivity(LoginActivity.class);
            }
        }
    };

    private void profileUpdate(){
        Intent intent = getIntent();

        String email = intent.getStringExtra("email");
//        String userToken = intent.getStringExtra("newToken");

        String name = ((EditText)findViewById(R.id.nameEditText)).getText().toString();
        String phoneNumber = ((EditText)findViewById(R.id.phoneNumberEditText)).getText().toString();
        String birthDay = ((EditText)findViewById(R.id.birthDayEditText)).getText().toString();
        String address = ((EditText)findViewById(R.id.addressEditText)).getText().toString();



        if(name.length() > 0 ) {
            if(Pattern.matches("^[0-9]*$",phoneNumber)) {
                if(phoneNumber.length() == 11) {
                    if(Pattern.matches("^[0-9]*$",phoneNumber)) {
                        if (birthDay.length() == 8) {
                            if(address.length() > 0) {

                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // db??? ?????? ?????? ??? ?????????
                                FirebaseFirestore db = FirebaseFirestore.getInstance();

                                MemberInfo memberInfo;
                                if(email != null) {
                                    memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, email, userUid);
                                }else{
                                    memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, dbEmail, userUid);
                                }

                                if (user != null) {
                                    db.collection("users").document(user.getUid()).set(memberInfo)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    startToast("???????????? ????????? ??????????????????");
                                                    finish();
                                                    myStartActivity(MainActivity.class);
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    startToast("???????????? ????????? ?????????????????????");
                                                    Log.w(TAG, "Error writing document", e);
                                                }
                                            });
                                }
                            }else{
                                startToast("????????? ??????????????????");
                            }
                        } else {
                            startToast("??????????????? ?????????????????? (ex. 19950101)");
                        }
                    }else{
                        startToast("??????????????? ????????? ?????? ??????????????? (ex. 19950101)");
                    }
                }else{
                    startToast("????????? ????????? ?????????????????? (-??????)");
                }
            }else{
                startToast("????????? ????????? ????????? ?????????????????? (-??????)");
            }
        }else{
            startToast("????????? ??????????????????");
        }
    }

    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        startActivity(intent);
    }

}
