package com.example.android.taskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
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

public class RegistrationActivity extends AppCompatActivity {

    private EditText regIdView;
    private EditText passView;
    private  EditText rePassView;
    private Button regBtnView;
    private TextView loginTxtView;
    private FirebaseAuth mAuth;
    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        regIdView = findViewById(R.id.enter_id);
        passView = findViewById(R.id.enter_password1);
        rePassView = findViewById(R.id.enter_password2);
        regBtnView = findViewById(R.id.signup_btn);
        loginTxtView = findViewById(R.id.login_txt);

        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        regBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regId = regIdView.getText().toString().trim();
                String pass = passView.getText().toString().trim();
                String rePass = rePassView.getText().toString().trim();
                if(TextUtils.isEmpty(regId)) {
                    regIdView.setError("Required..");
                    return;
                } else if(!regId.matches("^(.+)@(.+)\\.(.+)")) {
                    regIdView.setError("Please enter a valid email id");
                    return;
                }
                if(TextUtils.isEmpty(pass)) {
                    passView.setError("Required..");
                    return;
                }
                if(TextUtils.isEmpty(rePass)) {
                    rePassView.setError("Required..");
                    return;
                }
                if(!pass.equals(rePass)) {
                    rePassView.setError("Password not Matches..");
                    return;
                }

                mDialog.setMessage("Processing..");
                mDialog.show();

                mAuth.createUserWithEmailAndPassword(regId, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully registered", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            mDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error in registration", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });
    }
}