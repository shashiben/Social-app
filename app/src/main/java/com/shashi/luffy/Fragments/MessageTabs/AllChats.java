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
import com.shashi.luffy.Adapters.RecentChatAdapter;
import com.shashi.luffy.Model.RecentChatModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllChats extends Fragment {

    private List<RecentChatModel> userModelsList;
    private RecyclerView recyclerView;
    private List<String> idList;
    String id;
    private RecentChatAdapter chatUsersAdapter;

    public AllChats() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_all_chats, container, false);
        recyclerView=view.findViewById(R.id.all_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        userModelsList= new ArrayList<>();
        idList=new ArrayList<>();
        getAll();
        return view;
    }
    private void getAll() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("ChatList").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        ref.keepSynced(true);
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
        reference.keepSynced(true);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userModelsList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    RecentChatModel userModel=ds.getValue(RecentChatModel.class);
                    for(String id:idList){
                        assert userModel != null;
                        if(userModel.getUid().equals(id)){
                            userModelsList.add(userModel);
                        }

                    }
                }
                chatUsersAdapter=new RecentChatAdapter(getActivity(),userModelsList);
                recyclerView.setAdapter(chatUsersAdapter);
                chatUsersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
