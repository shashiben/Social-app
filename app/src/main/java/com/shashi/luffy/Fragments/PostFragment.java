package com.shashi.luffy.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.PostAdapter;
import com.shashi.luffy.Model.PostModel;
import com.shashi.luffy.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostFragment extends Fragment {

    RecyclerView postRecycler;
    CircleImageView pp;
    PostAdapter postAdapter;
    LinearLayout linearLayout;
    List<PostModel> postModelList;
    TextView whatshappening;
    List<String> followingList;
    SwipeRefreshLayout swipeRefreshLayout;


    public PostFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post, container, false);
        swipeRefreshLayout=view.findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPosts();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        pp=view.findViewById(R.id.pp);
        postRecycler=view.findViewById(R.id.post_recycler);
        linearLayout=view.findViewById(R.id.layoutgif);
        postRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        postRecycler.setLayoutManager(linearLayoutManager);
        postModelList=new ArrayList<>();
        postAdapter=new PostAdapter(getActivity(),postModelList);
        postRecycler.setAdapter(postAdapter);
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            checkFollow();
        }
        return view;
    }
    private void checkFollow(){
        followingList=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Follow")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("following");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    followingList.add(ds.getKey());
                }
                followingList.add(FirebaseAuth.getInstance().getCurrentUser().getUid());
                getAllPosts();

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getAllPosts() {
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Posts");

        ref.keepSynced(true);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postModelList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    PostModel postModel=ds.getValue(PostModel.class);
                    for(String id:followingList){
                        assert postModel != null;
                        if(postModel.getUid().equals(id)){
                            postModelList.add(postModel);
                        }
                    }
                }
                if(postAdapter.getItemCount()==0){
                    linearLayout.setVisibility(View.VISIBLE);
                }else{
                    linearLayout.setVisibility(View.GONE);
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(getActivity(),"Error"+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onStart() {

        super.onStart();
    }
}
