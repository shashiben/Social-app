package com.shashi.luffy.Profile_Options.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.StoryAdapter;
import com.shashi.luffy.Model.Story;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatusFragment extends Fragment {
    private RecyclerView recyclerViewStory;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;
    private List<String> followingList,hmmm;
    public StatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_status, container, false);
        recyclerViewStory=view.findViewById(R.id.recycler_story);
        recyclerViewStory.setLayoutManager(new GridLayoutManager(getActivity(),6));
        storyList=new ArrayList<>();
        storyAdapter=new StoryAdapter(getActivity(),storyList);
        recyclerViewStory.setAdapter(storyAdapter);
        checkFollow();
        return view;
    }
    private void checkFollow(){
        hmmm= new ArrayList<>();
        followingList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                hmmm.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    followingList.add(ds.getKey());
                    hmmm.add(ds.getKey());
                }
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                readStory();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void readStory(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timecurrent=System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("","", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid(),0,0));
                for(String id:hmmm){
                    int countStory=0;
                    Story story=null;
                    for(DataSnapshot ds:dataSnapshot.child(id).getChildren()){
                        story=ds.getValue(Story.class);
                        assert story != null;
                        if(timecurrent>story.getTimestart() && timecurrent<story.getTimeend()){
                            countStory++;

                        }
                    }
                    if(countStory>0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
