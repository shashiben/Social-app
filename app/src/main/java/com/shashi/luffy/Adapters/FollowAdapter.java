package com.shashi.luffy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Model.FavouriteChatModel;
import com.shashi.luffy.Profile_Options.FriendProfile;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.ViewHolder>{
    private Context context;
    private List<FavouriteChatModel> userModelList;

    public FollowAdapter(Context context, List<FavouriteChatModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.follow_view,parent,false);
        return new FollowAdapter.ViewHolder(view);     }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String receiverUid=userModelList.get(position).getUid();
        isFollowing(receiverUid,holder.followStatus);
        holder.followStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.followStatus.getText().toString().equals("Follow")){
                    if(!FirebaseAuth.getInstance().getCurrentUser().getUid().equals(receiverUid)){
                        holder.followStatus.setText("Following");
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child("following").child(receiverUid).setValue(true);
                        FirebaseDatabase.getInstance().getReference().child("Follow").child(receiverUid)
                                .child("followers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

                        addNotiffication(receiverUid);
                    }else{
                        holder.followStatus.setVisibility(View.GONE);
                    }

                }else{
                   holder. followStatus.setText("Follow");
                    FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("following").child(receiverUid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(receiverUid)
                            .child("followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k=new Intent(context, FriendProfile.class);
                k.putExtra("hisUid",receiverUid);
                context.startActivity(k);
            }
        });
        DatabaseReference db= FirebaseDatabase.getInstance().getReference("Users").child(receiverUid);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String image=dataSnapshot.child("image").getValue().toString();
                try{
                    Picasso.get().load(image).into(holder.pp);

                }catch (Exception e){
                    Picasso.get().load(R.drawable.luffy).into(holder.pp);
                }
                holder.name.setText(dataSnapshot.child("name").getValue().toString());
                holder.mail.setText(dataSnapshot.child("email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CircleImageView pp;
        TextView name,mail;
        Button followStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.username);
            mail=itemView.findViewById(R.id.email);
            followStatus=itemView.findViewById(R.id.follow);
            pp=itemView.findViewById(R.id.pp);
        }
    }
    private void isFollowing(final String userid, final Button button){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Follow").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(userid).exists()){
                    button.setText("Following");
                }else{
                    button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addNotiffication(String receiverUid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("NotificationActivity").child(receiverUid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
        hashMap.put("text","started following you");
        hashMap.put("postid","");
        hashMap.put("time",String.valueOf(System.currentTimeMillis()));
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }
}
