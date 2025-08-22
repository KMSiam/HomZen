package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.MaterialToolbar;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class ServiceDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        ImageView imgService = findViewById(R.id.imgServiceDetail);
        TextView txtTitle = findViewById(R.id.txtServiceTitle);
        TextView txtPrice = findViewById(R.id.txtServicePrice);
        TextView txtDesc = findViewById(R.id.txtServiceDetails);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Provider views
        ImageView imgProvider = findViewById(R.id.imgProvider);
        TextView txtProviderName = findViewById(R.id.txtProviderName);
        TextView txtProviderRating = findViewById(R.id.txtProviderRating);
        TextView txtProviderDesc = findViewById(R.id.txtProviderDesc);
        TextView txtProviderPhone = findViewById(R.id.txtProviderPhone);
        TextView txtProviderEmail = findViewById(R.id.txtProviderEmail);

        // Get service data from intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String desc = getIntent().getStringExtra("desc");
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Set service values
        topAppBar.setTitle(name);
        txtTitle.setText(name);
        txtPrice.setText("Starts from " + price);
        txtDesc.setText(desc);

        // Load service image
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgService);

        // Set provider data (you can get this from intent or database later)
        txtProviderName.setText("CleanPro Services");
        txtProviderRating.setText("⭐ 4.8 (120 reviews)");
        txtProviderDesc.setText("Professional cleaning service with 5+ years of experience.");
        txtProviderPhone.setText("+880 1712 345678");
        txtProviderEmail.setText("contact@cleanpro.com");

        // Load provider image (replace with actual provider image URL)
        Glide.with(this)
                .load("https://cdn.pixabay.com/photo/2016/07/20/23/31/girl-1531575_1280.jpg")
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProvider);

        // Back button
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        btnContinue.setOnClickListener(v -> {
            // later add booking code or firebase request
        });
    }
}