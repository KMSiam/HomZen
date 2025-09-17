package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Order;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    
    private TextView orderIdText, orderDateText, orderStatusText, orderTotalText;
    private TextView deliveryAddressText, itemsCountText;
    private LinearLayout itemsContainer;
    private RadioGroup paymentMethodGroup;
    private Button checkoutButton, changeAddressButton, cancelOrderButton;
    
    private Order order;
    private String currentDeliveryAddress;
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
        setContentView(R.layout.activity_order_details);
        
        initViews();
        setupLocationPermission();
        loadOrderData();
        loadUserAddress();
        setupPaymentMethods();
    }
    
    private void initViews() {
        findViewById(R.id.backButton).setOnClickListener(v -> finish());
        
        orderIdText = findViewById(R.id.orderIdText);
        orderDateText = findViewById(R.id.orderDateText);
        orderStatusText = findViewById(R.id.orderStatusText);
        orderTotalText = findViewById(R.id.orderTotalText);
        deliveryAddressText = findViewById(R.id.deliveryAddressText);
        itemsCountText = findViewById(R.id.itemsCountText);
        itemsContainer = findViewById(R.id.itemsContainer);
        paymentMethodGroup = findViewById(R.id.paymentMethodGroup);
        checkoutButton = findViewById(R.id.checkoutButton);
        changeAddressButton = findViewById(R.id.changeAddressButton);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        
        checkoutButton.setOnClickListener(v -> processCheckout());
        cancelOrderButton.setOnClickListener(v -> cancelOrder());
        changeAddressButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED) {
                showChangeAddressDialog();
            } else {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        });
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
        if (currentDeliveryAddress != null && !currentDeliveryAddress.equals("No address saved")) {
            etCurrentLocation.setText(currentDeliveryAddress);
        }
        
        btnSave.setOnClickListener(v -> {
            String location = etCurrentLocation.getText().toString().trim();
            if (!location.isEmpty()) {
                currentDeliveryAddress = location;
                deliveryAddressText.setText(location);
                Toast.makeText(this, "Delivery address updated", Toast.LENGTH_SHORT).show();
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
    
    private void loadOrderData() {
        String orderId = getIntent().getStringExtra("orderId");
        if (orderId == null) {
            finish();
            return;
        }
        
        if (auth.getCurrentUser() == null) {
            finish();
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .collection("orders")
                .whereEqualTo("orderId", orderId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        order = queryDocumentSnapshots.getDocuments().get(0).toObject(Order.class);
                        displayOrderDetails();
                    } else {
                        Toast.makeText(this, "Order not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to load order", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    
    private void displayOrderDetails() {
        if (order == null) return;
        
        orderIdText.setText(order.getOrderId());
        orderDateText.setText(dateFormat.format(order.getOrderDate()));
        orderStatusText.setText(order.getStatus());
        
        // Set status color
        String status = order.getStatus();
        if ("Complete".equals(status)) {
            orderStatusText.setTextColor(ContextCompat.getColor(this, R.color.success));
        } else if ("Confirmed".equals(status)) {
            orderStatusText.setTextColor(ContextCompat.getColor(this, R.color.teal_700));
        } else if ("Processing".equals(status)) {
            orderStatusText.setTextColor(ContextCompat.getColor(this, R.color.info));
        } else if ("Cancelled".equals(status)) {
            orderStatusText.setTextColor(ContextCompat.getColor(this, R.color.error));
        } else {
            orderStatusText.setTextColor(ContextCompat.getColor(this, R.color.warning));
        }
        orderTotalText.setText("৳" + String.format("%.0f", order.getTotal()));
        
        // Update button text based on order status
        if ("Complete".equals(order.getStatus()) || "Processing".equals(order.getStatus())) {
            checkoutButton.setText("Re-Order");
            cancelOrderButton.setVisibility(android.view.View.GONE);
        } else if ("Cancelled".equals(order.getStatus())) {
            checkoutButton.setText("Re-Order");
            cancelOrderButton.setVisibility(android.view.View.GONE);
        } else {
            checkoutButton.setText("Checkout");
            cancelOrderButton.setVisibility(android.view.View.VISIBLE);
        }
        
        // Display items
        ArrayList<Object> items = order.getItems();
        if (items != null) {
            itemsCountText.setText(items.size() + " items");
            displayOrderItems(items);
        }
    }
    
    private void displayOrderItems(ArrayList<Object> items) {
        itemsContainer.removeAllViews();
        
        for (Object item : items) {
            if (item instanceof Map) {
                Map<String, Object> itemMap = (Map<String, Object>) item;
                addItemView(itemMap);
            }
        }
    }
    
    private void addItemView(Map<String, Object> item) {
        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 16, 0, 16);
        
        TextView itemName = new TextView(this);
        itemName.setText(String.valueOf(item.get("name")));
        itemName.setTextSize(14);
        itemName.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
        
        TextView itemPrice = new TextView(this);
        itemPrice.setText("৳" + item.get("price"));
        itemPrice.setTextSize(14);
        itemPrice.setTextColor(getResources().getColor(R.color.teal_700));
        
        itemLayout.addView(itemName);
        itemLayout.addView(itemPrice);
        itemsContainer.addView(itemLayout);
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
                            currentDeliveryAddress = address;
                            deliveryAddressText.setText(address);
                        } else {
                            currentDeliveryAddress = "No address saved";
                            deliveryAddressText.setText("No address saved");
                        }
                    }
                });
    }
    
    private void setupPaymentMethods() {
        String[] methods = {"Cash on Delivery", "bKash", "Nagad", "Credit Card"};
        
        for (String method : methods) {
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
    
    private void processCheckout() {
        if ("Complete".equals(order.getStatus()) || "Cancelled".equals(order.getStatus())) {
            // Show confirmation dialog for re-order
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Re-Order")
                    .setMessage("Re-order items for ৳" + String.format("%.0f", order.getTotal()) + "?")
                    .setPositiveButton("Re-Order", (dialog, which) -> {
                        createReOrder();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        } else {
            // Show confirmation dialog for checkout
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirm Checkout")
                    .setMessage("Proceed with checkout for ৳" + String.format("%.0f", order.getTotal()) + "?")
                    .setPositiveButton("Checkout", (dialog, which) -> {
                        // Handle normal checkout
                        int selectedId = paymentMethodGroup.getCheckedRadioButtonId();
                        if (selectedId == -1) {
                            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        
                        RadioButton selectedMethod = findViewById(selectedId);
                        String paymentMethod = selectedMethod.getText().toString();
                        updateOrderPayment(paymentMethod);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }
    
    private void createReOrder() {
        if (auth.getCurrentUser() == null || order == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        String newOrderId = "ORD" + System.currentTimeMillis();
        
        Map<String, Object> newOrder = new HashMap<>();
        newOrder.put("orderId", newOrderId);
        newOrder.put("items", order.getItems());
        newOrder.put("total", order.getTotal());
        newOrder.put("status", "Pending");
        newOrder.put("orderDate", new java.util.Date());
        newOrder.put("deliveryAddress", currentDeliveryAddress);
        
        db.collection("users").document(userId)
                .collection("orders")
                .add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Re-order placed successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> 
                    Toast.makeText(this, "Failed to place re-order", Toast.LENGTH_SHORT).show());
    }
    
    private void cancelOrder() {
        if (auth.getCurrentUser() == null || order == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "Cancelled");
        
        db.collection("users").document(userId)
                .collection("orders")
                .whereEqualTo("orderId", order.getOrderId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference()
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Order cancelled successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> 
                                    Toast.makeText(this, "Failed to cancel order", Toast.LENGTH_SHORT).show());
                    }
                });
    }
    
    private void updateOrderPayment(String paymentMethod) {
        if (auth.getCurrentUser() == null || order == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        
        Map<String, Object> updates = new HashMap<>();
        updates.put("paymentMethod", paymentMethod);
        updates.put("status", "Processing");
        updates.put("deliveryAddress", currentDeliveryAddress);
        
        db.collection("users").document(userId)
                .collection("orders")
                .whereEqualTo("orderId", order.getOrderId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().get(0).getReference()
                                .update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(this, "Order updated successfully!", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> 
                                    Toast.makeText(this, "Failed to update order", Toast.LENGTH_SHORT).show());
                    }
                });
    }
}
