package com.shashi.luffy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.UserAdapter;
import com.shashi.luffy.Model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {
    String id,title;
    List<String> idList;
    RecyclerView recyclerView;
    UserAdapter userAdapter;
    List<UserModel> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        title=intent.getStringExtra("title");
        ActionBar actionBar=getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList=new ArrayList<>();
        userAdapter=new UserAdapter(this,userList);
        recyclerView.setAdapter(userAdapter);
        idList=new ArrayList<>();

        switch (title){
            case "Likes":
                getLikes();
                actionBar.setTitle("Likes ");

                break;
            case "following":
                actionBar.setTitle("Following List");

                getfollowing();
                break;
            case "followers":
                actionBar.setTitle("Followers List");

                getfollowers();
                break;
            case "views":
                actionBar.setTitle("Views");
                getViews();
                break;
        }
    }

    private void getViews(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Story").child(id)
                .child(getIntent().getStringExtra("storyid")).child("views");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    idList.add(ds.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getLikes() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Likes").child(id);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    idList.add(ds.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getfollowers(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Follow").child(id).child("followers");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    idList.add(ds.getKey());

                }
                showUsers();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void getfollowing(){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Follow").child(id).child("following");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    idList.add(ds.getKey());

                }
                showUsers();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void showUsers(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    UserModel userModel=ds.getValue(UserModel.class);
                    for(String id:idList){
                        assert userModel != null;
                        if(userModel.getUid().equals(id)){
                            userList.add(userModel);

                        }

                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
