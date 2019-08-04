package com.example.android.taskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText loginIdView;
    private EditText passView;
    private Button loginBtnView;
    private TextView regTxtView;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginIdView = findViewById(R.id.enter_id);
        passView = findViewById(R.id.enter_password);
        loginBtnView = findViewById(R.id.login_btn);
        regTxtView = findViewById(R.id.reg_txt);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);

        if(mAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }

        loginBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String loginId = loginIdView.getText().toString().trim();
                String pass = passView.getText().toString().trim();
                if(TextUtils.isEmpty(loginId)) {
                    loginIdView.setError("Required..");
                } else if(!loginId.matches("^(.+)@(.+)\\.(.+)")) {
                    loginIdView.setError("Please enter a valid email id");
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    passView.setError("Required..");
                }
                mDialog.setMessage("Processing..");
                mDialog.show();
                mAuth.signInWithEmailAndPassword(loginId, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong Credentials", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });

        regTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mAuth.getCurrentUser() == null) {
            ActivityCompat.finishAffinity(MainActivity.this);
        } else {
            super.onBackPressed();
        }
    }
}
