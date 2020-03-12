package com.shashi.luffy.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashi.luffy.Model.UserModel;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OnlinePeopleAdapter extends RecyclerView.Adapter<OnlinePeopleAdapter.ViewHolder> {
    private Context context;
    private List<UserModel> userModelList;

    public OnlinePeopleAdapter(Context context, List<UserModel> userModelList) {
        this.context = context;
        this.userModelList = userModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String image=userModelList.get(position).getImage();
        try{
            Picasso.get().load(image).into(holder.pp);

        }catch (Exception e){
            Picasso.get().load(R.drawable.luffy).into(holder.pp);
        }
        holder.name.setText(userModelList.get(position).getName());
        holder.mail.setText(userModelList.get(position).getEmail());
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
