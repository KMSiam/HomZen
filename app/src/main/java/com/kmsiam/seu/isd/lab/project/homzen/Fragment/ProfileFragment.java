package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.Manifest;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.Activity.LoginActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    final static int LOCATION_PERMISSION_REQUEST = 100;
    private String userId = "USER_ID";

    // Views
    ImageView userProfilePic;
    TextView userName, userEmail;
    Button loginButton;
    FloatingActionButton btnEditProfile;
    LinearLayout guestView, btnAddress, btnShare;
    ScrollView loggedInView;

    // Firebase
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    static final int MAX_IMAGE_SIZE = 500; // Max width/height in pixels

    // Location
    private FusedLocationProviderClient fusedLocationClient;

    //Simple in-memory cache to prevent redundant reloads
    private boolean isUserDataLoaded = false;
    private String cachedName = null;
    private String cachedProfileImage = null;

    // Image Picker
    final ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                requireActivity().getContentResolver(), imageUri);
                        bitmap = resizeBitmap(bitmap);
                        String base64Image = bitmapToBase64(bitmap);
                        saveImageToFirestore(base64Image);
                        // Update cache + UI immediately
                        cachedProfileImage = base64Image;          // NEW
                        userProfilePic.setImageBitmap(bitmap);     // keep
                        isUserDataLoaded = true;                   // NEW
                    } catch (IOException e) {
                        Toast.makeText(getContext(), "Failed to load image", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Views
        userProfilePic = view.findViewById(R.id.userProfilePic);
        userName = view.findViewById(R.id.tvUserName);
        userEmail = view.findViewById(R.id.tvUserEmail);
        loginButton = view.findViewById(R.id.login_button);
        guestView = view.findViewById(R.id.guestView);
        loggedInView = view.findViewById(R.id.loggedInView);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnAddress = view.findViewById(R.id.btnAddress);
        btnShare = view.findViewById(R.id.btnShare);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Resolve userId once (if logged in)
        FirebaseUser current = auth.getCurrentUser();     // NEW
        if (current != null) userId = current.getUid();   // NEW

        // Button Click Listeners
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> pickImage());
        loginButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> logoutUser());

        btnAddress.setOnClickListener(v -> {              // FIX: use field reference
            FirebaseUser u = auth.getCurrentUser();       // NEW guard
            if (checkLocationPermission()) {
                showAddressDialog();
            }
        });

        btnShare.setOnClickListener(v -> {
            String appPackageName = requireContext().getPackageName(); // your app package
            String appLink = "App link : " + appPackageName;

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this app!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey! Check out this app: " + appLink);

            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });


        updateUI();
        return view;
    }

    private void pickImage() {
        imagePicker.launch(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
    }

    private Bitmap resizeBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        if (width > MAX_IMAGE_SIZE || height > MAX_IMAGE_SIZE) {
            float ratio = (float) width / height;
            if (ratio > 1) {
                width = MAX_IMAGE_SIZE;
                height = (int) (width / ratio);
            } else {
                height = MAX_IMAGE_SIZE;
                width = (int) (height * ratio);
            }
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }
        return bitmap;
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void saveImageToFirestore(String base64Image) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .update("profileImage", base64Image)
                    .addOnSuccessListener(aVoid -> Toast.makeText(getContext(), "Profile updated", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> {
                        // If user doc doesn't exist, create it (keeps your structure)
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("profileImage", base64Image);
                        db.collection("users").document(user.getUid()).set(userMap)
                                .addOnSuccessListener(x -> Toast.makeText(getContext(), "Profile created", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(err -> Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show());
                    });
        }
    }

    private void updateUI() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            guestView.setVisibility(View.GONE);
            loggedInView.setVisibility(View.VISIBLE);
            userEmail.setText(user.getEmail());

            // If we’ve already loaded once, reuse cached values (prevents reload on each visit)
            if (isUserDataLoaded) {
                applyCachedUserData();
                return;
            }

            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(document -> {
                        cachedName = document.getString("name");
                        cachedProfileImage = document.getString("profileImage");
                        applyCachedUserData();
                        isUserDataLoaded = true;
                    })
                    .addOnFailureListener(e -> {
                        // Still show email if anything fails
                        isUserDataLoaded = true; // prevent constant retries on tab reselect
                    });

        } else {
            guestView.setVisibility(View.VISIBLE);
            loggedInView.setVisibility(View.GONE);
            // Clear cache when logged out
            isUserDataLoaded = false;
            cachedName = null;
            cachedProfileImage = null;
        }
    }

    // NEW: apply cached name + image to views
    private void applyCachedUserData() {
        userName.setText(cachedName != null ? cachedName : "");
        if (cachedProfileImage != null) {
            try {
                byte[] decodedString = Base64.decode(cachedProfileImage, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                userProfilePic.setImageBitmap(decodedBitmap);
            } catch (Exception ignored) { }
        }
    }

    private void logoutUser() {
        auth.signOut();
        updateUI();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        requireActivity().finish();
    }

    // ✅ Method 1: Check Location Permission
    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return false;
        }
        return true;
    }

    private void getCurrentAddress(EditText etAddress) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                try {
                    Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(),
                            1
                    );
                    if (addresses != null && !addresses.isEmpty()) {
                        etAddress.setText(addresses.get(0).getAddressLine(0));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(requireContext(), "Location unavailable. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ✅ Method 3: Show Address Dialog
    private void showAddressDialog() {
        // Resolve fresh UID (in case user just logged in)
        FirebaseUser current = auth.getCurrentUser();             // NEW
        if (current == null) {                                    // NEW
            Toast.makeText(requireContext(), "Please login first", Toast.LENGTH_SHORT).show();
            return;
        }
        userId = current.getUid();                                // NEW

        Dialog dialog = new Dialog(requireContext(), R.style.TransparentDialog);
        dialog.setContentView(R.layout.dialog_current_location);
        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText etAddress = dialog.findViewById(R.id.location_editText_dialog);
        Button btnSave = dialog.findViewById(R.id.btnSave);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);
        MapView mapView = dialog.findViewById(R.id.mapView);

        mapView.onCreate(null);
        mapView.onResume();

        // 🔹 Step 1: Try to load saved address from Firestore
        db.collection("users").document(userId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("address")) {
                        etAddress.setText(doc.getString("address"));
                    } else {
                        // fallback to current GPS address
                        getCurrentAddress(etAddress);
                    }
                });

        // 🔹 Step 2: Setup Google Map
        mapView.getMapAsync(googleMap -> {
            googleMap.getUiSettings().setZoomControlsEnabled(true);

            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, 15));

                        Marker marker = googleMap.addMarker(new MarkerOptions()
                                .position(myLatLng)
                                .title("Your Location")
                                .draggable(true));

                        // Drag marker updates address
                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override public void onMarkerDragStart(Marker marker) {}
                            @Override public void onMarkerDrag(Marker marker) {}
                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                updateAddressFromLatLng(marker.getPosition(), etAddress);
                            }
                        });

                        // Tap map updates address
                        googleMap.setOnMapClickListener(pos -> {
                            marker.setPosition(pos);
                            updateAddressFromLatLng(pos, etAddress);
                        });
                    } else {
                        Toast.makeText(requireContext(), "Location unavailable. Open GPS.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 🔹 Step 3: Save/Update Firestore
        btnSave.setOnClickListener(v -> {
            String newAddress = etAddress.getText().toString().trim();
            if (!newAddress.isEmpty()) {
                db.collection("users").document(userId)
                        .update("address", newAddress)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Address saved!", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            // If doc doesn’t exist, create it
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("address", newAddress);
                            db.collection("users").document(userId).set(userMap)
                                    .addOnSuccessListener(x ->
                                            Toast.makeText(requireContext(), "Address saved!", Toast.LENGTH_SHORT).show());
                        });
            }
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        // ✅ Properly release MapView when the dialog closes (prevents leaks)
        dialog.setOnDismissListener(d -> {                        // NEW
            try {
                mapView.onPause();
                mapView.onDestroy();
                mapView.onLowMemory();
            } catch (Exception ignored) { }
        });

        dialog.show();
    }

    // ✅ Helper to update EditText from LatLng
    private void updateAddressFromLatLng(LatLng pos, EditText etAddress) {
        try {
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(pos.latitude, pos.longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                etAddress.setText(addresses.get(0).getAddressLine(0));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUI(); // will be cheap after first load due to cache flag
    }
}