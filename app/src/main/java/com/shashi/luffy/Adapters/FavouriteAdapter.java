package com.shashi.luffy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Fragments.ChatScreenActivity;
import com.shashi.luffy.Model.FavouriteChatModel;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.ViewHolder>{
    private Context context;
    private List<FavouriteChatModel> userModelList;

    public FavouriteAdapter(Context context, List<FavouriteChatModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view,parent,false);
        return new FavouriteAdapter.ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String kuid=userModelList.get(position).getUid();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ChatScreenActivity.class);
                intent.putExtra("hisUid",kuid);
                context.startActivity(intent);
            }
        });
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.child(kuid).addValueEventListener(new ValueEventListener() {
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.userUsername);
            pp=itemView.findViewById(R.id.userImage);
            mail=itemView.findViewById(R.id.userEmail);
        }
    }
}
