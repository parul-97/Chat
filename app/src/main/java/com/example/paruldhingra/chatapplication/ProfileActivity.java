package com.example.paruldhingra.chatapplication;

import android.app.ProgressDialog;
import android.icu.text.DateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_image;
    private TextView profile_Name,profile_current_user,total_friends;
    private Button mRequestButton,mDeclineButton;
    private DatabaseReference mdatabase;
    private ProgressDialog mProgressdialog;
    private DatabaseReference mFriendrequestdatabase;
    private DatabaseReference mfrienddatabase;
    private DatabaseReference mNotificationdatabase;
    private FirebaseUser mCurrent_User;
    private String current_state;
    private DatabaseReference mRootref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final String user_id = getIntent().getStringExtra("userId");

        profile_image = (ImageView)findViewById(R.id.friend_image);
        profile_Name = (TextView)findViewById(R.id.display_Name);
        profile_current_user = (TextView)findViewById(R.id.current_user_status);
        total_friends = (TextView)findViewById(R.id.total_friends);
        mRequestButton = (Button)findViewById(R.id.send_friend_request);
        mDeclineButton = (Button)findViewById(R.id.decline_friend_request);
        mRootref = FirebaseDatabase.getInstance().getReference();
        mDeclineButton.setVisibility(View.INVISIBLE);
        mDeclineButton.setEnabled(false);
        current_state = "not friends";

        mProgressdialog = new ProgressDialog(this);
        mProgressdialog.setTitle("Loading User data");
        mProgressdialog.setMessage("Please wait while we load the user data");
        mProgressdialog.setCanceledOnTouchOutside(false);
        mProgressdialog.show();


        mFriendrequestdatabase = FirebaseDatabase.getInstance().getReference().child("Friend_request");

        mfrienddatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mCurrent_User = FirebaseAuth.getInstance().getCurrentUser();
        mNotificationdatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mdatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        mdatabase.keepSynced(true);
        mdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 String display_name = dataSnapshot.child("name").getValue().toString();
                String display_status = dataSnapshot.child("status").getValue().toString();
                String display_image = dataSnapshot.child("image").getValue().toString();

                profile_Name.setText(display_name);
                profile_current_user.setText(display_status);
                Picasso.with(ProfileActivity.this).load(display_image).placeholder(R.drawable.default1).into(profile_image);

                mFriendrequestdatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(user_id))
                        {
                           String req_type = dataSnapshot.child(user_id).child("req_type").getValue().toString();
                            if(req_type.equals("recieved"))
                            {
                                current_state = "req_recieved";
                                mRequestButton.setText("Accept Friend Request");
                                mDeclineButton.setVisibility(View.VISIBLE);
                                mDeclineButton.setEnabled(true);
                            }
                            else if(req_type.equals("sent"))
                            {
                                current_state = "req_sent";
                                mRequestButton.setText("cancel Friend Request");
                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            }

                        }
                        else
                        {
                            mfrienddatabase.child(mCurrent_User.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.hasChild(user_id))
                                    {
                                        current_state = "Friends";
                                        mRequestButton.setText(" Unfriend this person");
                                        mDeclineButton.setVisibility(View.INVISIBLE);
                                        mDeclineButton.setEnabled(false);

                                    }
                                    mProgressdialog.dismiss();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressdialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                mProgressdialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mRequestButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                mRequestButton.setEnabled(false);
                if(current_state.equals("not friends")) {

                    DatabaseReference newNotificationref = mRootref.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();
                    Map requestMap = new HashMap();
                    requestMap.put("Friend_request/" + mCurrent_User.getUid() + "/" + user_id + "/requestType", "sent");
                    requestMap.put("Friend_request/" + user_id + "/" + mCurrent_User.getUid() + "/requestType", "recieved");
                    HashMap<String, String> notificationsData = new HashMap<String, String>();
                    notificationsData.put("from", mCurrent_User.getUid());
                    notificationsData.put("type", "request");

                    requestMap.put("notifications/" + user_id + "/" + newNotificationId + mNotificationdatabase, notificationsData);
                    mRootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if(databaseError!=null)
                                {
                                    Toast.makeText(ProfileActivity.this,"There was some error in sending request",Toast.LENGTH_LONG).show();
                                }
                                mRequestButton.setEnabled(true);
                            current_state = "not_Friends";
                            mRequestButton.setText("Cancel Friend Request");
                        }
                    });
                }
                   if(current_state.equals("req_sent"))
                {
                    mFriendrequestdatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendrequestdatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mRequestButton.setEnabled(true);
                                    current_state = "not_Friends";
                                    mRequestButton.setText("Send Friend Request");
                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                    mDeclineButton.setEnabled(false);
                                }
                            });
                        }
                    });
                }
                if(current_state.equals("req_recieved"))
                {
                    final String current_Date  = DateFormat.getTimeInstance().format(new Date());
                    mfrienddatabase.child(mCurrent_User.getUid()).child(user_id).setValue(current_Date).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mfrienddatabase.child(user_id).child(mCurrent_User.getUid()).setValue(current_Date).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mfrienddatabase.child(mCurrent_User.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            mfrienddatabase.child(user_id).child(mCurrent_User.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    mRequestButton.setEnabled(true);
                                                    current_state = "Friends";
                                                    mRequestButton.setText(" Unfriend this person");
                                                    mDeclineButton.setVisibility(View.INVISIBLE);
                                                    mDeclineButton.setEnabled(false);
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });


                }
                if(current_state.equals("friends"))
                {
                    Map UnfriendMap = new HashMap();
                    UnfriendMap.put("Friends/" + mCurrent_User.getUid() + "/" + user_id , null);
                    UnfriendMap.put("Friends/" + user_id + "/" + mCurrent_User.getUid() , null);

                    mRootref.updateChildren(UnfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if(databaseError == null)
                            {
                                mRequestButton.setEnabled(true);
                                current_state = "not friends";
                                mRequestButton.setText("Send Friend Request");

                                mDeclineButton.setVisibility(View.INVISIBLE);
                                mDeclineButton.setEnabled(false);
                            }
                            else
                            {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this,error,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });


    }
}
