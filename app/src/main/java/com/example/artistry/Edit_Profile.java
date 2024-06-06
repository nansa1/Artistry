package com.example.artistry;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

public class Edit_Profile extends AppCompatActivity {

    private String imageUrl;
    private Button btnlogin;

    private Uri imageUri;
    public TextView textView;
    private Button btn;
    private ImageView imageadded;
    private static final String IMAGE_DIRECTORY = "/VrSupMart";
    private EditText Fristname,Lastname,mnumber,profession;
    public ImageView imageView;
    public CardView cardView;
    private DatabaseReference mrootref;
    private FirebaseAuth maAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_edit_profile);

        Fristname = findViewById(R.id.email);
        Lastname = findViewById(R.id.last_name);
        profession = findViewById(R.id.profession);
        btnlogin = findViewById(R.id.btnlogin);
        imageView=(ImageView) findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Edit_Profile.this,Home.class);
                startActivity(intent);
            }
        });
        mrootref= FirebaseDatabase.getInstance().getReference();
        maAuth = FirebaseAuth.getInstance();
        btn = (Button) findViewById(R.id.btn);

        imageadded = (ImageView) findViewById(R.id.iv);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(Edit_Profile.this);
            }
        });

    }
    private void upload() {

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading....");
        pd.show();

        if(imageUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference("Profileimage").child(System.currentTimeMillis() +"."+getFileExtension(imageUri));

            StorageTask uploadtask  = filePath.putFile(imageUri);
            uploadtask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri = task.getResult();
                    imageUrl = downloadUri.toString();



                    HashMap<String ,Object> map =new HashMap<>();
                    String name = Fristname.getText().toString().trim()+" "+Lastname.getText().toString().trim();

                    map.put("name",name);
                    map.put("bio",profession.getText().toString().trim());
                    map.put("imageurl",imageUrl);


                    mrootref.child("Users").child(maAuth.getCurrentUser().getUid()).updateChildren(map);

                    pd.dismiss();
                    startActivity(new Intent(Edit_Profile.this,Home.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(Edit_Profile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            pd.dismiss();
            Toast.makeText(Edit_Profile.this, "No image was selected!", Toast.LENGTH_SHORT).show();
        }

    }

    private String getFileExtension(Uri uri) {

        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(uri));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            imageadded.setImageURI(imageUri);

        }else {
            Toast.makeText(Edit_Profile.this, "Try again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Edit_Profile.this,Home.class));
            finish();
        }

    }


}