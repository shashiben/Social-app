package com.shashi.luffy.Profile_Options.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Profile_Options.More_Info;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

    TextView fullname,email,dob,location,moreinfo,phoneNo,gender;
    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        fullname = view.findViewById(R.id.fullname);
        email = view.findViewById(R.id.email);
        location = view.findViewById(R.id.Location);
        moreinfo = view.findViewById(R.id.moreinfo);
        phoneNo = view.findViewById(R.id.phoneNo);
        gender = view.findViewById(R.id.gender);
        dob = view.findViewById(R.id.dateofBirth);
        moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), More_Info.class));
            }
        });
        String uidFriend = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference friendDb = FirebaseDatabase.getInstance().getReference("Users").child(uidFriend);
        friendDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.exists()) {
                    String gend = "" + ds.child("gender").getValue().toString();
                    String locat = "" + ds.child("location").getValue().toString();
                    String ph = "" + ds.child("phone").getValue().toString();
                    String em = "" + ds.child("email").getValue().toString();
                    String d = "" + ds.child("dob").getValue().toString();
                    String f = "" + ds.child("fullname").getValue().toString();

                    fullname.setText(f);
                    phoneNo.setText(ph);
                    email.setText(em);
                    dob.setText(d);
                    gender.setText(gend);
                    location.setText(locat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return view;
    }
    }
