package com.papb.imamfrf.land;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
        auth = FirebaseAuth.getInstance();
        forgotPassword=(TextView)findViewById(R.id.txt_forgot);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this,ResetPasswordActivity.class));
            }
        });
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignInActivity.this, SignUp.class));
            }
        });

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

        if(auth.getCurrentUser() != null){
            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();

        }
    }



}
