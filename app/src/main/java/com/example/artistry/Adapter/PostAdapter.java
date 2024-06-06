package com.example.artistry.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.artistry.Comment;
import com.example.artistry.Model.Post;
import com.example.artistry.Model.User;
import com.example.artistry.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.Viewholder>{

    AnimatedVectorDrawable avd2;
    private Context mContext;
    private List<Post> mPost;

    private FirebaseUser firebaseUser;

    boolean  doubleClick = false;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item,parent,false);
        return new PostAdapter.Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {



        Post post = mPost.get(position);
        Picasso.get().load(post.getImageurl()).into(holder.post_image);
        holder.description.setText(post.getDescription());
        /*holder.noOfLike.setText(post.getTittle());*/

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getPublisher())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        if(user.getImageurl().equals("default")){
                            holder.imageProfile.setImageResource(R.mipmap.ic_launcher);
                        }else {
                            Picasso.get().load(user.getImageurl()).into(holder.imageProfile);
                        }


                        holder.username.setText(user.getUsername());
                        holder.author.setText(user.getName());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        isLiked(post.getPostid(), holder.like);
        noOfLikes(post.getPostid(),holder.noOfLike);

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.like.getTag().equals("like")){
                   FirebaseDatabase.getInstance().getReference().child("Likes")
                           .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes")
                            .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, Comment.class);
                intent.putExtra("postId",post.getPostid());
                intent.putExtra("authorId",post.getPublisher());
                mContext.startActivity(intent);
            }
        });
        Handler handler=new Handler();

        final Drawable drawable = holder.likeImage.getDrawable();

        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable r = new Runnable() {
                    @Override
                    public void run() {

                        doubleClick = false;
                    }
                };
                if (doubleClick) {

                    if(holder.like.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                .child(post.getPostid()).child(firebaseUser.getUid()).setValue(true);
                        holder.likeImage.setAlpha(0.70f);

                        if(drawable instanceof AnimatedVectorDrawable){
                            avd2 = (AnimatedVectorDrawable) drawable;
                            avd2.start();
                        }


                    }else {
                        FirebaseDatabase.getInstance().getReference().child("Likes")
                                .child(post.getPostid()).child(firebaseUser.getUid()).removeValue();
                    }
                    doubleClick = false;

                }else {
                    doubleClick=true;
                    handler.postDelayed(r, 500);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public ImageView imageProfile;
        public ImageView post_image;
        public ImageView like;
        public ImageView comment;
        public ImageView save;
        public ImageView more;
        public ImageView likeImage;

        public TextView username;
        public TextView noOfLike;
        public TextView author;
        public TextView description;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageProfile = itemView.findViewById(R.id.profile_image);
            post_image = itemView.findViewById(R.id.post_image);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
            save = itemView.findViewById(R.id.save);
            more = itemView.findViewById(R.id.more);
            likeImage = itemView.findViewById(R.id.likeimage);

            username = itemView.findViewById(R.id.username);
            noOfLike = itemView.findViewById(R.id.tittle);
            author = itemView.findViewById(R.id.author);
            description = itemView.findViewById(R.id.description);

        }
    }

    private void isLiked(String postId,ImageView imageView){

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(firebaseUser.getUid()).exists()){

                    imageView.setImageResource(R.drawable.ic_liked_foreground);
                    imageView.setTag("liked");

                }else{
                    imageView.setImageResource(R.drawable.ic_like_foreground);
                    imageView.setTag("like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void noOfLikes(String postId,TextView text){

        FirebaseDatabase.getInstance().getReference().child("Likes").child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        text.setText(snapshot.getChildrenCount()+" Likes");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}
