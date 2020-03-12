package com.shashi.luffy.Fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.CommentAdapter;
import com.shashi.luffy.Authenication.LoginActivity;
import com.shashi.luffy.Model.CommentModel;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class CommentActivity extends AppCompatActivity {

    ProgressDialog pd;
    String myUid,myEmail,myName,myDp,postId,pLikes,hisDp,hisName;
    ImageView uPictureIv,pImageIv;
    TextView uNameTv,pTimeTv,pDescTv,pLikesTv,pCommentsTv;
    Button likeBtn,shareBtn;
    LinearLayout profileLayout;
    RecyclerView recyclerView;
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;
    FirebaseAuth firebaseAuth;
    List<CommentModel> commentList;
    CommentAdapter commentAdapter;

    boolean mProcessComment=false;
    boolean mProcessLike=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        Intent intent=getIntent();
        postId=intent.getStringExtra("postId");
        firebaseAuth=FirebaseAuth.getInstance();
        myUid= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        recyclerView=findViewById(R.id.recyclerCommentView);
        uPictureIv=findViewById(R.id.upictureIv);
        pImageIv=findViewById(R.id.pImageIv);
        uNameTv=findViewById(R.id.uNameTv);
        pTimeTv=findViewById(R.id.pTimeTv);
        pDescTv=findViewById(R.id.pDescriptionTv);
        pLikesTv=findViewById(R.id.pLikesTv);
        pCommentsTv=findViewById(R.id.pCommentsTv);
        likeBtn=findViewById(R.id.likeBtn);
        shareBtn=findViewById(R.id.shareBtn);
        profileLayout=findViewById(R.id.profileLayout);
        commentEt=findViewById(R.id.commentEt);
        sendBtn=findViewById(R.id.sendBtn);
        cAvatarIv=findViewById(R.id.cAvatarIv);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(getApplicationContext(),commentList,myUid,postId);
        recyclerView.setAdapter(commentAdapter);
        loadPostInfo();
        checkUserStatus();
        loadUserInfo();
        setLikes();
        loadComments();
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComments();
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pDescription=pDescTv.getText().toString();
                BitmapDrawable bitmapDrawable=(BitmapDrawable)pImageIv.getDrawable();
                if(bitmapDrawable==null){
                    shareText(pDescription);
                }else{
                    Bitmap bitmap=bitmapDrawable.getBitmap();
                    sharePost(bitmap,pDescription);
                }
            }
        });
    }

    private void shareText(String pDescription) {
        String s=pDescription;
        Intent sIntent=new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT,s);
        startActivity(Intent.createChooser(sIntent,"Share Via"));
    }
    private void sharePost(Bitmap bitmap, String pDescription) {
        String s=pDescription;
        Uri uri=saveImage(bitmap);
        Intent sIntent=new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sIntent.putExtra(Intent.EXTRA_TEXT,s);
        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        sIntent.setType("image/png");
        startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    private Uri saveImage(Bitmap bitmap) {
        File imageFolder=new File(getCacheDir(),"images");
        Uri uri=null;
        try{
            imageFolder.mkdirs();
            File file=new File(imageFolder,"share_image.png");
            FileOutputStream stream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri= FileProvider.getUriForFile(this,"android.support.FILE_PROVIDER_PATHS",file);

        }catch(Exception e){
            Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    private void loadComments() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    CommentModel commentModel=ds.getValue(CommentModel.class);
                    commentList.add(commentModel);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setLikes() {
        final DatabaseReference likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postId).hasChild(myUid)){
                    likeBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like_blue,0,0,0);
                    likeBtn.setText("Liked");
                }else {
                    likeBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
                    likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        mProcessLike=true;
        final DatabaseReference likeRef=FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(mProcessLike){
                    if(dataSnapshot.child(postId).hasChild(myUid)){
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)-1));
                        likeRef.child(postId).child(myUid).removeValue();
                        mProcessLike=false;

                    }else{
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likeRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike=false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void postComments() {
        pd=new ProgressDialog(CommentActivity.this);
        pd.setMessage("Adding Comment..");
        final String comment=commentEt.getText().toString().trim();
        if(TextUtils.isEmpty(comment)){
            Toast.makeText(CommentActivity.this,"Empty Comment Cant be Posted",Toast.LENGTH_LONG).show();
            return;
        }else{

            addNotiffication();
            String timeStamp=String.valueOf(System.currentTimeMillis());
            DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(postId).child("Comments");
            HashMap<String ,Object> hashMap=new HashMap<>();
            hashMap.put("cId",timeStamp);
            hashMap.put("comment",comment);
            hashMap.put("timestamp",timeStamp);
            hashMap.put("uid",myUid);
            hashMap.put("uEmail",myEmail);
            hashMap.put("uDp",myDp);
            hashMap.put("uName",myName);
            ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CommentActivity.this,"Comment Added..",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                    commentEt.setText("");
                    updateCommentCount();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(CommentActivity.this,"Error Occured:"+e.getMessage(),Toast.LENGTH_LONG).show();
                    commentEt.setText("");
                }
            });

        }
    }
    private void addNotiffication(){
        Intent i=getIntent();
        String pppp=i.getStringExtra("frienduid");
        if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(pppp) && pppp!=null){
            assert pppp != null;
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("NotificationActivity").child(pppp);
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("text","commented: "+commentEt.getText().toString().trim());
            hashMap.put("postid",postId);
            hashMap.put("time",String.valueOf(System.currentTimeMillis()));
            hashMap.put("ispost",false);
            reference.push().setValue(hashMap);
        }
    }
    private void updateCommentCount() {
        mProcessComment=true;
        final DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String comments=""+dataSnapshot.child("pComments").getValue();
                int newCommentBal=Integer.parseInt(comments)+1;
                ref.child("pComments").setValue(""+newCommentBal);
                mProcessComment=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserInfo() {
        Query myRef=FirebaseDatabase.getInstance().getReference("Users");
        myRef.orderByChild("uid").equalTo(myUid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    myName=""+ds.child("name").getValue();
                    myDp=""+ds.child("image").getValue();
                    try {
                        Picasso.get().load(myDp).into(cAvatarIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.luffy).into(cAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");
        Query query=ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String pDescr=""+ds.child("description").getValue();
                    String pTimeStamp=""+ds.child("timestamp").getValue();
                    pLikes=""+ds.child("pLikes").getValue();
                    String pImage=""+ds.child("post_image").getValue();
                    hisDp=""+ds.child("dp").getValue();
                    String uid=""+ds.child("uid").getValue();
                    String uEmail=""+ds.child("email").getValue();
                    String commentCount=""+ds.child("pComments").getValue();
                    hisName=""+ds.child("name").getValue();
                    Calendar cal=Calendar.getInstance(Locale.ENGLISH);
                    cal.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa",cal).toString();
                    pDescTv.setText(pDescr);
                    pLikesTv.setText(pLikes+" Likes");
                    pTimeTv.setText(pTime);
                    uNameTv.setText(hisName);
                    pCommentsTv.setText(commentCount+" Comments");
                    if(pImage.equals("noImage")){
                        pImageIv.setVisibility(View.GONE);
                    }else{
                        pImageIv.setVisibility(View.VISIBLE);
                        try{
                            Picasso.get().load(pImage).into(pImageIv);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.luffy).into(uPictureIv);
                        }
                    }
                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.luffy).into(uPictureIv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.luffy).into(uPictureIv);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void  checkUserStatus(){
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if(user!=null){
            myEmail=user.getEmail();
            myUid=user.getUid();
        }else{
            startActivity(new Intent(CommentActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }


}
