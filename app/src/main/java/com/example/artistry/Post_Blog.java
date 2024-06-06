package com.example.artistry;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Post_Blog extends AppCompatActivity {
    public TextView textView;
    public ImageView imageView;

    private EditText Tittle,des;
    private Button Publish;
    private DatabaseReference mrootref;
    private FirebaseAuth maAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_post_blog);

        Tittle=findViewById(R.id.profession);
        des = findViewById(R.id.bio);
        Publish = findViewById(R.id.btnlogin);

        imageView=(ImageView) findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Post_Blog.this,Home.class);
                startActivity(intent);
            }
        });

        Publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });


    }

    private void upload() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
        pd.show();

        String txttittle = Tittle.getText().toString().trim();
        String txtdes = des.getText().toString().trim();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("blog");
        String postId = ref.push().getKey();

        if(TextUtils.isEmpty(txttittle) || TextUtils.isEmpty(txtdes)){
            pd.dismiss();
            Toast.makeText(Post_Blog.this, "Enter all the fields!", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String,Object> map = new HashMap<>();
            map.put("postid",postId);
            map.put("tittle",txttittle);
            map.put("description",txtdes);
            map.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());

            ref.child(postId).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        pd.dismiss();
                        Toast.makeText(Post_Blog.this, "Uploaded!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Post_Blog.this,Home.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Post_Blog.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Post_Blog.this,Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });

        }
    }
}