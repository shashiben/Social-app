package com.shashi.luffy.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Adapters.ViewPageAdapter;
import com.shashi.luffy.Fragments.MessageTabs.AllChats;
import com.shashi.luffy.Fragments.MessageTabs.Favouriteschats;
import com.shashi.luffy.Fragments.MessageTabs.Onlinepeople;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageFragment extends Fragment {
    TabLayout tabLayout;
    ViewPager viewPager;
    CircleImageView pp;
    DatabaseReference db;

    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_message, container, false);
        tabLayout=view.findViewById(R.id.tabs);
        viewPager=view.findViewById(R.id.viewPager);
        pp=view.findViewById(R.id.profile_pic);
        db= FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image=""+dataSnapshot.child("image").getValue();
                try {
                    Picasso.get().load(image).into(pp);
                }catch (Exception e){
                    Picasso.get().load(R.drawable.luffy).into(pp);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        assert getFragmentManager() != null;
        ViewPageAdapter adapter=new ViewPageAdapter(getFragmentManager());
        adapter.add(new AllChats(),"Recent");
        adapter.add(new Onlinepeople(),"Online");
        adapter.add(new Favouriteschats(),"Favourites");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }


    }


