package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private TextInputEditText etName, etEmail, etPhone, etAddress;
    private ImageView profileImage, btnBack;
    private MaterialButton btnSave, btnChangePhoto, btnGetLocation;
    
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String userId;
    
    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private ActivityResultLauncher<String> locationPermissionLauncher;
    private GoogleMap mMap;
    private Dialog locationDialog;
    private TextInputEditText etCurrentLocation;
    private Uri selectedImageUri;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_edit_profile);
            
            initViews();
            initFirebase();
            setupImagePicker();
            setupLocationPermission();
            loadUserData();
            setupClickListeners();
        } catch (Exception e) {
            Toast.makeText(this, "Error loading profile: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
    
    private void initViews() {
        try {
            etName = findViewById(R.id.etName);
            etEmail = findViewById(R.id.etEmail);
            etPhone = findViewById(R.id.etPhone);
            etAddress = findViewById(R.id.etAddress);
            profileImage = findViewById(R.id.profileImage);
            btnBack = findViewById(R.id.btnBack);
            btnSave = findViewById(R.id.btnSave);
            btnChangePhoto = findViewById(R.id.btnChangePhoto);
            btnGetLocation = findViewById(R.id.btnGetLocation);
            
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize views: " + e.getMessage());
        }
    }
    
    private void initFirebase() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            userId = user.getUid();
            etEmail.setText(user.getEmail());
        } else {
            finish();
        }
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        profileImage.setImageURI(selectedImageUri);
                    }
                }
            }
        );
    }
    
    private void setupLocationPermission() {
        locationPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if (isGranted) {
                    showLocationDialog();
                } else {
                    Toast.makeText(this, "Location permission required for GPS feature", Toast.LENGTH_SHORT).show();
                }
            }
        );
    }
    
    private void loadUserData() {
        db.collection("users").document(userId).get()
            .addOnSuccessListener(doc -> {
                if (doc.exists()) {
                    etName.setText(doc.getString("name"));
                    etPhone.setText(doc.getString("phone"));
                    etAddress.setText(doc.getString("address"));
                    
                    String profileImageBase64 = doc.getString("profileImage");
                    if (profileImageBase64 != null && !profileImageBase64.isEmpty()) {
                        byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                        Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profileImage.setImageBitmap(bitmap);
                    }
                }
            })
            .addOnFailureListener(e -> 
                Toast.makeText(this, "Failed to load profile data", Toast.LENGTH_SHORT).show()
            );
    }
    
    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());
        
        btnChangePhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
        
        btnGetLocation.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
                showLocationDialog();
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });
        
        btnSave.setOnClickListener(v -> saveProfile());
    }
    
    private void showLocationDialog() {
        locationDialog = new Dialog(this, R.style.TransparentDialog);
        locationDialog.setContentView(R.layout.dialog_current_location);
        locationDialog.getWindow().setLayout(-1, -2);
        
        etCurrentLocation = locationDialog.findViewById(R.id.location_editText_dialog);
        MaterialButton btnSave = locationDialog.findViewById(R.id.btnSave);
        MaterialButton btnCancel = locationDialog.findViewById(R.id.btnCancel);
        
        btnSave.setOnClickListener(v -> {
            String location = etCurrentLocation.getText().toString().trim();
            if (!location.isEmpty()) {
                etAddress.setText(location);
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
    
    private void saveProfile() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        
        if (name.isEmpty()) {
            etName.setError("Name is required");
            return;
        }
        
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", name);
        userMap.put("phone", phone);
        userMap.put("address", address);
        
        // Handle image if selected
        if (selectedImageUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                // Resize bitmap to reduce memory usage
                bitmap = resizeBitmap(bitmap, 300, 300);
                
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                userMap.put("profileImage", imageBase64);
            } catch (Exception e) {
                Toast.makeText(this, "Error processing image", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        db.collection("users").document(userId)
            .set(userMap)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            })
            .addOnFailureListener(e -> 
                Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
            );
    }
    
    private Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        
        float scaleWidth = ((float) maxWidth) / width;
        float scaleHeight = ((float) maxHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);
        
        int newWidth = Math.round(width * scale);
        int newHeight = Math.round(height * scale);
        
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }
}
