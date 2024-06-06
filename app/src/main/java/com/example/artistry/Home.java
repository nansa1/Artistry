package com.example.artistry;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.artistry.Adapter.PostAdapter;
import com.example.artistry.Model.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    public CardView cardView;
    private DatabaseReference mrootref;
    private FirebaseAuth maAuth;
    private String id;
    private ImageView editimage;

  private RecyclerView recyclerViewPosts;
   private PostAdapter postAdapter;
   private List<Post> postList;

   private List<String> followingList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_home);

        editimage = findViewById(R.id.editimage);


        maAuth = FirebaseAuth.getInstance();
        id = maAuth.getCurrentUser().getUid();
        mrootref= FirebaseDatabase.getInstance().getReference().child("Users");
        if(TextUtils.isEmpty(id)){
            //Exit app
            System.exit(0);

        }else{

            getuserdata();

        }

        cardView=(CardView) findViewById(R.id.cardViewFirst);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this,Edit_Profile.class);
                startActivity(intent);
            }
        });

        recyclerViewPosts = findViewById(R.id.recycle_view_posts);
        post();

       // checkFollowingUser();


        final BottomNavigationView bnv=findViewById(R.id.bottomBar);
        bnv.setSelectedItemId(R.id.home);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull  MenuItem item) {
                switch (item.getItemId()){
                    case R.id.home:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(),Profile_Page.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.search:
                        startActivity(new Intent(getApplicationContext(),SearchBar.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.post:
                        startActivity(new Intent(getApplicationContext(),Post_FirstPage.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.blog:
                        startActivity(new Intent(getApplicationContext(),Home_Blog.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

    }

    private void checkFollowingUser() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    followingList.add(ds.getKey());
                }

                readpost();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void post(){
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Home.this);
        linearLayoutManager.setStackFromEnd(true);

        linearLayoutManager.setReverseLayout(true);

        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();

        postAdapter = new PostAdapter(Home.this,postList);

        recyclerViewPosts.setAdapter(postAdapter);
        followingList = new ArrayList<>();

        checkFollowingUser();

        /*readpost();*/
    }

    private void readpost() {

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);

                    for (String id : followingList){
                        if(post.getPublisher().equals(id)){
                            postList.add(post);
                        }

                    }
                    /*postList.add(post);*/
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getuserdata() {
        mrootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("id").getValue().equals(id)){

                        String s =ds.child("imageurl").getValue(String.class);

                        if(!s.equals("default")){

                            Picasso.get().load(s).into(editimage);

                        }else{

                        }


                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}