package com.example.paruldhingra.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mEmail1,mPassword1;
    private TextInputEditText mEditEmail1,mEditPassword1;
    private Button mLogin;
    private Toolbar mToolbar1;
    private TextView mLoginText;
    private DatabaseReference mUserDatabase;
    private ProgressDialog mProgess1;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        mToolbar1 = (Toolbar)findViewById(R.id.mainpageToolbar);
        setSupportActionBar(mToolbar1);
        getSupportActionBar().setTitle("LOGIN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgess1 = new ProgressDialog(this);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mLoginText = (TextView)findViewById(R.id.logintext);
        mEmail1 = (TextInputLayout)findViewById(R.id.textInputLayoutEmail12);
        mPassword1 = (TextInputLayout)findViewById(R.id.textInputLayoutPassword1);

        mEditEmail1 = (TextInputEditText) findViewById(R.id.textInputEditTextEmail12);
        mEditPassword1 = (TextInputEditText)findViewById(R.id.textInputEditTextPassword1);
        mLogin = (Button)findViewById(R.id.login);
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email1 = mEditEmail1.getText().toString();
                String password1 = mEditPassword1.getText().toString();

                if(!TextUtils.isEmpty(email1) || !TextUtils.isEmpty(password1))
                {
                    mProgess1.setTitle("Logging In");
                    mProgess1.setMessage("Please wait while we check your credentials!");
                    mProgess1.setCanceledOnTouchOutside(false);
                    mProgess1.show();
                    LoginUser(email1,password1);
                }
            }
        });


    }

    private void LoginUser(String email1,String password1)
    {
       mAuth.signInWithEmailAndPassword(email1, password1).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    mProgess1.dismiss();
                    String current_user_id = mAuth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent i =new Intent(LoginActivity.this,MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                            finish();
                        }
                    });


                }
                else
                {
                    mProgess1.hide();
                    Toast.makeText(LoginActivity.this,"Cannot Sign in.Please check the form and try again",Toast.LENGTH_SHORT).show();
                }
           }
       });
    }
}
