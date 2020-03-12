package com.shashi.luffy.Profile_Options.fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.PostProfileAdapter;
import com.shashi.luffy.Model.PostModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadFragment extends Fragment {

    RecyclerView recyclerView;
    PostProfileAdapter postProfileAdapter;
    List<PostModel> postModelList;
    private static final String TAG="Hmmmm";
    public UploadFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_upload, container, false);
        recyclerView=view.findViewById(R.id.post_recycler_profile);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        postModelList=new ArrayList<>();
        postProfileAdapter=new PostProfileAdapter(getContext(),postModelList);
        recyclerView.setAdapter(postProfileAdapter);
        getPosts();

        return view;
    }

    private void getPosts() {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("Posts");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postModelList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    PostModel postModel=ds.getValue(PostModel.class);
                    assert postModel != null;
                    if(postModel.getUid().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                    && !postModel.getPost_image().equals("noImage")){
                        postModelList.add(postModel);
                        Log.d(TAG,"Post Added");
                    }
                }
                Collections.reverse(postModelList);
                postProfileAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
