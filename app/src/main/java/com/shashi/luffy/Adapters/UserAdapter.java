package com.shashi.luffy.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ramotion.foldingcell.FoldingCell;
import com.shashi.luffy.FollowersActivity;
import com.shashi.luffy.Fragments.ChatScreenActivity;
import com.shashi.luffy.Model.UserModel;
import com.shashi.luffy.Profile_Options.FriendProfile;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.ViewHolder> {
    public List<UserModel> userModelList;
    private Context context;
    public UserAdapter(Context context,List<UserModel> userModelList){
        this.context=context;
        this.userModelList=userModelList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.fc.toggle(false);
            }
        });

        final String hisUid=userModelList.get(position).getUid().toString();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUid)
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.followingno.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference().child("Follow").child(hisUid).child("followers");
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.followersno.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.nameText.setText(userModelList.get(position).getName());

        holder.emailText.setText(userModelList.get(position).getEmail());

        try {
            Picasso.get().load(userModelList.get(position).getImage()).error(R.drawable.luffy).into(holder.imageText);
        }catch (Exception e){
            Picasso.get().load(R.drawable.luffy).into(holder.imageText);
        }
        holder.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k=new Intent(context, FriendProfile.class);
                k.putExtra("hisUid",hisUid);
                context.startActivity(k);
            }
        });
        DatabaseReference reference2=FirebaseDatabase.getInstance().getReference().child("Users").child(hisUid)
                .child("noofposts");
        reference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                holder.postno.setText(""+dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatScreenActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });

        holder.imageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView imageView=new ImageView(context);
                imageView.setImageResource(R.drawable.luffy);
                AlertDialog.Builder builder=new AlertDialog.Builder(context).setView(imageView);
                builder.create().show();
                final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
                Query query=databaseReference.orderByChild("uid").equalTo(hisUid);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            String imagei=""+ds.child("image").getValue();
                            try{
                                Picasso.get().load(imagei).into(imageView);
                            }catch (Exception e){
                                Picasso.get().load(R.drawable.luffy).into(imageView);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
        holder.following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,FollowersActivity.class);
                intent.putExtra("id",hisUid);
                intent.putExtra("title","following");
                context.startActivity(intent);
            }
        });
        holder.followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FollowersActivity.class);
                intent.putExtra("id",hisUid);
                intent.putExtra("title","followers");
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return userModelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView nameText,emailText;
        public ImageView imageText;
        public FoldingCell fc;
        Button profile,message;
        TextView postno,followersno,followingno,following,followers;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            nameText=mView.findViewById(R.id.userUsername);
            emailText=mView.findViewById(R.id.userEmail);
            imageText=mView.findViewById(R.id.userImage);
            fc=mView.findViewById(R.id.folding_cell);
            postno=mView.findViewById(R.id.postno);
            followersno=mView.findViewById(R.id.followersno);
            followingno=mView.findViewById(R.id.followingno);
            following=mView.findViewById(R.id.followinghmmm);
            followers=mView.findViewById(R.id.followershmmm);
            profile=mView.findViewById(R.id.profilehmmm);
            message=mView.findViewById(R.id.messagehmmm);
        }
    }

}
