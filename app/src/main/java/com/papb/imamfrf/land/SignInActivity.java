package com.papb.imamfrf.land;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button signIn;
    private TextView message, daftar, forgotPassword;
    private CheckBox cb_remember;

    SharedPreferences sharedpreferences;
    public static final String mypreference = "com.papb.imamfrf.land";
    public static final String userEmail = "emailKey";

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        username = (EditText)findViewById(R.id.txtusername);
        password= (EditText)findViewById(R.id.txtpassword);
        signIn = (Button)findViewById(R.id.btn_signIn);
        message=(TextView)findViewById(R.id.message);
        daftar=(TextView)findViewById(R.id.daftar);
        cb_remember = findViewById(R.id.cbox_remember);
        auth = FirebaseAuth.getInstance();

        //shared pref
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        if (sharedpreferences != null){
            username.setText(sharedpreferences.getString(userEmail, ""));
        }

        //go to forgot pass
        forgotPassword=(TextView)findViewById(R.id.txt_forgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,ResetPasswordActivity.class));
            }
        });

        //go to register
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUp.class));
            }
        });

        //sign in
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (username.getText().toString().matches("") || password.getText().toString().matches("")){
                    Toast.makeText(SignInActivity.this, "Mohon isi Username/Password!", Toast.LENGTH_SHORT).show();
                }
                else{
                    auth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString()).addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                if (cb_remember.isChecked()){
                                    SharedPreferences.Editor preferencesEditor= sharedpreferences.edit();
                                    preferencesEditor.putString(userEmail, username.getText().toString());
                                    preferencesEditor.clear();
                                    preferencesEditor.apply();
                                }
                                startActivity(new Intent(SignInActivity.this, MainActivity.class));
                                finish();
                            }
                            else {
                                Toast.makeText(SignInActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    });
                }
                //validate(username.getText().toString(), password.getText().toString());


            }
        });

    }



}
