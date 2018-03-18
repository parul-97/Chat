package com.example.paruldhingra.chatapplication;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class StatusActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private TextInputLayout mstatus;
    private Button button;
    private DatabaseReference mstatusDatabase;
    private FirebaseUser CurrentUser;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = CurrentUser.getUid();
        
        mstatusDatabase = getInstance().getReference().child("users").child(current_uid);
        mtoolbar = (Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        String status_value =  getIntent().getStringExtra("status_value");
        mstatus = (TextInputLayout)findViewById(R.id.text1);
        button = (Button)findViewById(R.id.button6);

        mstatus.getEditText().setText(status_value);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while we save changes");
                mProgress.show();

                String status  = mstatus.getEditText().getText().toString();
                mstatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            mProgress.dismiss();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"there was some error in making changes",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }




}
