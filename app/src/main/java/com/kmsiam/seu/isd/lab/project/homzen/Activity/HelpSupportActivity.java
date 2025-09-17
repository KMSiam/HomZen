package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class HelpSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        setupHelpOptions();
    }

    private void setupHelpOptions() {
        LinearLayout helpContainer = findViewById(R.id.helpContainer);
        
        addHelpOption(helpContainer, "FAQ", "Frequently Asked Questions", () -> {});
        addHelpOption(helpContainer, "Contact Us", "Get in touch with our team", this::contactUs);
        addHelpOption(helpContainer, "Live Chat", "Chat with support agent", () -> {});
        addHelpOption(helpContainer, "Report Issue", "Report a problem", () -> {});
        addHelpOption(helpContainer, "Terms & Conditions", "Read our terms", () -> {});
        addHelpOption(helpContainer, "Privacy Policy", "Our privacy policy", () -> {});
    }

    private void addHelpOption(LinearLayout container, String title, String description, Runnable action) {
        LinearLayout optionLayout = new LinearLayout(this);
        optionLayout.setOrientation(LinearLayout.HORIZONTAL);
        optionLayout.setPadding(32, 24, 32, 24);
        optionLayout.setBackgroundResource(R.drawable.rounded_background);
        optionLayout.setOnClickListener(v -> action.run());
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        optionLayout.setLayoutParams(params);

        LinearLayout textContainer = new LinearLayout(this);
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));

        TextView titleText = new TextView(this);
        titleText.setText(title);
        titleText.setTextSize(16);
        titleText.setTextColor(getResources().getColor(R.color.black));

        TextView descText = new TextView(this);
        descText.setText(description);
        descText.setTextSize(14);
        descText.setTextColor(getResources().getColor(R.color.teal_200));

        textContainer.addView(titleText);
        textContainer.addView(descText);
        optionLayout.addView(textContainer);

        container.addView(optionLayout);
    }

    private void contactUs() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@homzen.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "HomZen Support Request");
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }
}
