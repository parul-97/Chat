package com.example.paruldhingra.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.R.attr.bitmap;
import static android.R.attr.thumb;


public class SettingsActivity extends AppCompatActivity {


    private TextView mName,mstatus;
    private FirebaseUser mUser;
    private DatabaseReference mUserDatabase;
    private CircleImageView mDisplayImage;


    private Button mStatusButton;
    private Button butt;
    private static final int GALLERY_PICK = 1;

    //Storage firebase
        private StorageReference mImageStorage;
        private ProgressDialog mProgressDialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();

            CropImage.activity(imageUri).setAspectRatio(1,1).start(this);
            //Toast.makeText(SettingsActivity.this, imageUri ,Toast.LENGTH_LONG).show();



        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK) {
                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading image...");
                mProgressDialog.setMessage("Please wait while we upload and process the image");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();
                Uri resultUri = result.getUri();
                File thumb_Filepath = new File(resultUri.getPath());
                String user_id = mUser.getUid();
                Bitmap thumb_bitmap = null;
                try {
                     thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_Filepath);
                }catch(IOException e)
                {
                    e.printStackTrace();
                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] thumb_byte = baos.toByteArray();

                StorageReference localref = mImageStorage.child("profile_images").child(user_id+".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("profile_images").child("thumbs").child(user_id+".jpg");
                localref.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if(task.isSuccessful()){
                            //Toast.makeText(SettingsActivity.this,"Working",Toast.LENGTH_LONG).show();
                           @SuppressWarnings("VisibleForTests") final String download_url = task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                                    @SuppressWarnings("VisibleForTests") String thumb_download_url = task.getResult().getDownloadUrl().toString();
                                    if(task.isSuccessful()) {
                                        Map update_hashmap = new HashMap();
                                        update_hashmap.put("image",thumb_download_url );
                                        update_hashmap.put("thumb_image",download_url);
                                        mUserDatabase.updateChildren(update_hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                        @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressDialog.dismiss();
                                                    Toast.makeText(SettingsActivity.this, "Success uploading", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                                    }




                        else
                                    {Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
                                    mProgressDialog.dismiss();

                                }
                            }
                        });

                    }
                        else
                        {
                            Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }

                    }
                });

            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView)findViewById(R.id.circle_image);
        mName = (TextView)findViewById(R.id.textView5);
        mstatus = (TextView)findViewById(R.id.textView6);


        mStatusButton = (Button)findViewById(R.id.button5);
        butt = (Button)findViewById(R.id.button4);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUUID = mUser.getUid();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mUUID);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               String name = dataSnapshot.child("name").getValue().toString();

                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();


                mName.setText(name);
                mstatus.setText(status);
                if(!image.equals("default")) {
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.default1).into(mDisplayImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.default1).into(mDisplayImage);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status_value = mstatus.getText().toString();
                Intent i = new Intent(SettingsActivity.this,StatusActivity.class);
                i.putExtra("status_value",status_value);
                startActivity(i);
            }
        });
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,"SELECT IMAGE"),GALLERY_PICK);
               /* CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);*/


            }
        });
    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(20);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

}
