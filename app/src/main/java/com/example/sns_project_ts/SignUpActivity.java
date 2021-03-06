package com.example.sns_project_ts;

import static java.sql.DriverManager.println;

import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class SignUpActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
//    private String newToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.signUpOKBtn).setOnClickListener(onClickListener);

//        userToken();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.signUpOKBtn) {
                signUp();
            }
        }
    };

    private void signUp() {
        String email = ((EditText) findViewById(R.id.editEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editPw)).getText().toString();
        String passwordCheck = ((EditText) findViewById(R.id.editPwCheck)).getText().toString();


        if (email.length() > 0 && password.length() > 0 && passwordCheck.length() > 0) {
            if (password.equals(passwordCheck)) {

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    startToast("???????????? ?????????????????????");
                                    myStartActivity(MemberInitActivity.class, email);
                                    finish();
                                } else {
                                    if (task.getException() != null) {
                                        startToast("?????? ???????????? ???????????????");
                                    }
                                }
                            }
                        });
            } else {
                startToast("??????????????? ???????????? ????????????");
            }
        } else {
            startToast("???????????? ??????????????? ?????? ????????? ?????????");
        }


    }

    private void startToast(String msg) {
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }


    private void myStartActivity(Class c, String email) {
        Intent intent = new Intent(this, c);
        intent.putExtra("email", email.toString());
//        intent.putExtra("newToken", newToken);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // ????????? ?????? ?????????????????? ?????????????????? ???????????? ??????
        startActivity(intent);
    }

//    private void userToken() {
//        FirebaseMessaging.getInstance().getToken()
//                .addOnCompleteListener(new OnCompleteListener<String>() {
//                    @Override
//                    public void onComplete(@NonNull Task<String> task) {
//                        if (!task.isSuccessful()) {
//                            Log.e("SignUpActivity", "?????? ??????????????? ?????????", task.getException());
//                            return;
//                        }
//
//                        newToken = task.getResult();
//                    }
//                });
//
//    }
}

