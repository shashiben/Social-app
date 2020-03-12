package com.shashi.luffy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.shashi.luffy.Authenication.LoginActivity;
import com.shashi.luffy.Fragments.MessageFragment;
import com.shashi.luffy.Fragments.NotificationFragment;
import com.shashi.luffy.Fragments.PostFragment;
import com.shashi.luffy.Fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    CurvedBottomNavigationView curvedBottomNavigationView;
    FrameLayout view;
    private MessageFragment messageFragment;
    private PostFragment postFragment;
    private ProfileFragment profileFragment;
    private NotificationFragment notificationFragment;
    FloatingActionButton fab;
    String uid;
    AlertDialog dialog_verifying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        messageFragment=new MessageFragment();
        postFragment=new PostFragment();
        profileFragment=new ProfileFragment();
        notificationFragment=new NotificationFragment();
        curvedBottomNavigationView=findViewById(R.id.curvedBNV);
        curvedBottomNavigationView.inflateMenu(R.menu.bottom_menu);
        fab=findViewById(R.id.add_post);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,AddPostActivity.class));
            }
        });
        view=findViewById(R.id.main_container);
        replaceFragment(postFragment);
        curvedBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_home:
                        replaceFragment(postFragment);
                        return true;
                    case R.id.bottom_users:
                        replaceFragment(messageFragment);
                        return true;
                    case R.id.bottom_profile:
                        replaceFragment(profileFragment);
                        return true;
                    case R.id.bottom_notification:
                        replaceFragment(notificationFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        checkUserStatus();
        super.onResume();
    }

    private void checkUserStatus() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }else {
            uid=currentUser.getUid();
            SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            editor.putString("Current_USERID",uid);
            editor.apply();
        }
    }
    private void sendToLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }
    private void checkConnection(){
        ConnectivityManager manager=(ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo activeNetwork=manager.getActiveNetworkInfo();
        if(null!=activeNetwork){

        }else{
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout= inflater.inflate(R.layout.panda_dribble_wait,null);
            AlertDialog.Builder show = new AlertDialog.Builder(MainActivity.this);
            show.setView(alertLayout);
            show.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkConnection();
                }
            });
            show.setCancelable(true);
            dialog_verifying = show.create();
            dialog_verifying.show();
        }

    }
    @Override
    protected void onStart() {
        checkUserStatus();
        checkConnection();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("onlineStatus").setValue("online");
        }
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sendToLogin();
        }
        super.onStart();
    }
    @Override
    public void onPause() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            FirebaseDatabase.getInstance().getReference("Users").
                    child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    child("onlineStatus").setValue(String.valueOf(System.currentTimeMillis()));
        }
        super.onPause();
    }
}
