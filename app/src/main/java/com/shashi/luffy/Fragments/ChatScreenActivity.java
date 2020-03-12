package com.shashi.luffy.Fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.shashi.luffy.Adapters.ChatAdapter;
import com.shashi.luffy.Authenication.LoginActivity;
import com.shashi.luffy.Cryptography.AES;
import com.shashi.luffy.Model.ChatModel;
import com.shashi.luffy.Model.RecentChatModel;
import com.shashi.luffy.Notification.Data;
import com.shashi.luffy.Notification.Sender;
import com.shashi.luffy.Notification.Token;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ChatScreenActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recyclerView;
    ImageView profileIv;
    TextView nameTv,userStatusTv;
    EditText messageet;
    ImageButton sendBtn,attachBtn;
    DatabaseReference userDbref;
    FirebaseAuth firebaseAuth;
    String hisuid;
    String myuid;
    private boolean notify=false;
    String hisImage;
    private RequestQueue requestQueue;
    Uri image_uri=null;
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ChatModel> chatList;
    ChatAdapter chatAdapter;
    String secretKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        attachBtn=findViewById(R.id.attachBtn);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView=findViewById(R.id.chatRecyclerView);
        recyclerView.setHasFixedSize(true);
        profileIv=findViewById(R.id.profile);
        nameTv=findViewById(R.id.nameChat);
        requestQueue= Volley.newRequestQueue(getApplicationContext());
        userStatusTv=findViewById(R.id.onlineChat);
        messageet=findViewById(R.id.messageChat);
        sendBtn=findViewById(R.id.chatSendBtn);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(ChatScreenActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        firebaseAuth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        hisuid=intent.getStringExtra("hisUid");
        userDbref= FirebaseDatabase.getInstance().getReference("Users");

        Query userquery=userDbref.orderByChild("uid").equalTo(hisuid);
        userquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String name=""+ds.child("name").getValue();
                    hisImage=""+ds.child("image").getValue();
                    String typingStatus=""+ds.child("typingTo").getValue();
                    String onlineStatus=""+ds.child("onlineStatus").getValue();
                    if(typingStatus.equals(myuid)){
                        userStatusTv.setText("typing...");
                    }else{
                        if(onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        }else{
                            Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                            userStatusTv.setText("Last Seen:"+dateTime);
                        }
                    }
                    nameTv.setText(name);
                    try{
                        Picasso.get().load(hisImage).placeholder(R.drawable.luffy).into(profileIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.luffy).into(profileIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                notify=true;
                final String message=messageet.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatScreenActivity.this,"Can't send Empty Message",Toast.LENGTH_LONG).show();
                }else{
                    sendMessage(message);

                }
                messageet.setText("");
                String msg=message;
                DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
                database.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String kkuid= Objects.requireNonNull(dataSnapshot.child("uid").getValue()).toString();
                        String kkname= Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString();
                        if(notify){
                            senNotification(hisuid,kkname,message);
                        }
                        notify=false;

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String options[]={"\uD83D\uDCF7Image","\uD83D\uDDCEDocument","\uD83D\uDCF9Video","\uD83C\uDFA7Audio","\uD83D\uDC64User Contact"};
                AlertDialog.Builder builder=new AlertDialog.Builder(ChatScreenActivity.this);
                builder.setTitle("Choose Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0){
                            CropImage.activity()
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .setMinCropResultSize(512, 512)
                                    .setAspectRatio(1, 1)
                                    .start(ChatScreenActivity.this);
                        }else{
                            Toast.makeText(ChatScreenActivity.this,"Comming soon...",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.create().show();

            }
        });
        messageet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    checkTypingStatus("noOne");
                }else{
                    checkTypingStatus(hisuid);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        readMessages();
        seenMessages();

    }
    private void senNotification(final String hisuid,final String name,final String message){
        DatabaseReference allTokens=FirebaseDatabase.getInstance().getReference("Tokens");
        Query query=allTokens.orderByKey().equalTo(hisuid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Token token=ds.getValue(Token.class);
                    Data data=new Data(myuid
                            ,name+":"+message
                            ,"New Message"
                            ,""+hisuid,
                            "ChatNotification"
                            ,
                            R.drawable.luffy);
                    assert token != null;
                    Sender sender=new Sender(data,token.getToken());
                    try{
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("JSON_RESPONSE","onResponse:"+response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String ,String > headers=new HashMap<>();
                                headers.put("content-Type","application/json");
                                headers.put("Authorization",
                                        "key=AAAA9xTb0OQ:APA91bHnaEa6EFahc8RNv94NW2qjciqsEkbsFRIP3C1zIECHLbaYZjA5qS_hh_o1vUiHBfyq259hres5iRf6GmOEZ37sNDnkJZvR9N5X845vGKkMA1tmb_csZBtxEQkZQ_abKdCJun8N");
                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void seenMessages() {
        userRefForSeen=FirebaseDatabase.getInstance().getReference("Chats");
        seenListener=userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ChatModel chat=ds.getValue(ChatModel.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myuid) && chat.getSender().equals(hisuid)){
                        HashMap<String ,Object> hasSeen=new HashMap<>();
                        hasSeen.put("isSeen",true);
                        ds.getRef().updateChildren(hasSeen);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readMessages() {
        chatList=new ArrayList<>();
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Chats");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ChatModel chat=ds.getValue(ChatModel.class);
                    assert chat != null;
                    if(!chat.getReceiver().equals(chat.getSender())){
                        if(chat.getReceiver().equals(myuid) && chat.getSender().equals(hisuid)||
                                chat.getReceiver().equals(hisuid) && chat.getSender().equals(myuid)){
                            chatList.add(chat);
                        }
                    }

                    chatAdapter=new ChatAdapter(ChatScreenActivity.this,chatList,hisImage);
                    recyclerView.setAdapter(chatAdapter);
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(final String message) {
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        String timestamp= String.valueOf(System.currentTimeMillis());
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",myuid);
        hashMap.put("receiver",hisuid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isseen",false);
        hashMap.put("type","text");
        databaseReference.child("Chats").push().setValue(hashMap);
        String msg=message;
        final DatabaseReference database=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RecentChatModel userModel=dataSnapshot.getValue(RecentChatModel.class);
                if(notify){
                    assert userModel != null;
                    senNotification(hisuid,userModel.getName(),message);
                }
                notify=false;
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(hisuid).child("message").setValue(msg);
        FirebaseDatabase.getInstance().getReference("ChatList").child(hisuid).child(myuid).child("message").setValue(msg);
        FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(hisuid).child("type").setValue("text");
        FirebaseDatabase.getInstance().getReference("ChatList").child(hisuid).child(myuid).child("type").setValue("text");


    }
    private void sendImageMessage(Uri image_uri) throws IOException {
        notify=true;
        final ProgressDialog pd=new ProgressDialog(ChatScreenActivity.this);
        pd.setMessage("Sending image..");
        pd.show();
        final String timeStamp=""+System.currentTimeMillis();
        String filePathAndName="ChatImages/"+"message_"+timeStamp;
        Bitmap bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte[] data=baos.toByteArray();
        StorageReference ref= FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        pd.dismiss();
                        Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        String downloadUri=uriTask.getResult().toString();
                        if(uriTask.isSuccessful()){
                            DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
                            HashMap<String ,Object> hashMap=new HashMap<>();
                            hashMap.put("sender",myuid);
                            hashMap.put("receiver",hisuid);
                            hashMap.put("message",downloadUri);
                            hashMap.put("timestamp",timeStamp);
                            hashMap.put("type","image");
                            hashMap.put("isseen",false);
                            FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(hisuid).child("message").setValue(downloadUri);
                            FirebaseDatabase.getInstance().getReference("ChatList").child(hisuid).child(myuid).child("message").setValue(downloadUri);
                            FirebaseDatabase.getInstance().getReference("ChatList").child(myuid).child(hisuid).child("type").setValue("image");
                            FirebaseDatabase.getInstance().getReference("ChatList").child(hisuid).child(myuid).child("type").setValue("image");
                            databaseReference.child("Chats").push().setValue(hashMap);
                            DatabaseReference notificationReferece=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
                            notificationReferece.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    RecentChatModel userModel=dataSnapshot.getValue(RecentChatModel.class);
                                    if(notify){
                                        senNotification(hisuid,userModel.getName(),"sent you a photo");
                                    }
                                    notify=false;
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(ChatScreenActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void checkUserStatus() {
        FirebaseUser fuser=firebaseAuth.getCurrentUser();
        if (fuser == null) {
            startActivity(new Intent(ChatScreenActivity.this, LoginActivity.class));
            finish();
        }else{
            myuid=fuser.getUid();
        }
    }
    private void checkOnlineStatus(String status){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("onlineStatus",status);
        dbRef.updateChildren(hashMap);
    }
    private void checkTypingStatus(String typing){
        DatabaseReference dbRef=FirebaseDatabase.getInstance().getReference("Users").child(myuid);
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("typingTo",typing);
        dbRef.updateChildren(hashMap);
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        checkTypingStatus("noOne");
        String timestamp=String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        userRefForSeen.removeEventListener(seenListener);
    }
    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                assert result != null;
                image_uri = result.getUri();
                try {
                    sendImageMessage(image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                assert result != null;
                Exception error = result.getError();
                Toast.makeText(ChatScreenActivity.this,""+ error.getMessage(),Toast.LENGTH_LONG).show();
            }
        }

    }
        @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.chat_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemid=item.getItemId();
        switch(itemid){
            case R.id.markFav:
                DatabaseReference db=FirebaseDatabase.getInstance().getReference("Favourites").child(myuid);
                HashMap<String ,String > hashMap=new HashMap<>();
                hashMap.put("favourite","true");
                hashMap.put("uid",hisuid);
                db.child(hisuid).setValue(hashMap);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}

