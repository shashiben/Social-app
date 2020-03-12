package com.shashi.luffy.Authenication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.shashi.luffy.MainActivity;
import com.shashi.luffy.R;
import com.shuhart.stepview.StepView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfile extends AppCompatActivity {
    private int currentStep = 0;
    LinearLayout layout1,layout2,layout3;
    StepView stepView;
    AlertDialog profile_dialog;
    int year,dayofMonth,month;
    DatePickerDialog datePicker;
    private FirebaseAuth mAuth;
    RadioGroup rg;
    CircleImageView profilepic;
    private Uri postImageUri = null;

    EditText username,fullname,phoneno,location;
    Button first,second,last;
    RadioButton rOption;
    ImageButton datepick;
    TextView dob;
    String gen,uid;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mAuth=FirebaseAuth.getInstance();
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        profilepic=findViewById(R.id.profilepicshashi);
        username=findViewById(R.id.usernameshashi);
        fullname=findViewById(R.id.fullnameshashi);
        first=findViewById(R.id.submit1);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        phoneno=findViewById(R.id.phonenumbershashi);
        location=findViewById(R.id.locationshashi);
        datepick=findViewById(R.id.datepickershashi);
        dob=findViewById(R.id.dobshashi);
        second=findViewById(R.id.submit2);
        last=findViewById(R.id.submit3);
        rg=findViewById(R.id.radiogroup);
        databaseReference= FirebaseDatabase.getInstance().getReference("Users");

        stepView = findViewById(R.id.step_view);
        stepView.setStepsNumber(3);
        stepView.go(0, true);
        layout1.setVisibility(View.VISIBLE);
        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(UserProfile.this);
            }
        });
        datepick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                year=cal.get(Calendar.YEAR);
                month=cal.get(Calendar.MONTH);
                dayofMonth=cal.get(Calendar.DAY_OF_MONTH);
                datePicker=new DatePickerDialog(UserProfile.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dob.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,dayofMonth);
                datePicker.show();
            }
        });
        first.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(username.getText().toString()) && postImageUri!=null){
                    if (currentStep < stepView.getStepCount() - 1) {
                        currentStep++;
                        stepView.go(currentStep, true);
                    } else {
                        stepView.done(true);
                    }
                }else if(TextUtils.isEmpty(username.getText().toString())){
                    username.setError("Enter Username");
                    username.setFocusable(true);
                }else if(postImageUri==null){
                    Toast.makeText(UserProfile.this,"Upload Profile Pic",Toast.LENGTH_SHORT).show();
                }
                databaseReference.child(uid).child("name").setValue(username.getText().toString());
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }
        });
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rOption=rg.findViewById(checkedId);
                switch (checkedId){
                    case R.id.radiomale:
                        gen="Male";
                        //databaseReference.child(uid).child("gender").setValue(gen);
                        break;
                    case R.id.radiofemale:
                        gen="Female";
                        //databaseReference.child(uid).child("gender").setValue(gen);
                        break;
                    case R.id.radioothers:
                        gen="Others";
                        //databaseReference.child(uid).child("gender").setValue(gen);
                        break;
                }
            }
        });
        second.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(fullname.getText().toString()) && !TextUtils.isEmpty(phoneno.getText().toString()) && !TextUtils.isEmpty(location.getText().toString()) && !dob.getText().
                        equals("Enter Date of Birth")){
                    if (currentStep < stepView.getStepCount() - 1) {
                        currentStep++;
                        stepView.go(currentStep, true);
                    } else {
                        stepView.done(true);
                    }
                    databaseReference.child(uid).child("fullname").setValue(fullname.getText().toString());
                    databaseReference.child(uid).child("gender").setValue(gen);
                    Log.d("Selected Gender","Gender is"+gen);
                    databaseReference.child(uid).child("phone").setValue(phoneno.getText().toString());
                    databaseReference.child(uid).child("location").setValue(location.getText().toString());
                    databaseReference.child(uid).child("dob").setValue(dob.getText().toString());
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(UserProfile.this,"Enter all details",Toast.LENGTH_SHORT).show();
                }
            }
        });
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentStep < stepView.getStepCount() - 1) {
                    currentStep++;
                    stepView.go(currentStep, true);
                } else {
                    stepView.done(true);
                }
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout= inflater.inflate(R.layout.processing_dialog,null);
                AlertDialog.Builder show = new AlertDialog.Builder(UserProfile.this);
                show.setView(alertLayout);
                show.setCancelable(false);
                profile_dialog = show.create();
                profile_dialog.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        profile_dialog.dismiss();
                        Intent intent=new Intent(UserProfile.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                },3000);
            }
        });
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(Uri uri) {
        if(uri!=null && FirebaseAuth.getInstance().getCurrentUser()!=null){
            String filePathAndName="Profile_pics/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+"."+getFileExtension(uri);
            StorageReference ref= FirebaseStorage.getInstance().getReference(filePathAndName);
            ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    String downloadUri=uriTask.getResult().toString();
                    if(uriTask.isSuccessful()){
                        String k=FirebaseAuth.getInstance().getCurrentUser().getUid();
                        FirebaseDatabase.getInstance().getReference("Users").child(k).child("image").setValue(downloadUri);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }else{
            Toast.makeText(UserProfile.this,"No Image Selected",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                profilepic.setImageURI(postImageUri);
                uploadImage(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(UserProfile.this,""+ error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }
    }
}

