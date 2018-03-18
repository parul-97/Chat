package com.example.paruldhingra.chatapplication;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.auth.ui.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersActivity extends AppCompatActivity {

    private RecyclerView rc;
    private Toolbar mToolbar;
    private DatabaseReference mUsersDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("ALL USERS");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        rc = (RecyclerView) findViewById(R.id.rc);
        rc.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<users, usersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<users, usersViewHolder>( users.class, R.layout.users_single_layout,
                usersViewHolder.class,
                mUsersDatabase) {
                @Override

                protected void populateViewHolder (usersViewHolder viewHolder, users model,
                final int position){

                    viewHolder.setName(model.getName());
                    viewHolder.setStatus(model.getStatus());
                    viewHolder.setUserImage(model.getImage(),getApplicationContext());
                    viewHolder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(UsersActivity.this,ProfileActivity.class);
                            String user_id = getRef(position).getKey();
                            i.putExtra("userId",user_id);
                            startActivity(i);
                        }
                    });


            }
            };
            rc.setAdapter(firebaseRecyclerAdapter);

        }

        public static class usersViewHolder extends RecyclerView.ViewHolder {

            View mView;
            CircleImageView userImageview;
            TextView userNameView;
            RelativeLayout relativeLayout;

            public usersViewHolder(View itemView) {
                super(itemView);
                mView = itemView;

                relativeLayout=(RelativeLayout)itemView.findViewById(R.id.users_relative);
            }
            public void setName(String name)
            {
                userNameView = (TextView)mView.findViewById(R.id.users_name);
                userNameView.setText(name);
            }
            public void setStatus(String status)
            {
                TextView userStatus = (TextView)mView.findViewById(R.id.users_status);
                userStatus.setText(status);
            }
            public void setUserImage(String thumb_image, Context cntxt)
            {
                userImageview = (CircleImageView)mView.findViewById(R.id.circle_image);

                if(thumb_image==null)
                    Log.d("error","ko");
                else
                Picasso.with(cntxt).load(thumb_image).placeholder(R.drawable.default1).into(userImageview);
            }
        }
    }

