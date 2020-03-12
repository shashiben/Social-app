package com.shashi.luffy.Fragments.MessageTabs;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.OnlinePeopleAdapter;
import com.shashi.luffy.Model.UserModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Onlinepeople extends Fragment {

    RecyclerView recyclerView;
    OnlinePeopleAdapter adapter;
    List<UserModel> modelList;
    List<String> idList,idList2;
    String id;

    public Onlinepeople() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_onlinepeople, container, false);
        recyclerView=view.findViewById(R.id.online_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        idList=new ArrayList<>();
        idList2=new ArrayList<>();
        modelList=new ArrayList<>();
        adapter=new OnlinePeopleAdapter(getActivity(),modelList);
        recyclerView.setAdapter(adapter);
        getAll();
        return view;
    }
    private void getAll() {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.keepSynced(true);
        db.child("following").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    idList.add(ds.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        db.child("followers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                idList2.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    idList2.add(ds.getKey());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        for (String x : idList2){
            if (!idList.contains(x))
                idList.add(x);
        }
        showUsers();
    }

    private void showUsers() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                modelList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    UserModel userModel=ds.getValue(UserModel.class);
                    for(String id:idList){
                        assert userModel != null;
                        if(userModel.getUid().equals(id) && userModel.getOnlineStatus().equals("online")){
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

    @Override
    public void onStart() {
        super.onStart();
    }
}
