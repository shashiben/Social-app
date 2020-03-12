package com.shashi.luffy.Profile_Options;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.shashi.luffy.Adapters.TablayoutAdapter;
import com.shashi.luffy.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendProfile extends AppCompatActivity {
    Button followStatus,message;
    CircleImageView pp;
    ImageView CoverPhoto;
    TabLayout tabs;
    ViewPager viewPager;
    TablayoutAdapter adapter;
    TextView postno,followersno,followingno,name,location;
    String receiverUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Intent get=getIntent();
    }
}
