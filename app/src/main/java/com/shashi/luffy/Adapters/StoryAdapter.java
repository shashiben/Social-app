package com.shashi.luffy.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Model.Story;
import com.shashi.luffy.Model.UserModel;
import com.shashi.luffy.Profile_Options.AddStoryActivity;
import com.shashi.luffy.Profile_Options.StoryActivity;
import com.shashi.luffy.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoryAdapter extends  RecyclerView.Adapter<StoryAdapter.ViewHolder>{
    public List<Story> storyList;
    private Context context;
    public StoryAdapter(Context context,List<Story> storyList){
        this.context=context;
        this.storyList=storyList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==0){
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.add_story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.story_item,parent,false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Story story=storyList.get(position);
        userInfo(holder,story.getUserid(),position);
        if(holder.getAdapterPosition()!=0){
            seenStory(holder,story.getUserid());
        }if(holder.getAdapterPosition()==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.getAdapterPosition()==0){
                    myStory(holder.addstory_text,holder.story_plus,true);

                }else{
                    Intent intent=new Intent(context,StoryActivity.class);
                    intent.putExtra("kim",story.getUserid());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return storyList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView story_photo,story_plus,story_photo_seen;
        TextView story_username,addstory_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            story_photo=itemView.findViewById(R.id.story_photo);
            story_plus=itemView.findViewById(R.id.story_plus);
            story_photo_seen=itemView.findViewById(R.id.story_photo_seen);
            story_username=itemView.findViewById(R.id.story_username);
            addstory_text=itemView.findViewById(R.id.addStory_text);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return  0;
        }
        return 1;
    }
    private void userInfo(final ViewHolder viewHolder, final String userid, final int pos){
        if(userid!=null){
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
            reference.keepSynced(true);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    final UserModel userModel=dataSnapshot.getValue(UserModel.class);
                    assert userModel != null;
                    if(userModel.getImage()!=null){
                        Picasso.get().load(userModel.getImage()).networkPolicy(NetworkPolicy.OFFLINE).into(viewHolder.story_photo, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load(userModel.getImage()).into(viewHolder.story_photo);
                            }
                        });
                        Picasso.get().load(userModel.getImage()).into(viewHolder.story_photo);
                    }else{
                        Picasso.get().load(R.drawable.luffy).into(viewHolder.story_photo);
                    }
                    if (pos != 0) {
                        if(userModel.getImage()!=null){
                            Picasso.get().load(userModel.getImage()).into(viewHolder.story_photo_seen);

                        }else{
                            Picasso.get().load(R.drawable.luffy).into(viewHolder.story_photo_seen);
                        }
                        viewHolder.story_username.setText(userModel.getName());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }
    private void myStory(final TextView textView, final ImageView imageView, final boolean click){
        DatabaseReference dr=FirebaseDatabase.getInstance().getReference().child("Story").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        dr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                long timecurrent=System.currentTimeMillis();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    Story story=ds.getValue(Story.class);
                    assert story != null;
                    if(timecurrent>story.getTimestart() && timecurrent<story.getTimeend()){
                        count++;
                    }
                }
                if(click){
                    if(count>0){
                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
                        builder.setPositiveButton("View Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(context, StoryActivity.class);
                                intent.putExtra("kim",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                context.startActivity(intent);
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Add Story", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.startActivity(new Intent(context,AddStoryActivity.class));
                                dialog.dismiss();
                            }
                        });
                        builder.create().show();
                    }else{
                        context.startActivity(new Intent(context, AddStoryActivity.class));
                    }

                }else{
                    if(count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else{
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference def=FirebaseDatabase.getInstance().getReference().child("Story").child(userid);
        def.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(!ds.child("views").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).exists() &&
                            System.currentTimeMillis()< Objects.requireNonNull(ds.getValue(Story.class)).getTimeend()){
                        i++;
                    }
                }
                if(i>0){
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                    viewHolder.story_photo.setVisibility(View.VISIBLE);

                }else {
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                    viewHolder.story_photo.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
