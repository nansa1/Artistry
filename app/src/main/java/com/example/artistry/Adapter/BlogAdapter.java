package com.example.artistry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artistry.Model.Blog;
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

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.Viewholder> {

    private Context mContext;
    private List<Blog> mBlog;

    private FirebaseUser firebaseUser;

    public BlogAdapter(Context mContext, List<Blog> mBlog) {
        this.mContext = mContext;
        this.mBlog = mBlog;
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.blog_item,parent,false);
        return new BlogAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Blog blog = mBlog.get(position);
        holder.description.setText(blog.getDescription());
        holder.tittle.setText(blog.getTittle());

        FirebaseDatabase.getInstance().getReference().child("Users").child(blog.getPublisher()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                if(user.getImageurl().equals("default")){
                    holder.profile_image.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Picasso.get().load(user.getImageurl()).into(holder.profile_image);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return mBlog.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder{

        public ImageView profile_image;
        public TextView tittle;
        public TextView description;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            profile_image = itemView.findViewById(R.id.profile_image_blog);
            tittle = itemView.findViewById(R.id.tittle_blog);
            description = itemView.findViewById(R.id.description_blog);

        }
    }

}
