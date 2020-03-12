package com.shashi.luffy.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.TablayoutAdapter;
import com.shashi.luffy.Profile_Options.fragments.AboutFragment;
import com.shashi.luffy.Profile_Options.fragments.FollowersFragment;
import com.shashi.luffy.Profile_Options.fragments.FollowingFragment;
import com.shashi.luffy.Profile_Options.fragments.StatusFragment;
import com.shashi.luffy.Profile_Options.fragments.UploadFragment;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private CircleImageView pp;
    TextView name,location;
    TabLayout tab;
    FirebaseAuth mAuth;
    DatabaseReference db;
    String uid;
    ViewPager viewPager;
    TextView postno,followingno,followersno;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_profile, container, false);
        pp=view.findViewById(R.id.pp);
        name=view.findViewById(R.id.ppname);
        location=view.findViewById(R.id.pplocation);
        uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        tab=view.findViewById(R.id.pptab);
        viewPager=view.findViewById(R.id.ppview);
        postno=view.findViewById(R.id.postIv);
        followersno=view.findViewById(R.id.followersIv);
        followingno=view.findViewById(R.id.followingIv);
        mAuth=FirebaseAuth.getInstance();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users");
        ref.keepSynced(true);
        Query query=ref.orderByChild("uid").equalTo(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    String image= Objects.requireNonNull(ds.child("image").getValue()).toString();
                    String uname= Objects.requireNonNull(ds.child("name").getValue()).toString();
                    name.setText(uname);
                    Picasso.get().load(image).into(pp);
                    String l= Objects.requireNonNull(ds.child("location").getValue()).toString();
                    location.setText(l);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        assert getFragmentManager() != null;
        TablayoutAdapter tablayoutAdapter=new TablayoutAdapter(getFragmentManager());
        tablayoutAdapter.add(new StatusFragment(),"Status");
        tablayoutAdapter.add(new UploadFragment(),"Uploads");
        tablayoutAdapter.add(new FollowersFragment(),"Followers");
        tablayoutAdapter.add(new FollowingFragment(),"Following");
        tablayoutAdapter.add(new AboutFragment(),"About");
        viewPager.setAdapter(tablayoutAdapter);
        tab.setupWithViewPager(viewPager);
        getFollowers();
        getFollowing();
        getnoPost();
        return view;
    }
    private void getnoPost(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("PostNumbers").child(uid);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postno.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getFollowers() {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow").child(uid).child("followers");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followersno.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void  getFollowing(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow").child(uid)
                .child("following");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingno.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
