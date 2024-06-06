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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Signup extends AppCompatActivity {
        private TextView textView;
        private EditText username;
        private EditText name;
        private EditText email;
        private EditText password;
        private EditText cpassword;
        private Button signup;

        private DatabaseReference mrootref;
        private FirebaseAuth maAuth;

        ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        username =findViewById(R.id.username);
        name =findViewById(R.id.name);
        email =findViewById(R.id.email);
        password =findViewById(R.id.password);
        cpassword =findViewById(R.id.cpassword);
        signup = findViewById(R.id.btnlogin);

        pd = new ProgressDialog(this);

        mrootref= FirebaseDatabase.getInstance().getReference();
        maAuth = FirebaseAuth.getInstance();

        textView=(TextView) findViewById(R.id.login);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Signup.this,MainActivity.class);
                startActivity(intent);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtusername = username.getText().toString().trim();
                String txtname = name.getText().toString().trim();
                String txtemail = email.getText().toString().trim();
                String txtpassword = password.getText().toString().trim();
                String txtcpassword = cpassword.getText().toString().trim();

                if(TextUtils.isEmpty(txtusername) || TextUtils.isEmpty(txtname)
                || TextUtils.isEmpty(txtemail) || TextUtils.isEmpty(txtpassword)
                        || TextUtils.isEmpty(txtcpassword)){
                    Toast.makeText(Signup.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }else if(txtpassword.length()<6){
                    Toast.makeText(Signup.this, "Enter Strong Password", Toast.LENGTH_SHORT).show();
                }else if(!txtpassword.equals(txtcpassword)){
                    Toast.makeText(Signup.this, "Confirm Password don't match Password", Toast.LENGTH_SHORT).show();
                }else{
                    sigupuser(txtusername,txtname,txtemail,txtpassword );

                }

            }
        });


    }

    private void sigupuser(String username, String name, String email, String password) {

        pd.setMessage("Please Wait!");
        pd.show();

        maAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String,Object> map = new HashMap<>();
                map.put("name",name);
                map.put("email",email);
                map.put("username",username);
                map.put("id",maAuth.getCurrentUser().getUid());
                map.put("bio","");
                map.put("imageurl","default");

                mrootref.child("Users").child(maAuth.getCurrentUser().getUid()).setValue(map).
                        addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            pd.dismiss();
                            Toast.makeText(Signup.this, "Update profile for better" +
                                    " experience", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this,Home.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Signup.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}