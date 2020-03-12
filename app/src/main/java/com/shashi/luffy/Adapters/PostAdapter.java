package com.shashi.luffy.Adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shashi.luffy.FollowersActivity;
import com.shashi.luffy.Fragments.CommentActivity;
import com.shashi.luffy.Model.PostModel;
import com.shashi.luffy.Profile_Options.FriendProfile;
import com.shashi.luffy.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<PostModel> postList;
    private Context context;
    private String myUid,kuid;
    private DatabaseReference likeRef;
    private DatabaseReference postRef;
    private boolean mProcessLike=false;
    public PostAdapter(Context context,List<PostModel> postList){
        this.context=context;
        this.postList=postList;
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
            myUid=FirebaseAuth.getInstance().getCurrentUser().getUid();
        }
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef=FirebaseDatabase.getInstance().getReference().child("Posts");
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.post_view,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final String uId = postList.get(position).getUid();
        kuid=uId;
        final String pId=postList.get(position).getpId();
        final String pDescription=postList.get(position).getDescription();
        final String pImage=postList.get(position).getPost_image();
        String pTimestamp=postList.get(position).getTimestamp();
        String pLikes=postList.get(position).getpLikes();
        String pComments=postList.get(position).getpComments();
        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimestamp));
        String pTime= DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();
        holder.pDescriptionTv.setText(pDescription);
        DatabaseReference db=FirebaseDatabase.getInstance().getReference().child("Users").child(kuid).child("name");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ino=dataSnapshot.getValue().toString();
                holder.uNameTv.setText(ino);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        holder.pTimeTv.setText(pTime);
        holder.pLikesTv.setText(pLikes+" Likes");
        holder.pCommentsTv.setText(pComments+" Comments");
        setLikes(holder,pId);
        Picasso.get().load(pImage).networkPolicy(NetworkPolicy.OFFLINE).into(holder.pImageIv, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Exception e) {
                Picasso.get().load(pImage).into(holder.uPictureIv);
            }
        });
        if(pImage.equals("noImage")){
            holder.pImageIv.setVisibility(View.GONE);
        }else{
            holder.pImageIv.setVisibility(View.VISIBLE);
            try{
                Picasso.get().load(pImage).into(holder.pImageIv);
            }catch (Exception e){
                Picasso.get().load(R.drawable.luffy).into(holder.uPictureIv);
            }
        }
        try{
            DatabaseReference sdb=FirebaseDatabase.getInstance().getReference().child("Users").child(kuid).child("image");
            sdb.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String sakura=dataSnapshot.getValue().toString();
                    Picasso.get().load(sakura).placeholder(R.drawable.luffy).into(holder.uPictureIv);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }catch (Exception e){
            Picasso.get().load(R.drawable.luffy).into(holder.uPictureIv);
        }

        holder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions(holder.moreBtn,uId,myUid,pId,pImage);
            }
        });
        holder.LikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int pLikes= Integer.parseInt(postList.get(position).getpLikes());
                mProcessLike=true;
                final String postIde=postList.get(position).getpId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(mProcessLike){
                            if(dataSnapshot.child(postIde).hasChild(myUid)){
                                postRef.child(postIde).child("pLikes").setValue(""+(pLikes-1));
                                likeRef.child(postIde).child(myUid).removeValue();
                                mProcessLike=false;
                            }else{
                                final String jk = postList.get(position).getUid();
                                if(!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid().equals(jk)){
                                    addNotiffication(jk,postIde);
                                }
                                postRef.child(postIde).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        holder.pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, FollowersActivity.class);
                intent.putExtra("id",pId);
                intent.putExtra("title","Likes");
                context.startActivity(intent);
            }
        });
        holder.CommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context,CommentActivity.class);
                i.putExtra("postId",pId);
                i.putExtra("frienduid",uId);
                context.startActivity(i);
            }
        });
        holder.ShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable=(BitmapDrawable)holder.pImageIv.getDrawable();
                if(bitmapDrawable==null){
                    shareText(pDescription);
                }else{
                    Bitmap bitmap=bitmapDrawable.getBitmap();
                    sharePost(bitmap,pDescription);
                }
            }
        });
        holder.uNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent k=new Intent(context, FriendProfile.class);
                k.putExtra("hisUid",kuid);
                context.startActivity(k);
            }
        });

    }
    private void shareText(String pDescription) {
        String s=pDescription;
        Intent sIntent=new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT,s);
        context.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }
    private void sharePost(Bitmap bitmap, String pDescription) {

        Uri uri=saveImage(bitmap);
        Intent sIntent=new Intent(Intent.ACTION_SEND);

        sIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sIntent.putExtra(Intent.EXTRA_TEXT, pDescription);
        sIntent.putExtra(Intent.EXTRA_SUBJECT,"Subject Here");
        sIntent.setType("image/*");
        context.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }




    private Uri saveImage(Bitmap bitmap) {
        File imageFolder=new File(context.getCacheDir(),"images");
        Uri uri=null;
        try{
            imageFolder.mkdirs();
            File file=new File(imageFolder,"share_image.png");
            FileOutputStream stream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,stream);
            stream.flush();
            stream.close();
            uri= FileProvider.getUriForFile(context,"android.support.FILE_PROVIDER_PATHS",file);

        }catch(Exception e){
            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
        }
        return uri;
    }

    private void setLikes(final ViewHolder holder, final String postkey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(postkey).hasChild(myUid)){
                    holder.LikeBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like_blue,0,0,0);
                    holder.LikeBtn.setText("Liked");
                }else {
                    holder.LikeBtn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
                    holder.LikeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addNotiffication(String userid,String postId){
        if(!userid.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())){
            DatabaseReference reference=FirebaseDatabase.getInstance().getReference("NotificationActivity").child(userid);
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            hashMap.put("text","liked your post");
            hashMap.put("postid",postId);
            hashMap.put("time",String.valueOf(System.currentTimeMillis()));
            hashMap.put("ispost",true);
            reference.push().setValue(hashMap);
        }

    }

    private void showMoreOptions(ImageButton moreBtn, String uId, String myUid, final String pId, final String pImage) {
        final PopupMenu popupMenu=new PopupMenu(context,moreBtn, Gravity.END);
        if(uId.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE,0,0,"Delete");
        }
        popupMenu.getMenu().add(Menu.NONE,2,0,"View Details");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id=item.getItemId();
                if(id==0){
                    beginDelete(pId,pImage);
                }else if(id==2){
                    Intent i=new Intent(context, CommentActivity.class);
                    i.putExtra("postId",pId);
                    context.startActivity(i);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }else{
            deleteWithImage(pId,pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Deleting...");
        StorageReference picRef= FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query s= FirebaseDatabase.getInstance().getReference("PostNumbers").child(myUid).orderByChild(pId).equalTo(pId);
                s.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context,"Post Deleted",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
                fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            ds.getRef().removeValue();
                        }



                        Toast.makeText(context,"Post Deleted",Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void deleteWithoutImage(String pId) {
        final ProgressDialog pd=new ProgressDialog(context);
        pd.setMessage("Deleting...");
        FirebaseDatabase.getInstance().getReference().child("PostNumbers").child(myUid).child(pId).removeValue();
        Query fquery= FirebaseDatabase.getInstance().getReference("Posts").orderByChild("pId").equalTo(pId);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context,"Post Deleted",Toast.LENGTH_LONG).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageView uPictureIv,pImageIv;
        TextView uNameTv,pTimeTv,pDescriptionTv,pLikesTv,pCommentsTv;
        ImageButton moreBtn;
        Button LikeBtn,CommentBtn,ShareBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            uPictureIv=mView.findViewById(R.id.upictureIv);
            pImageIv=mView.findViewById(R.id.pImageIv);
            uNameTv=mView.findViewById(R.id.uNameTv);
            pTimeTv=mView.findViewById(R.id.pTimeTv);
            pDescriptionTv=mView.findViewById(R.id.pDescriptionTv);
            pLikesTv=mView.findViewById(R.id.pLikesTv);
            moreBtn=mView.findViewById(R.id.moreBtn);
            LikeBtn=mView.findViewById(R.id.likeBtn);
            CommentBtn=mView.findViewById(R.id.commentBtn);
            ShareBtn=mView.findViewById(R.id.shareBtn);
            pCommentsTv=mView.findViewById(R.id.pCommentsTv);

        }
    }
}

