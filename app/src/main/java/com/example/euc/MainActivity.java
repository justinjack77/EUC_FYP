package com.example.euc;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    private ImageView addImageView,homeImageView,peopleOutlineImageView,settingsImageView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find image views by id
        addImageView = findViewById(R.id.add_image_view);
        homeImageView = findViewById(R.id.home_image_view);
        peopleOutlineImageView = findViewById(R.id.people_outline_image_view);
        settingsImageView = findViewById(R.id.settings_image_view);
        // Find the ImageViews by their IDs
        ImageView facebookIcon = findViewById(R.id.facebook_icon);
        ImageView twitterIcon = findViewById(R.id.twitter_icon);
        ImageView instagramIcon = findViewById(R.id.instagram_icon);
        ImageView whatsappIcon = findViewById(R.id.whatsapp_icon);

// Set onClick listeners for each ImageView
        facebookIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Facebook app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                startActivity(intent);
            }
        });

        twitterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Twitter app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com"));
                startActivity(intent);
            }
        });

        instagramIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open Instagram app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com"));
                startActivity(intent);
            }
        });

        whatsappIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Open WhatsApp app or website
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.whatsapp.com"));
                startActivity(intent);
            }
        });


        // Set click listeners for each image view
        addImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new AddDeviceFragment());
            }
        });

        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle home image view click
                Intent intent= new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });

        peopleOutlineImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle people outline image view click
                Intent intent= new Intent(MainActivity.this, UsersListActivity.class);
                startActivity(intent);
            }
        });

        settingsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle settings image view click
                replaceFragment(new SettingFragment());
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}