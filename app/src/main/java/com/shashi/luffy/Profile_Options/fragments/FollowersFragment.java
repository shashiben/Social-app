package com.shashi.luffy.Profile_Options.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.UserManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.FollowAdapter;
import com.shashi.luffy.Model.FavouriteChatModel;
import com.shashi.luffy.Model.UserModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FollowersFragment extends Fragment {

    RecyclerView recyclerView;
    FollowAdapter adapter;
    List<FavouriteChatModel> modelList;
    List<String > idList;
    public FollowersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_followers, container, false);
        recyclerView=view.findViewById(R.id.followers_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modelList=new ArrayList<>();
        adapter=new FollowAdapter(getActivity(),modelList);
        recyclerView.setAdapter(adapter);
        idList=new ArrayList<>();
        getfollowers();
        return view;
    }
    private void getfollowers(){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Follow").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("followers");
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
                modelList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    FavouriteChatModel userModel=ds.getValue(FavouriteChatModel.class);
                    for(String id:idList){
                        assert userModel != null;
                        if(userModel.getUid().equals(id)){
                            modelList.add(userModel);

                        }

                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
