package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences("HomZenSettings", MODE_PRIVATE);
        
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());

        setupSettings();
    }

    private void setupSettings() {
        LinearLayout settingsContainer = findViewById(R.id.settingsContainer);
        
        addSwitchSetting(settingsContainer, "Push Notifications", "Receive order updates", "notifications", true);
        addSwitchSetting(settingsContainer, "Email Notifications", "Receive promotional emails", "email_notifications", false);
        addSwitchSetting(settingsContainer, "Location Services", "Enable location tracking", "location", true);
        addSwitchSetting(settingsContainer, "Auto-Save Cart", "Save cart items automatically", "auto_save_cart", true);
        addSwitchSetting(settingsContainer, "Dark Mode", "Use dark theme", "dark_mode", false);
    }

    private void addSwitchSetting(LinearLayout container, String title, String description, String key, boolean defaultValue) {
        LinearLayout settingLayout = new LinearLayout(this);
        settingLayout.setOrientation(LinearLayout.HORIZONTAL);
        settingLayout.setPadding(32, 24, 32, 24);
        settingLayout.setBackgroundResource(R.drawable.rounded_background);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 16);
        settingLayout.setLayoutParams(params);

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

        Switch settingSwitch = new Switch(this);
        settingSwitch.setChecked(prefs.getBoolean(key, defaultValue));
        settingSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> 
            prefs.edit().putBoolean(key, isChecked).apply());

        textContainer.addView(titleText);
        textContainer.addView(descText);
        settingLayout.addView(textContainer);
        settingLayout.addView(settingSwitch);

        container.addView(settingLayout);
    }
}
