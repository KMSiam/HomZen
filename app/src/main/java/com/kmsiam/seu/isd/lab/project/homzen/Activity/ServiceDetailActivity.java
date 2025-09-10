package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ServiceDetailActivity extends AppCompatActivity {

    private int quantity = 1;
    private double basePrice = 0;
    private String selectedDate = "";
    private String selectedTime = "";
    
    // Firebase
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    
    // Views
    private TextView txtQuantity, txtSelectedDate, txtSelectedTime;
    private TextView txtPriceBreakdown, txtTotalAmount;
    private ImageView btnDecreaseQty, btnIncreaseQty;
    private LinearLayout btnSelectDate, btnSelectTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_detail);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        ImageView imgService = findViewById(R.id.imgServiceDetail);
        TextView txtTitle = findViewById(R.id.txtServiceTitle);
        TextView txtPrice = findViewById(R.id.txtServicePrice);
        TextView txtDesc = findViewById(R.id.txtServiceDetails);
        Button btnBookService = findViewById(R.id.btnBookService);

        // Booking controls
        txtQuantity = findViewById(R.id.txtQuantity);
        btnDecreaseQty = findViewById(R.id.btnDecreaseQty);
        btnIncreaseQty = findViewById(R.id.btnIncreaseQty);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnSelectTime = findViewById(R.id.btnSelectTime);
        txtSelectedDate = findViewById(R.id.txtSelectedDate);
        txtSelectedTime = findViewById(R.id.txtSelectedTime);
        
        // Price summary
        txtPriceBreakdown = findViewById(R.id.txtPriceBreakdown);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);

        // Provider views
        ImageView imgProvider = findViewById(R.id.imgProvider);
        TextView txtProviderName = findViewById(R.id.txtProviderName);
        TextView txtProviderRating = findViewById(R.id.txtProviderRating);
        TextView txtProviderDesc = findViewById(R.id.txtProviderDesc);
        TextView txtProviderPhone = findViewById(R.id.txtProviderPhone);
        MaterialButton btnProviderPhone = findViewById(R.id.btnProviderPhone);
        TextView txtProviderEmail = findViewById(R.id.txtProviderEmail);
        MaterialButton btnProviderEmail = findViewById(R.id.btnProviderEmail);

        // Get service data from intent
        String name = getIntent().getStringExtra("name");
        String price = getIntent().getStringExtra("price");
        String desc = getIntent().getStringExtra("desc");
        int imageResId = getIntent().getIntExtra("imageResId", R.drawable.ic_launcher_foreground);

        // Extract base price from price string
        if (price != null) {
            try {
                basePrice = Double.parseDouble(price.replaceAll("[^\\d.]", ""));
            } catch (NumberFormatException e) {
                basePrice = 0;
            }
        }

        // Get provider data from intent
        String providerName = getIntent().getStringExtra("providerName");
        String providerDesc = getIntent().getStringExtra("providerDesc");
        String providerPhone = getIntent().getStringExtra("providerPhone");
        String providerEmail = getIntent().getStringExtra("providerEmail");
        String providerRating = getIntent().getStringExtra("providerRating");
        int providerImageResId = getIntent().getIntExtra("providerImageResId", R.drawable.service_probider);

        // Set service values with null checks
        topAppBar.setTitle(name != null ? name : "Service Details");
        txtTitle.setText(name != null ? name : "Service");
        txtPrice.setText(price != null ? price : "৳0");
        txtDesc.setText(desc != null ? desc : "No description available");

        // Load service image from drawable resource
        imgService.setImageResource(imageResId);

        // Set provider data with null checks
        txtProviderName.setText(providerName != null ? providerName : "Unknown Provider");
        txtProviderRating.setText(providerRating != null ? providerRating : "⭐ 0.0");
        txtProviderDesc.setText(providerDesc != null ? providerDesc : "No description available");
        txtProviderPhone.setText(providerPhone != null ? providerPhone : "");
        txtProviderEmail.setText(providerEmail != null ? providerEmail : "");

        // Phone click listener
        btnProviderPhone.setOnClickListener(v -> {
            if (providerPhone != null && !providerPhone.isEmpty()) {
                Intent iPhone = new Intent(Intent.ACTION_DIAL);
                iPhone.setData(Uri.parse("tel:" + providerPhone));
                startActivity(iPhone);
            }
        });
        
        // Email click listener
        btnProviderEmail.setOnClickListener(v -> {
            if (providerEmail != null && !providerEmail.isEmpty()) {
                Intent iEmail = new Intent(Intent.ACTION_SEND);
                iEmail.setType("message/rfc822");
                iEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{providerEmail});
                iEmail.putExtra(Intent.EXTRA_SUBJECT, "Need more info about " + (name != null ? name : "service"));
                iEmail.putExtra(Intent.EXTRA_TEXT, "Hello,\n\nI am interested in your " + (name != null ? name : "service") + " service. Could you please provide more information?\n\nThank you!");
                startActivity(Intent.createChooser(iEmail, "Choose an Email client"));
            }
        });

        // Load provider image from drawable resource
        Glide.with(this)
             .load(providerImageResId)
             .diskCacheStrategy(DiskCacheStrategy.ALL)
             .placeholder(R.drawable.ic_launcher_foreground)
             .into(imgProvider);

        // Quantity controls
        btnDecreaseQty.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                updateQuantityAndPrice();
            }
        });

        btnIncreaseQty.setOnClickListener(v -> {
            if (quantity < 24) { // Max 24 hours
                quantity++;
                updateQuantityAndPrice();
            }
        });

        // Date selection
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Time selection
        btnSelectTime.setOnClickListener(v -> showTimePicker());

        // Back button
        topAppBar.setNavigationOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        // Book service button
        btnBookService.setOnClickListener(v -> {
            if (validateBooking()) {
                bookService(name);
            }
        });

        // Initialize price display
        updateQuantityAndPrice();
    }

    private void updateQuantityAndPrice() {
        txtQuantity.setText(String.valueOf(quantity));
        txtPriceBreakdown.setText("৳" + String.format("%.0f", basePrice) + " per hour × " + quantity);
        double total = basePrice * quantity;
        txtTotalAmount.setText("৳" + String.format("%.0f", total));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                Calendar selectedCalendar = Calendar.getInstance();
                selectedCalendar.set(year, month, dayOfMonth);
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                selectedDate = dateFormat.format(selectedCalendar.getTime());
                txtSelectedDate.setText(selectedDate);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        
        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
                txtSelectedTime.setText(selectedTime);
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        );
        timePickerDialog.show();
    }

    private boolean validateBooking() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Please login to book service", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void bookService(String serviceName) {
        double totalAmount = basePrice * quantity;
        String userId = auth.getCurrentUser().getUid();
        
        // Create booking data
        Map<String, Object> booking = new HashMap<>();
        booking.put("bookingId", "BKG" + System.currentTimeMillis());
        booking.put("userId", userId);
        booking.put("serviceName", serviceName != null ? serviceName : "Unknown Service");
        booking.put("providerName", getIntent().getStringExtra("providerName"));
        booking.put("duration", quantity);
        booking.put("pricePerHour", basePrice);
        booking.put("totalAmount", totalAmount);
        booking.put("bookingDate", selectedDate);
        booking.put("bookingTime", selectedTime);
        booking.put("createdDate", new Date());
        booking.put("status", "Confirmed");
        
        // Save to Firebase
        db.collection("users").document(userId)
                .collection("bookings").add(booking)
                .addOnSuccessListener(documentReference -> {
                    String bookingDetails = "Service: " + (serviceName != null ? serviceName : "Unknown") + 
                                           "\nDuration: " + quantity + " hours" +
                                           "\nDate: " + selectedDate +
                                           "\nTime: " + selectedTime +
                                           "\nTotal: ৳" + String.format("%.0f", totalAmount);
                    
                    Toast.makeText(this, "Booking Confirmed!\n" + bookingDetails, Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
