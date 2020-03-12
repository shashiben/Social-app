package com.shashi.luffy.Authenication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.shashi.luffy.MainActivity;
import com.shashi.luffy.R;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn,signupBtn;
    private TextView forgot;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        forgot=findViewById(R.id.textView5);

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverpassword();
            }
        });
        loginEmailText = findViewById(R.id.editText);
        signupBtn=findViewById(R.id.btnSignup);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));

            }
        });
        loginPassText = findViewById(R.id.editText2);
        loginBtn = findViewById(R.id.button3);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd=new ProgressDialog(LoginActivity.this);
                pd.setTitle("Logging in..");
                pd.setMessage("Loading");
                pd.show();
                String loginEmail = loginEmailText.getText().toString();
                final String loginPass = loginPassText.getText().toString();
                if(!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){

                    mAuth.signInWithEmailAndPassword(loginEmail, loginPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){

                                pd.dismiss();
                                sendToMain();
                            } else {
                                pd.dismiss();
                                String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                                Toast.makeText(LoginActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                            }

                        }
                    });

                }
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

    }
    private void sendToMain() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void recoverpassword() {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        LinearLayout linearLayout=new LinearLayout(this);
        final EditText kk=new EditText(this);
        kk.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        kk.setHint("Email");
        linearLayout.addView(kk);
        linearLayout.setPadding(10,10,10,10);
        builder.setView(linearLayout);
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String l=kk.getText().toString().trim();
                beginRecovery(l);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
    private void beginRecovery(String l) {
        mAuth.sendPasswordResetEmail(l).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this,"Email Sent",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(LoginActivity.this,"Failed to Recover",Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LoginActivity.this,"Failed to Recover",Toast.LENGTH_LONG).show();
            }
        });
    }
}
