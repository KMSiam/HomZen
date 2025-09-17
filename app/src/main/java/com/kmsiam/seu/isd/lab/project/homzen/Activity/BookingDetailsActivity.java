package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Booking;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BookingDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private TextView bookingIdText, serviceNameText, providerNameText, bookingDateTimeText;
    private TextView createdDateText, durationText, totalAmountText, statusText, serviceAddressText;
    private TextView providerPhoneText, providerEmailText;
    private RadioGroup paymentMethodGroup;
    private MaterialButton checkoutButton, cancelBookingButton;
    private Button changeAddressButton;
    
    private Booking booking;
    private String currentServiceAddress;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault());
    
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private GoogleMap mMap;
    private Dialog locationDialog;
    private TextInputEditText etCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);
        
        initViews();
        setupLocationPermission();
        loadBookingData();
        loadUserAddress();
        setupPaymentMethods();
    }
    
    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        bookingIdText = findViewById(R.id.bookingIdText);
        serviceNameText = findViewById(R.id.serviceNameText);
        providerNameText = findViewById(R.id.providerNameText);
        bookingDateTimeText = findViewById(R.id.bookingDateTimeText);
        createdDateText = findViewById(R.id.createdDateText);
        durationText = findViewById(R.id.durationText);
        totalAmountText = findViewById(R.id.totalAmountText);
        statusText = findViewById(R.id.statusText);
        serviceAddressText = findViewById(R.id.serviceAddressText);
        providerPhoneText = findViewById(R.id.providerPhoneText);
        providerEmailText = findViewById(R.id.providerEmailText);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        checkoutButton = findViewById(R.id.checkoutButton);
        cancelBookingButton = findViewById(R.id.cancelBookingButton);
        changeAddressButton = findViewById(R.id.changeAddressButton);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        checkoutButton.setOnClickListener(v -> processCheckout());
        cancelBookingButton.setOnClickListener(v -> cancelBooking());
        changeAddressButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
                showChangeAddressDialog();
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });
    }
    
    private void setupPaymentMethods() {
        String[] paymentMethods = {"Cash on Delivery", "bKash", "Nagad", "Credit Card"};
        
        for (String method : paymentMethods) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(method);
            radioButton.setTextSize(16);
            radioButton.setPadding(16, 16, 16, 16);
            paymentMethodGroup.addView(radioButton);
        }
        
        // Select first option by default
        if (paymentMethodGroup.getChildCount() > 0) {
            ((RadioButton) paymentMethodGroup.getChildAt(0)).setChecked(true);
        }
    }
    
    private void setupLocationPermission() {
        locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    showChangeAddressDialog();
                } else {
                    Toast.makeText(this, "Location permission required for GPS feature", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void showChangeAddressDialog() {
        locationDialog = new Dialog(this, R.style.TransparentDialog);
        locationDialog.setContentView(R.layout.dialog_current_location);
        locationDialog.getWindow().setLayout(-1, -2);
        
        etCurrentLocation = locationDialog.findViewById(R.id.location_editText_dialog);
        MaterialButton btnSave = locationDialog.findViewById(R.id.btnSave);
        MaterialButton btnCancel = locationDialog.findViewById(R.id.btnCancel);
        
        // Pre-fill with current address
        if (currentServiceAddress != null && !currentServiceAddress.equals("No address saved")) {
            etCurrentLocation.setText(currentServiceAddress);
        }
        
        btnSave.setOnClickListener(v -> {
            String location = etCurrentLocation.getText().toString().trim();
            if (!location.isEmpty()) {
                currentServiceAddress = location;
                serviceAddressText.setText(location);
                Toast.makeText(this, "Service address updated", Toast.LENGTH_SHORT).show();
            }
            locationDialog.dismiss();
        });
        
        btnCancel.setOnClickListener(v -> locationDialog.dismiss());
        
        // Clean up fragment when dialog closes
        locationDialog.setOnDismissListener(dialog -> {
            SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.mapFragment);
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
            }
        });
        
        locationDialog.show();
        
        // Setup map after dialog is shown
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.mapFragment, mapFragment)
                    .commitAllowingStateLoss();
        }
        mapFragment.getMapAsync(this);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        
        // Enable location if permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            
            // Get current location and add marker
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    
                    // Add marker
                    mMap.addMarker(new MarkerOptions()
                            .position(currentLocation)
                            .title("Your Location"));
                    
                    // Move camera
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
                    
                    // Get address
                    updateAddressFromLocation(location.getLatitude(), location.getLongitude());
                }
            });
        }
        
        // Add map click listener
        mMap.setOnMapClickListener(latLng -> {
            // Clear existing markers
            mMap.clear();
            
            // Add new marker at clicked position
            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Selected Location"));
            
            // Update address from clicked location
            updateAddressFromLocation(latLng.latitude, latLng.longitude);
        });
    }
    
    private void updateAddressFromLocation(double latitude, double longitude) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String address = addresses.get(0).getAddressLine(0);
                etCurrentLocation.setText(address);
            }
        } catch (Exception e) {
            etCurrentLocation.setText("Unable to get address");
        }
    }
    
    private void loadUserAddress() {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String address = document.getString("address");
                        if (address != null && !address.trim().isEmpty()) {
                            currentServiceAddress = address;
                        } else {
                            currentServiceAddress = "No address saved";
                        }
                    }
                });
    }
    
    private void loadBookingData() {
        String bookingId = getIntent().getStringExtra("bookingId");
        if (bookingId == null) {
            finish();
            return;
        }
        
        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .collection("bookings")
                .whereEqualTo("bookingId", bookingId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        booking = queryDocumentSnapshots.getDocuments().get(0).toObject(Booking.class);
                        displayBookingDetails();
                    } else {
                        Toast.makeText(this, "Booking not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load booking", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    
    private void displayBookingDetails() {
        if (booking == null) return;
        
        bookingIdText.setText(booking.getBookingId());
        serviceNameText.setText(booking.getServiceName());
        providerNameText.setText(booking.getProviderName());
        bookingDateTimeText.setText(booking.getBookingDate() + " at " + booking.getBookingTime());
        createdDateText.setText(dateFormat.format(booking.getCreatedDate()));
        durationText.setText(booking.getDuration() + " hours");
        totalAmountText.setText("৳" + String.format("%.0f", booking.getTotalAmount()));
        statusText.setText(booking.getStatus());
        
        String address = booking.getServiceAddress();
        if (address != null && !address.trim().isEmpty()) {
            currentServiceAddress = address;
            serviceAddressText.setText(address);
        } else {
            serviceAddressText.setText(currentServiceAddress != null ? currentServiceAddress : "Home Service");
        }
        
        String phone = booking.getProviderPhone();
        providerPhoneText.setText(phone != null ? phone : "Not provided");
        
        String email = booking.getProviderEmail();
        providerEmailText.setText(email != null ? email : "Not provided");
        
        // Update button text and visibility based on booking status
        String status = booking.getStatus();
        if ("Complete".equals(status) || "Cancelled".equals(status) || "Processing".equals(status) || "Confirmed".equals(status)) {
            checkoutButton.setText("Re-Book");
            cancelBookingButton.setVisibility(android.view.View.GONE);
        } else {
            checkoutButton.setText("Checkout");
            cancelBookingButton.setText("Cancel Booking");
            cancelBookingButton.setVisibility(android.view.View.VISIBLE);
        }
        
        // Set status color at the very end
        if ("Complete".equals(status)) {
            statusText.setTextColor(0xFF00C853); // Green
        } else if ("Confirmed".equals(status)) {
            statusText.setTextColor(0xFF018786); // Teal
        } else if ("Processing".equals(status)) {
            statusText.setTextColor(0xFF2196F3); // Blue
        } else if ("Cancelled".equals(status)) {
            statusText.setTextColor(0xFFF44336); // Red
        } else {
            statusText.setTextColor(0xFFFF9800); // Orange
        }
    }
    
    private void processCheckout() {
        String status = booking.getStatus();
        
        if ("Complete".equals(status) || "Cancelled".equals(status) || "Processing".equals(status) || "Confirmed".equals(status)) {
            // Show confirmation dialog for re-booking
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Re-Booking")
                    .setMessage("Re-book " + booking.getServiceName() + " for ৳" + String.format("%.0f", booking.getTotalAmount()) + "?")
                    .setPositiveButton("Re-Book", (dialog, which) -> {
                        createReBooking();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // Show confirmation dialog for checkout
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Checkout")
                    .setMessage("Proceed with checkout for ৳" + String.format("%.0f", booking.getTotalAmount()) + "?")
                    .setPositiveButton("Checkout", (dialog, which) -> {
                        int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
                        if (selectedId == -1) {
                            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        RadioButton selectedMethod = findViewById(selectedId);
                        String paymentMethod = selectedMethod.getText().toString();
                        updateBookingPayment(paymentMethod, "Processing");
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    
    private void createReBooking() {
        if (auth.getCurrentUser() == null || booking == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        String newBookingId = "BKG" + System.currentTimeMillis();
        
        Map<String, Object> newBooking = new HashMap<>();
        newBooking.put("bookingId", newBookingId);
        newBooking.put("serviceName", booking.getServiceName());
        newBooking.put("providerName", booking.getProviderName());
        newBooking.put("duration", booking.getDuration());
        newBooking.put("pricePerHour", booking.getPricePerHour());
        newBooking.put("totalAmount", booking.getTotalAmount());
        newBooking.put("bookingDate", booking.getBookingDate());
        newBooking.put("bookingTime", booking.getBookingTime());
        newBooking.put("status", "Pending");
        newBooking.put("createdDate", new Date());
        newBooking.put("serviceAddress", currentServiceAddress != null ? currentServiceAddress : booking.getServiceAddress());
        newBooking.put("providerPhone", booking.getProviderPhone());
        newBooking.put("providerEmail", booking.getProviderEmail());
        
        db.collection("users").document(userId)
                .collection("bookings")
                .add(newBooking)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Re-booking placed successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Failed to place re-booking", Toast.LENGTH_SHORT).show());
    }
    
    private void updateBookingPayment(String paymentMethod, String newStatus) {
        if (auth.getCurrentUser() == null || booking == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", newStatus);
        updates.put("paymentMethod", paymentMethod);
        updates.put("serviceAddress", currentServiceAddress);
        
        db.collection("users").document(userId)
                .collection("bookings")
                .whereEqualTo("bookingId", booking.getBookingId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference()
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    booking.setStatus(newStatus);
                                    displayBookingDetails();
                                    String message = "Confirmed".equals(newStatus) ? "Booking confirmed successfully!" : "Booking updated successfully!";
                                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> 
                                    Toast.makeText(this, "Failed to update booking", Toast.LENGTH_SHORT).show());
                    }
                });
    }
    
    private void cancelBooking() {
        if (auth.getCurrentUser() == null || booking == null) return;
        
        new AlertDialog.Builder(this)
                .setTitle("Cancel Booking")
                .setMessage("Are you sure you want to cancel this booking?")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                    String userId = auth.getCurrentUser().getUid();
                    
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("status", "Cancelled");
                    
                    db.collection("users").document(userId)
                            .collection("bookings")
                            .whereEqualTo("bookingId", booking.getBookingId())
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                if (!queryDocumentSnapshots.isEmpty()) {
                                    queryDocumentSnapshots.getDocuments().get(0).getReference()
                                            .update(updates)
                                            .addOnSuccessListener(aVoid -> {
                                                booking.setStatus("Cancelled");
                                                displayBookingDetails();
                                                Toast.makeText(this, "Booking cancelled successfully!", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> 
                                                Toast.makeText(this, "Failed to cancel booking", Toast.LENGTH_SHORT).show());
                                }
                            });
                })
                .setNegativeButton("No", null)
                .show();
    }
}
