package com.shashi.luffy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddPostActivity extends AppCompatActivity {
    private static final String TAG = "FCM_RESPONSE";
    private ImageView newPostImage;
    private EditText newPostDesc;
    private TextView newPostBtn;
    private Uri postImageUri = null;
    String email,dp;
    String np;
    Toolbar toolbar;
    private FirebaseAuth firebaseAuth;
    private String current_user_id,current_name;
    private DatabaseReference userDbref;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        firebaseAuth = FirebaseAuth.getInstance();
        email = firebaseAuth.getCurrentUser().getEmail();
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        userDbref= FirebaseDatabase.getInstance().getReference("Users");
        Query query=userDbref.orderByChild("email").equalTo(email);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    current_name=""+ds.child("name").getValue();
                    email=""+ds.child("email").getValue();
                    dp=""+ds.child("image").getValue();
                    np=""+ds.child("noofposts").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        pd=new ProgressDialog(AddPostActivity.this);
        newPostImage = findViewById(R.id.postimage);
        newPostDesc = findViewById(R.id.desc);
        newPostBtn = findViewById(R.id.post);
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512, 512)
                        .setAspectRatio(1, 1)
                        .start(AddPostActivity.this);

            }
        });
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String desc=newPostDesc.getText().toString();
                if(TextUtils.isEmpty(desc)){
                    Toast.makeText(AddPostActivity.this,"Please Enter Description",Toast.LENGTH_LONG).show();
                    return;
                }
                if(postImageUri==null){
                    uploadData(desc,"noImage");
                }else{
                    uploadData(desc,String.valueOf(postImageUri));
                }
            }
        });
    }
    private void prepareNotification(String pId,String title,String description,String notificationType,String notificationTopic){
        String NOTIFICATION_TOPIC="/topics/"+notificationTopic;
        String NOTIFICATION_TITLE=title;
        String NOTIFICATION_MESSAGE=description;
        String NOTIFICATION_TYPE=notificationType;
        JSONObject notificationJo=new JSONObject();
        JSONObject notificationBodyJo=new JSONObject();
        try{
            notificationBodyJo.put("notificationType",NOTIFICATION_TYPE);
            notificationBodyJo.put("sender",current_user_id);
            notificationBodyJo.put("pId",pId);
            notificationBodyJo.put("pTitle",NOTIFICATION_TITLE);
            notificationBodyJo.put("pDescription",NOTIFICATION_MESSAGE);
            notificationJo.put("to",NOTIFICATION_TOPIC);
            notificationJo.put("data",notificationBodyJo);

        }catch (Exception e){
            Toast.makeText(AddPostActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        sendPostification(notificationJo);

    }

    private void sendPostification(JSONObject notificationJo) {
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notificationJo
                , new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG,"on Response"+response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(AddPostActivity.this,error.toString(),Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String ,String > headers=new HashMap<>();
                headers.put("content-Type","application/json");
                headers.put("Authorization","key=AAAA9xTb0OQ:APA91bHnaEa6EFahc8RNv94NW2qjciqsEkbsFRIP3C1zIECHLbaYZjA5qS_hh_o1vUiHBfyq259hres5iRf6GmOEZ37sNDnkJZvR9N5X845vGKkMA1tmb_csZBtxEQkZQ_abKdCJun8N");
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(jsonObjectRequest);
    }

    private void uploadData(final String desc, final String uri) {
        pd.setTitle("Posting");
        pd.setMessage("Please Wait..");
        pd.show();
        final String timeStamp=String.valueOf(System.currentTimeMillis());
        String filePathAndName="Posts/"+"post_"+timeStamp;
        if(!uri.equals("noImage")){
            StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putFile(Uri.parse(uri)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTask.isSuccessful());
                    String downloadUri=uriTask.getResult().toString();
                    if(uriTask.isSuccessful()){
                        HashMap<Object,String > hashMap=new HashMap<>();
                        hashMap.put("uid",current_user_id);
                        hashMap.put("name",current_name);
                        hashMap.put("timestamp",timeStamp);
                        hashMap.put("email",email);
                        hashMap.put("description",desc);
                        hashMap.put("post_image",downloadUri);
                        hashMap.put("pId",timeStamp);
                        hashMap.put("dp",dp);
                        hashMap.put("pLikes","0");
                        hashMap.put("pComments","0");
                        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
                        FirebaseDatabase.getInstance().getReference().child("PostNumbers").child(current_user_id)
                                .child(timeStamp).setValue(timeStamp);
                        reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.dismiss();
                                np=String.valueOf(Integer.parseInt(np)+1);
                                userDbref.child(current_user_id).child("noofposts").setValue(np);
                                Toast.makeText(AddPostActivity.this,"Post was Added",Toast.LENGTH_LONG).show();
                                newPostDesc.setText("");
                                newPostImage.setImageURI(null);
                                postImageUri=null;
                                prepareNotification(""+timeStamp,
                                        ""+current_name+ " added new post",
                                        ""+desc,
                                        "PostNotification",
                                        "POST");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();

                                Toast.makeText(AddPostActivity.this,""+e,Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                }
            });
        }else{
            HashMap<Object,String > hashMap=new HashMap<>();
            hashMap.put("uid",current_user_id);
            hashMap.put("name",current_name);
            hashMap.put("timestamp",timeStamp);
            hashMap.put("email",email);
            hashMap.put("description",desc);
            hashMap.put("post_image","noImage");
            hashMap.put("pId",timeStamp);
            hashMap.put("dp",dp);
            hashMap.put("pComments","0");
            hashMap.put("pLikes","0");
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
            FirebaseDatabase.getInstance().getReference().child("PostNumbers").child(current_user_id)
                    .child(timeStamp).setValue(timeStamp);
            np=String.valueOf(Integer.parseInt(np)+1);
            userDbref.child(current_user_id).child("noofposts").setValue(np);
            reference.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    pd.dismiss();
                    Toast.makeText(AddPostActivity.this,"Post was Added",Toast.LENGTH_SHORT).show();
                    newPostDesc.setText("");
                    newPostImage.setImageURI(null);
                    postImageUri=null;
                    prepareNotification(""+timeStamp,
                            ""+current_name+ " added new post",
                            ""+desc,
                            "PostNotification",
                            "POST");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String error=e.getMessage();
                    pd.dismiss();

                    Toast.makeText(AddPostActivity.this,""+e,Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri = result.getUri();
                newPostImage.setImageURI(postImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(AddPostActivity.this,""+ error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
