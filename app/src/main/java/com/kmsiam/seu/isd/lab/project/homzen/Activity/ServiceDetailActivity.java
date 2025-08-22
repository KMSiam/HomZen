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

        // Get data from intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String desc = getIntent().getStringExtra("desc");
        String imageUrl = getIntent().getStringExtra("imageUrl"); // Get URL instead of resource ID

        // Set values
        topAppBar.setTitle(name);
        txtTitle.setText(name);
        txtPrice.setText(price);
        txtDesc.setText(desc);

        // Load image using Glide
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgService);

        // Back button
        topAppBar.setNavigationOnClickListener(v -> onBackPressed());

        btnContinue.setOnClickListener(v -> {
            // later add booking code or firebase request
        });
    }
}