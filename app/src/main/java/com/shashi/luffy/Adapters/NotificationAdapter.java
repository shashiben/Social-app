package com.shashi.luffy.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shashi.luffy.Fragments.PostFragment;
import com.shashi.luffy.Fragments.ProfileFragment;
import com.shashi.luffy.Model.NotificationModel;
import com.shashi.luffy.Model.PostModel;
import com.shashi.luffy.Model.UserModel;
import com.shashi.luffy.R;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class NotificationAdapter  extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{
    public List<NotificationModel> notificationModelList;
    private Context context;
    public NotificationAdapter(Context context,List<NotificationModel> notificationModelList){
        this.context=context;
        this.notificationModelList=notificationModelList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final NotificationModel notification=notificationModelList.get(position);
        holder.typeing.setText(notification.getText());
        getUserInfo(holder.pic,holder.username,notification.getUserid());
        String k=notification.getTime();
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(k));
        String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.time.setText(pTime);
        if(notification.isIspost()){
            holder.photo.setVisibility(View.VISIBLE);
            getPostImage(holder.photo,notification.getPostid());
        }else{
            holder.photo.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notification.isIspost()){
                    SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",context.MODE_PRIVATE).edit();
                    editor.putString("postid",notification.getPostid());
                    editor.apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.bottom_notification,
                            new PostFragment());
                }else{
                    SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",context.MODE_PRIVATE).edit();
                    editor.putString("profileid",notification.getUserid());
                    editor.apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.bottom_notification,
                            new ProfileFragment());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username,typeing,time;
        CircleImageView pic;
        ImageView photo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            time=itemView.findViewById(R.id.notifytime);
            username=itemView.findViewById(R.id.notifyusername);
            typeing=itemView.findViewById(R.id.typenotify);
            pic=itemView.findViewById(R.id.notifydp);
            photo=itemView.findViewById(R.id.notifyphoto);
        }
    }
    private void getUserInfo(final ImageView imageView, final TextView username, String  publisherId){

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(publisherId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserModel user=dataSnapshot.getValue(UserModel.class);
                Picasso.get().load(user.getImage()).into(imageView);
                username.setText(user.getName());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void getPostImage(final ImageView imageView, final String postId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostModel post=dataSnapshot.getValue(PostModel.class);
                assert post != null;
                Picasso.get().load(post.getPost_image()).into(imageView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
