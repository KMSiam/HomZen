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
        txtProviderPhone.setText("+880 1785 954300");
        btnProviderPhone.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent iPhone = new Intent(Intent.ACTION_DIAL);
                iPhone.setData(Uri.parse("tel:" + txtProviderPhone.getText().toString().trim()));
                startActivity(iPhone);
                return true;
            }
        });
        txtProviderEmail.setText("contact@cleanpro.com");
        btnProviderEmail.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent iEmail = new Intent(Intent.ACTION_SEND);
                iEmail.setType("message/rfc822");
                iEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{txtProviderEmail.getText().toString().trim()});
                iEmail.putExtra(Intent.EXTRA_SUBJECT, "Need more info about "+ txtTitle.getText().toString().trim());
                iEmail.putExtra(Intent.EXTRA_TEXT, "Body of the email");
                startActivity(Intent.createChooser(iEmail, "Choose an Email client"));
                return true;
            }
        });

        // Load provider image (replace with actual provider image URL)
        Glide.with(this)
                .load("https://cdn.pixabay.com/photo/2016/07/20/23/31/girl-1531575_1280.jpg")
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
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