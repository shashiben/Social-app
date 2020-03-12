package com.shashi.luffy.Profile_Options;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.FollowersActivity;
import com.shashi.luffy.MainActivity;
import com.shashi.luffy.Model.Story;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public  class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {
    int counter=0;
    long pressTime=0L;
    long limit=500L;
    StoriesProgressView storiesProgressView;
    ImageView image,story_photo,delete,views;
    TextView story_username,status_no_count;
    List<String > images;
    LinearLayout r_seen;
    List<String > storyids;
    String userid;
    private View.OnTouchListener onTouchListener=new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime=System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now=System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit <now-pressTime;
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        delete=findViewById(R.id.statusDelete);
        views=findViewById(R.id.status_no_of_views);
        r_seen=findViewById(R.id.r_seen);
        status_no_count=findViewById(R.id.count_views);
        storiesProgressView=findViewById(R.id.stories);
        story_photo=findViewById(R.id.story_photo);
        image=findViewById(R.id.image);
        story_username=findViewById(R.id.story_username);
        final Intent i=getIntent();
        r_seen.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);

        userid=i.getStringExtra("kim");
        if(userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            r_seen.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);

        }
        userInfo(userid);
        getStories(userid);
        View reverse=findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);
        View skip=findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        skip.setOnTouchListener(onTouchListener);
        r_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(StoryActivity.this, FollowersActivity.class);
                intent.putExtra("id",userid);
                intent.putExtra("storyid",storyids.get(counter));
                intent.putExtra("title","views");
                startActivity(intent);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(StoryActivity.this);
                builder.setMessage("Are you sure want to delete your status?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Story")
                                .child(userid).child(storyids.get(counter));

                        databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(StoryActivity.this,"Status Deleted!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(StoryActivity.this, MainActivity.class));
                                    finish();
                                }
                            }
                        });
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
        });
    }

    @Override
    public void onNext() {
        Picasso.get().load(images.get(++counter)).into(story_photo);
        addView(storyids.get(counter));
        seenNumber(storyids.get(counter));
    }


    @Override
    public void onPrev() {
        if(counter-1<0) return;
        Picasso.get().load(images.get(--counter)).into(story_photo);
        seenNumber(storyids.get(counter));

    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    private void getStories(String userid){
        images=new ArrayList<>();
        storyids=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                images.clear();
                storyids.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Story story=ds.getValue(Story.class);
                    long timecurrent=System.currentTimeMillis();
                    assert story != null;
                    if(timecurrent>story.getTimestart() && timecurrent<story.getTimeend()){
                        images.add(story.getImageurl());
                        storyids.add(story.getStoryid());
                    }
                }
                Picasso.get().load(images.get(counter)).into(story_photo);
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories();
                addView(storyids.get(counter));
                seenNumber(storyids.get(counter));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void userInfo(String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users");
        Query query=reference.orderByChild("uid").equalTo(userid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String naam=""+ ds.child("name").getValue().toString();
                    story_username.setText(naam);
                    String im=""+ ds.child("image").getValue().toString();
                    try {
                        Picasso.get().load(im).into(image);

                    }catch (Exception e){
                        Picasso.get().load(R.drawable.luffy).into(image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void addView(String  storyid){
        if(!userid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            FirebaseDatabase.getInstance().getReference("Story").child(userid).child(storyid).child("views")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);
        }


    }
    private void seenNumber(String storyid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story").child(userid).child(storyid).child("views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                status_no_count.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
