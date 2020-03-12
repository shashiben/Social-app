package com.shashi.luffy.Authenication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashi.luffy.MainActivity;
import com.shashi.luffy.R;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn,loginBtn;
    ProgressDialog pd;
    DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        pd=new ProgressDialog(RegisterActivity.this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mAuth = FirebaseAuth.getInstance();
        pd.setTitle("Registering");

        pd.setMessage("Please Wait..");
        loginBtn=findViewById(R.id.btnLogin);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });

        reg_email_field = findViewById(R.id.editText);
        reg_pass_field = findViewById(R.id.editText2);
        reg_confirm_pass_field = findViewById(R.id.editText3);
        reg_btn = findViewById(R.id.button3);

        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();
                final String confirm_pass = reg_confirm_pass_field.getText().toString();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass)) {
                        if (pass.equals(confirm_pass)) {
                            pd.show();
                            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        pd.dismiss();
                                        FirebaseUser user=mAuth.getCurrentUser();
                                        String uid=user.getUid();
                                        String mail=user.getEmail();
                                        HashMap<String,Object > result=new HashMap<>();
                                        result.put("email",mail);
                                        result.put("uid",uid);
                                        result.put("onlineStatus","online");
                                        result.put("typingTo","noOne");
                                        result.put("image","");
                                        result.put("name","");
                                        result.put("location","");
                                        result.put("gender","");
                                        result.put("dob","");
                                        result.put("status","");
                                        result.put("phone","");
                                        result.put("fullname","");
                                        result.put("noofposts","0");
                                        result.put("password",confirm_pass);
                                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                                        databaseReference=database.getReference("Users");
                                        databaseReference.child(uid).setValue(result);
                                        Intent mainIntent = new Intent(RegisterActivity.this, UserProfile.class);
                                        String type="email";
                                        mainIntent.putExtra("phoneoremail",type);
                                        startActivity(mainIntent);
                                        finish();

                                    } else {
                                        pd.dismiss();
                                        String errorMessage = task.getException().getMessage();
                                        Toast.makeText(RegisterActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {

                            Toast.makeText(RegisterActivity.this, "Confirm Password and Password Field doesn't match.", Toast.LENGTH_LONG).show();
                        }
                    }
                }else{
                    reg_email_field.setError("Invalid Email");
                    reg_email_field.setFocusable(true);
                }
            }
        });
    }

    private void sendToMain() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
