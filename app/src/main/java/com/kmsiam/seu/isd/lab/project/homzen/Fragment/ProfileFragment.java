package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.Activity.EditProfileActivity;
import com.kmsiam.seu.isd.lab.project.homzen.Activity.LoginActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileFragment extends Fragment {
    final static int LOCATION_PERMISSION_REQUEST = 100;
    private String userId = "USER_ID";

    // Views
    ImageView userProfilePic;
    TextView userName, userEmail, totalOrdersText, totalSpentText;
    Button loginButton;
    LinearLayout guestView, btnShare;
    ScrollView loggedInView;

    // Firebase
    final FirebaseAuth auth = FirebaseAuth.getInstance();
    final FirebaseFirestore db = FirebaseFirestore.getInstance();
    static final int MAX_IMAGE_SIZE = 500; // Max width/height in pixels

    //Simple in-memory cache to prevent redundant reloads
    private boolean isUserDataLoaded = false;
    private String cachedName = null;
    private String cachedProfileImage = null;

    // Image Picker
    private ActivityResultLauncher<Intent> editProfileLauncher;
    
    // Initialize edit profile launcher
    {
        editProfileLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Profile was updated, refresh the data
                    isUserDataLoaded = false;
                    cachedName = null;
                    cachedProfileImage = null;
                    updateUI();
                }
            });
    }
    
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
        totalOrdersText = view.findViewById(R.id.tvTotalOrders);
        totalSpentText = view.findViewById(R.id.tvTotalSpent);
        loginButton = view.findViewById(R.id.login_button);
        guestView = view.findViewById(R.id.guestView);
        loggedInView = view.findViewById(R.id.loggedInView);
        btnShare = view.findViewById(R.id.btnShare);

        // Resolve userId once (if logged in)
        FirebaseUser current = auth.getCurrentUser();
        if (current != null) userId = current.getUid();

        // Button Click Listeners
        view.findViewById(R.id.btnChangeProfilePic).setOnClickListener(v -> pickImage());
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> 
            editProfileLauncher.launch(new Intent(getActivity(), EditProfileActivity.class)));
        view.findViewById(R.id.btnOrders).setOnClickListener(v -> 
            startActivity(new Intent(getActivity(), com.kmsiam.seu.isd.lab.project.homzen.Activity.MyOrdersActivity.class)));
        loginButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), LoginActivity.class)));
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> logoutUser());

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
                        loadUserStats(); // Load stats after user data
                        isUserDataLoaded = true;
                    })
                    .addOnFailureListener(e -> {
                        // Still show email if anything fails
                        loadUserStats(); // Still try to load stats
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
    
    private void loadUserStats() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            totalOrdersText.setText("0");
            totalSpentText.setText("৳0");
            return;
        }
        
        db.collection("users").document(user.getUid())
                .collection("orders")
                .whereEqualTo("status", "Complete")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int totalOrders = queryDocumentSnapshots.size();
                    double totalSpent = 0.0;
                    
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Double orderTotal = document.getDouble("total");
                        if (orderTotal != null) {
                            totalSpent += orderTotal;
                        }
                    }
                    
                    totalOrdersText.setText(String.valueOf(totalOrders));
                    totalSpentText.setText("৳" + String.format("%.0f", totalSpent));
                })
                .addOnFailureListener(e -> {
                    totalOrdersText.setText("0");
                    totalSpentText.setText("৳0");
                });
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

    @Override
    public void onStart() {
        super.onStart();
        updateUI(); // will be cheap after first load due to cache flag
    }
}
