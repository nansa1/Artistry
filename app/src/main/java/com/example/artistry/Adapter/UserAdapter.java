package com.example.artistry.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artistry.Model.User;
import com.example.artistry.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private boolean isFargement;

    private FirebaseUser FirebaseUser;

    public UserAdapter(Context mContext, List<User> mUsers, boolean isFargement) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.isFargement = isFargement;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        FirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        User user = mUsers.get(position);
        holder.btnfollow.setVisibility(View.VISIBLE);

        holder.username.setText(user.getUsername());
        holder.fullname.setText(user.getName());

        Picasso.get().load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.imageprofile);

        isFollowed(user.getId(),holder.btnfollow);

        //holder.btnfollow.setText("Follow");

        if(user.getId().equals(FirebaseUser.getUid())){
            holder.btnfollow.setVisibility(View.GONE);
        }

        holder.btnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.btnfollow.getText().toString().equals(("follow"))){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child((FirebaseUser.getUid())).child("following").child(user.getId()).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(FirebaseUser.getUid()).setValue(true);
                }else{
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child((FirebaseUser.getUid())).child("following").child(user.getId()).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getId()).child("followers").child(FirebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    private void isFollowed(final String id,final Button btnfollow) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseUser.getUid())
                .child("following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(id).exists()){
                    btnfollow.setText("following");
                }else {
                    btnfollow.setText("follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public CircleImageView imageprofile;
        public TextView username;
        public TextView fullname;
        public Button btnfollow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageprofile = itemView.findViewById(R.id.image_profile);
            username = itemView.findViewById(R.id.usersname);
            fullname = itemView.findViewById(R.id.fullname);
            btnfollow = itemView.findViewById(R.id.btn_follow);


        }
    }

}
