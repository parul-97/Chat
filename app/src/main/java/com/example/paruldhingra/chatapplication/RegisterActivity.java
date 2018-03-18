package com.example.paruldhingra.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout mDisplayName,mEmail,mPassword;
    private TextInputEditText mEditDisplayName,mEditEmail,mEditPassword;
    private Button mRegister;
    private Toolbar mToolbar;
    private TextView mRegisterText;
    private ProgressDialog mProgess;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mToolbar = (Toolbar)findViewById(R.id.mainpageToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgess = new ProgressDialog(this);

        //Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mRegisterText = (TextView)findViewById(R.id.registertext);
        mDisplayName = (TextInputLayout)findViewById(R.id.textInputLayoutName);
        mEmail = (TextInputLayout)findViewById(R.id.textInputLayoutEmail);
        mPassword = (TextInputLayout)findViewById(R.id.textInputLayoutPassword);

        mEditDisplayName = (TextInputEditText)findViewById(R.id.textInputEditTextName);
        mEditEmail = (TextInputEditText) findViewById(R.id.textInputEditTextEmail);
        mEditPassword = (TextInputEditText)findViewById(R.id.textInputEditTextPassword);

        mRegister = (Button)findViewById(R.id.register);
        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String display_name = mDisplayName.getEditText().getText().toString();
                String email = mEmail.getEditText().getText().toString();
                String password = mPassword.getEditText().getText().toString();

                Toast.makeText(getApplicationContext(),display_name+email+password,Toast.LENGTH_SHORT).show();
                if(!TextUtils.isEmpty(display_name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
                {
                    mProgess.setTitle("Registering User");
                    mProgess.setMessage("Please wait while we create your Account!");
                    mProgess.setCanceledOnTouchOutside(false);
                    mProgess.show();
                    registerUser(display_name,email,password);
                }

            }
        });

    }

    private void registerUser(final String display_name, String email, String password )
    {
        mAuth.createUserWithEmailAndPassword(email, password)

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();
                            mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String,String> usermap = new HashMap<String, String>();
                            usermap.put("name",display_name);
                            usermap.put("status","Hi there,i'm using chat app");
                            usermap.put("image","default");
                            usermap.put("thumb_image","default");
                            usermap.put("device_token",deviceToken);
                            mDatabase.setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        mProgess.dismiss();
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        finish();

                                    }
                                }
                            });

                        }
                        else
                        {
                            mProgess.hide();
                            Toast.makeText(RegisterActivity.this,"Cannot Sign in.Please check the form and try again ",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }
}
