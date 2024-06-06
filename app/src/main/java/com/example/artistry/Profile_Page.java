package com.example.artistry;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

public class Profile_Page extends AppCompatActivity {

    public TextView textView,posttxt,blogtxt;
    public ImageView imageView;
    public CardView cardView;
    private ImageView roundedImageView;
    private TextView name,bio;
    private DatabaseReference mrootref;
    private FirebaseAuth maAuth;
    private String id;
    ProgressDialog pd;
    private int count = 0;

    private RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        getSupportActionBar().hide();

        pd = new ProgressDialog(this);

        posttxt=findViewById(R.id.textView12);
        blogtxt = findViewById(R.id.textView11);



        imageView=(ImageView) findViewById(R.id.logout);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClickLogout(this);
            }
        });



        imageView=(ImageView) findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Page.this,Home.class);
                startActivity(intent);
            }
        });


        //Code for link page with the help of Text.
        textView=(TextView) findViewById(R.id.blog);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile_Page.this,Profile_Blog.class);
                startActivity(intent);
            }
        });
        // End of the Link Code.
        recyclerViewPosts = findViewById(R.id.recycle_view_posts_profile);
        post();

        //code for navigation bar
        final BottomNavigationView bnv=findViewById(R.id.bottomBar);
        bnv.setSelectedItemId(R.id.profile);
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        return true;
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(),Home.class));
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
        // end of navigation bar

        roundedImageView = findViewById(R.id.roundedImageView);
        name = findViewById(R.id.textView2);
        bio = findViewById(R.id.textView7);

        maAuth = FirebaseAuth.getInstance();
        id = maAuth.getCurrentUser().getUid();
        mrootref= FirebaseDatabase.getInstance().getReference().child("Users");
        if(TextUtils.isEmpty(id)){
            //Exit app
            System.exit(0);

        }else{

            getuserdata();

        }


    }

    private void getuserdata() {

        pd.setMessage("Please Wait!");
        pd.show();
        mrootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("id").getValue().equals(id)){
                        name.setText(ds.child("name").getValue(String.class));
                        bio.setText(ds.child("bio").getValue(String.class));
                        String s =ds.child("imageurl").getValue(String.class);

                        if(!s.equals("default")){

                            Picasso.get().load(s).into(roundedImageView);

                        }else{

                        }

                        pd.dismiss();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mrootref= FirebaseDatabase.getInstance().getReference("Posts");
        mrootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count =0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("publisher").getValue().equals(id)){
                        count = count + 1;
                    }
                }
                String c = String.valueOf(count);
                posttxt.setText((c));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mrootref= FirebaseDatabase.getInstance().getReference("blog");
        mrootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count =0;
                for(DataSnapshot ds : snapshot.getChildren()){
                    if(ds.child("publisher").getValue().equals(id)){
                        count = count + 1;
                    }
                }
                String c = String.valueOf(count);
                blogtxt.setText(c);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void ClickLogout(View.OnClickListener view){
        // Close app
        logout( this);
    }
    private void logout(Activity activity) {
        // Initialize alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        //set title
        builder.setTitle("Logout");
        //set massage
        builder.setMessage("Are you sure you want to logout ? ");
        // Positive yes button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Finish activity
                activity.finishAffinity();

                FirebaseAuth.getInstance().signOut();
                //Exit app

                startActivity(new Intent(Profile_Page.this,MainActivity.class));;
            }
        });

        // Negative no Button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.show();
    }
    public void post(){
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Profile_Page.this);
        linearLayoutManager.setStackFromEnd(true);

        linearLayoutManager.setReverseLayout(true);

        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();

        postAdapter = new PostAdapter(Profile_Page.this,postList);

        recyclerViewPosts.setAdapter(postAdapter);
        /*followingList = new ArrayList<>();

        checkFollowingUser();*/

        readpost();
    }
    private void readpost() {

        FirebaseDatabase.getInstance().getReference("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    Post post = ds.getValue(Post.class);


                        if(post.getPublisher().equals(id)){
                            postList.add(post);
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
}