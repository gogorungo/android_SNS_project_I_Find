package com.example.sns_project_ts;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginBtn).setOnClickListener(onClickListener);
        findViewById(R.id.signUpBtn).setOnClickListener(onClickListener);
        findViewById(R.id.searchIdBtn).setOnClickListener(onClickListener);
        findViewById(R.id.searchPwBtn).setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
//        android.os.Process.killProcess(android.os.Process.myPid()); // 안쓰는것이 좋음
        finish();
        System.exit(1);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.loginBtn) {
                login();
            } else if (id == R.id.signUpBtn) {
                myStartActivity(SignUpActivity.class);
            } else if (id == R.id.searchIdBtn) {
                myStartActivity(SearchIDActivity.class);
            } else if (id == R.id.searchPwBtn) {
                myStartActivity(PasswordResetActivity.class);
            }
        }
    };

    private void login(){
        String email = ((EditText)findViewById(R.id.inputID)).getText().toString();
        String password = ((EditText)findViewById(R.id.inputPW)).getText().toString();

        if(email.length() > 0 ) {
            if(password.length() > 0) {

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startToast("로그인 되었습니다");
                                    myStartActivity(MainActivity.class);
                                } else {
                                    if (task.getException() != null) {
                                        startToast("비밀번호가 틀렸습니다");
                                    }
                                }
                            }
                        });
            } else {
                startToast("비밀번호를 입력해주세요");
            }
        }else{
            startToast("아이디를 입력해주세요");
        }


    }

    private void startToast(String msg){
        Toast.makeText(this, msg,
                Toast.LENGTH_SHORT).show();
    }

    private void myStartActivity(Class c){
        Intent intent = new Intent(this,c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 로그인 이후 백스페이스로 로그인창으로 돌아가지 않음
        startActivity(intent);
    }

}
