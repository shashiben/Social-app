package com.shashi.luffy.Fragments;


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
import com.shashi.luffy.Adapters.NotificationAdapter;
import com.shashi.luffy.Model.NotificationModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {

    private NotificationAdapter notificationAdapter;
    private List<NotificationModel> notificationModelList;
    private RecyclerView recyclerView;
    private FirebaseAuth firebaseAuth;
    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_notification, container, false);
        recyclerView=view.findViewById(R.id.notificationRecyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        firebaseAuth=FirebaseAuth.getInstance();
        recyclerView.setLayoutManager(linearLayoutManager);
        notificationModelList=new ArrayList<>();
        notificationAdapter=new NotificationAdapter(getContext(),notificationModelList);
        recyclerView.setAdapter(notificationAdapter);


        getAllNotification();
        return view;
    }
    private void getAllNotification() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("NotificationActivity").child(firebaseAuth.getCurrentUser().getUid());
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationModelList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    NotificationModel postModel=ds.getValue(NotificationModel.class);
                    notificationModelList.add(postModel);

                }
                Collections.reverse(notificationModelList);
                notificationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"Error"+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

}
