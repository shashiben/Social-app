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
import com.shashi.luffy.Adapters.FavouriteAdapter;
import com.shashi.luffy.Model.FavouriteChatModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Favouriteschats extends Fragment {

    private RecyclerView recyclerView;
    private List<FavouriteChatModel> chatModelList;
    private List<String> idList;
    FavouriteAdapter adapter;
    public Favouriteschats() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favouriteschats, container, false);
        recyclerView=view.findViewById(R.id.fav_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        chatModelList=new ArrayList<>();
        idList=new ArrayList<>();
        adapter=new FavouriteAdapter(getActivity(),chatModelList);
        recyclerView.setAdapter(adapter);
        getID();
        return view;
    }

    private void getID() {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Favourites").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.keepSynced(true);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
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
        getAll();
    }

    private void getAll() {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("Favourites");
        db.keepSynced(true);
        db.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatModelList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    FavouriteChatModel userModel=ds.getValue(FavouriteChatModel.class);
                    for(String id:idList){
                        assert userModel != null;
                        chatModelList.add(userModel);
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
