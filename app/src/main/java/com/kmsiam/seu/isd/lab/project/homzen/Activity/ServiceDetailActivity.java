package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
        LinearLayout btnProviderPhone = findViewById(R.id.btnProviderPhone);
        TextView txtProviderEmail = findViewById(R.id.txtProviderEmail);
        LinearLayout btnProviderEmail = findViewById(R.id.btnProviderEmail);

        // Get service data from intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String desc = getIntent().getStringExtra("desc");
        int imageResId = getIntent().getIntExtra("imageResId", R.drawable.ic_launcher_foreground);

        // Get provider data from intent
        String providerName = getIntent().getStringExtra("providerName");
        String providerDesc = getIntent().getStringExtra("providerDesc");
        String providerPhone = getIntent().getStringExtra("providerPhone");
        String providerEmail = getIntent().getStringExtra("providerEmail");
        String providerRating = getIntent().getStringExtra("providerRating");
        int providerImageResId = getIntent().getIntExtra("providerImageResId", R.drawable.service_probider);

        // Set service values
        topAppBar.setTitle(name);
        txtTitle.setText(name);
        txtPrice.setText(price);
        txtDesc.setText(desc);

        // Load service image from drawable resource
        imgService.setImageResource(imageResId);

        // Set provider data
        txtProviderName.setText(providerName);
        txtProviderRating.setText(providerRating);
        txtProviderDesc.setText(providerDesc);
        txtProviderPhone.setText(providerPhone);
        
        // Phone click listener
        btnProviderPhone.setOnLongClickListener(v -> {
            Intent iPhone = new Intent(Intent.ACTION_DIAL);
            iPhone.setData(Uri.parse("tel:" + providerPhone));
            startActivity(iPhone);
            return true;
        });
        
        // Email click listener
        txtProviderEmail.setText(providerEmail);
        btnProviderEmail.setOnLongClickListener(v -> {
            Intent iEmail = new Intent(Intent.ACTION_SEND);
            iEmail.setType("message/rfc822");
            iEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{providerEmail});
            iEmail.putExtra(Intent.EXTRA_SUBJECT, "Need more info about " + name);
            iEmail.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI am interested in your " + name + " service. Could you please provide more information?\n\nThank you!");
            startActivity(Intent.createChooser(iEmail, "Choose an Email client"));
            return true;
        });

        // Load provider image from drawable resource
        Glide.with(this)
             .load(providerImageResId)
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .placeholder(R.drawable.ic_launcher_foreground)
             .into(imgProvider);

        // Back button
        topAppBar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        btnContinue.setOnClickListener(v -> {
            // later add booking code or firebase request
        });
    }
}