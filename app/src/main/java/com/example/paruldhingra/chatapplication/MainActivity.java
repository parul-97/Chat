package com.example.paruldhingra.chatapplication;

import android.content.ClipData;
import android.content.Intent;
import com.google.firebase.auth.FirebaseAuth;

import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private TabLayout mTablayout;
    private SectionPagerAdapter mSectionPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar)findViewById(R.id.mainpageToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("CHAT APP");
        mAuth = FirebaseAuth.getInstance();

        mViewPager = (ViewPager)findViewById(R.id.main_viewpager);
        mTablayout = (TabLayout)findViewById(R.id.main_tabs);
        mSectionPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionPagerAdapter);
        mTablayout.setupWithViewPager(mViewPager);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.main_logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this,StartActivity.class);
            startActivity(i);
            finish();

        }

        if(item.getItemId() == R.id.main_account_settings)
        {
            Intent i = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(i);
        }

        if(item.getItemId() == R.id.main_all_users)
        {
            Intent i = new Intent(MainActivity.this,UsersActivity.class);
            startActivity(i);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.main_menu,menu);
            return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent i = new Intent(MainActivity.this,StartActivity.class);
            startActivity(i);
            finish();
        }
    }



}
