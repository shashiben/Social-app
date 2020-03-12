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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Fragments.ChatScreenActivity;
import com.shashi.luffy.Model.RecentChatModel;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

public class RecentChatAdapter extends  RecyclerView.Adapter<RecentChatAdapter.ViewHolder> {
    public List<RecentChatModel> userModelList;
    String type;
    private Context context;
    public RecentChatAdapter(Context context, List<RecentChatModel> userModelList){
        this.context=context;
        this.userModelList=userModelList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final String hisUid=userModelList.get(position).getUid();
        holder.nameText.setText(userModelList.get(position).getName());
        final String userid= FirebaseAuth.getInstance().getCurrentUser().getUid();
        String o=userModelList.get(position).getOnlineStatus();
        if(o.equals("online")){
            holder.on.setImageResource(R.drawable.circle_online);
        }else {
            holder.on.setVisibility(View.GONE);
        }
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("ChatList");
        db.keepSynced(true);
        db.child(hisUid).child(userid).child("type").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                type= Objects.requireNonNull(dataSnapshot.getValue()).toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference().child("ChatList").child(hisUid).child(userid).child("message").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String p= Objects.requireNonNull(dataSnapshot.getValue()).toString();
                if(type.equals("text")){
                    holder.lastmsg.setText(p);
                }else{
                    holder.lastmsg.setText("\uD83D\uDCF7 sent you a photo");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            Picasso.get().load(userModelList.get(position).getImage()).error(R.drawable.luffy).into(holder.imageText);
        }catch (Exception e){
            Picasso.get().load(R.drawable.luffy).into(holder.imageText);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatScreenActivity.class);
                intent.putExtra("hisUid",hisUid);
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(context);
                builder.setMessage("Delete Chat from List");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("ChatList").child(userid).child(hisUid).removeValue();


                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                return false;
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

    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        public TextView nameText,lastmsg;
        ImageView imageText,on;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            on=mView.findViewById(R.id.circle_online);
            nameText=mView.findViewById(R.id.usrUsername);
            lastmsg=mView.findViewById(R.id.userail);
            imageText=mView.findViewById(R.id.useImage);
        }
    }



}
