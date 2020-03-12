package com.shashi.luffy.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashi.luffy.Fragments.CommentActivity;
import com.shashi.luffy.Model.PostModel;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostProfileAdapter extends RecyclerView.Adapter<PostProfileAdapter.ViewHolder>{
    private Context context;
    private List<PostModel> mPosts;
    public PostProfileAdapter(Context context, List<PostModel> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.postprofile_item,parent,false);
        return new PostProfileAdapter.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        PostModel postModel=mPosts.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uId = mPosts.get(position).getUid();
                final String pId=mPosts.get(position).getpId();
                Intent i=new Intent(context, CommentActivity.class);
                i.putExtra("postId",pId);
                i.putExtra("frienduid",uId);
                context.startActivity(i);
            }
        });
        String i=mPosts.get(position).getPost_image();
        Picasso.get().load(i).into(holder.post_image);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView post_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            post_image=itemView.findViewById(R.id.post_image);
        }
    }
}
