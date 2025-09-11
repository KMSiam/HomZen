package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.button.MaterialButton;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.CartAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.MainActivity;
import com.kmsiam.seu.isd.lab.project.homzen.Model.CartItem;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.CartManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private CartManager cartManager;
    private TextView totalPriceText, subtotalPriceText, cartItemCountText;
    private LinearLayout emptyCartContainer, loadingState;
    private View checkoutContainer;
    private MaterialButton placeOrderButton, browseProductsButton;
    
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private boolean hasLoadedFromFirestore = false;
    private static boolean hasMergedOnLogin = false;
    
    private static final double DELIVERY_FEE = 50.0;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        // Initialize views
        initializeViews(view);
        
        // Initialize cart manager
        cartManager = new CartManager(requireContext());
        
        // Setup immediately
        setupRecyclerView();
        setupButtons();
        
        // Merge carts only once per login session
        if (auth.getCurrentUser() != null && !hasMergedOnLogin) {
            loadAndMergeCartFromFirestore();
            hasMergedOnLogin = true;
        } else {
            updateUI();
        }
        
        return view;
    }

    private void initializeViews(View view) {
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        totalPriceText = view.findViewById(R.id.total_price);
        subtotalPriceText = view.findViewById(R.id.subtotal_price);
        cartItemCountText = view.findViewById(R.id.cart_item_count);
        emptyCartContainer = view.findViewById(R.id.empty_cart_container);
        loadingState = view.findViewById(R.id.loading_state);
        checkoutContainer = view.findViewById(R.id.checkout_container);
        placeOrderButton = view.findViewById(R.id.place_order_button);
        browseProductsButton = view.findViewById(R.id.browse_products_btn);
    }

    private void setupRecyclerView() {
        // Get cart items from CartManager
        cartItems = cartManager.getCartItems();
        
        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), cartItems);
        cartAdapter.setOnCartItemChangeListener(this);
        cartRecyclerView.setAdapter(cartAdapter);
        
        // Update UI
        updateUI();
    }

    private void setupButtons() {
        // Place order button
        placeOrderButton.setOnClickListener(v -> showPlaceOrderDialog());
        
        // Browse products button
        browseProductsButton.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.loadFrag(new GroceryFragment(), false);
            }
        });
    }

    private void showPlaceOrderDialog() {
        if (cartItems == null || cartItems.isEmpty()) {
            Toast.makeText(getContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        double total = calculateTotal();
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Order")
                .setMessage("Place order for ৳" + String.format("%.0f", total) + "?")
                .setPositiveButton("Place Order", (dialog, which) -> {
                    placeOrder();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void placeOrder() {
        // Check if user is logged in first
        if (auth.getCurrentUser() == null) {
            Toast.makeText(getContext(), "Please login to place order", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show loading on button
        placeOrderButton.setText("Placing Order...");
        placeOrderButton.setEnabled(false);
        
        // Save order to Firebase
        saveOrderToFirebase();
        
        // Simulate order placement
        new Handler().postDelayed(() -> {
            // Clear cart
            cartManager.clearCart();
            cartItems.clear();
            
            // Clear cart from Firestore
            clearCartFromFirestore();
            cartAdapter.notifyDataSetChanged();
            
            // Reset button
            placeOrderButton.setText("Place Order");
            placeOrderButton.setEnabled(true);
            
            // Update UI
            updateUI();
            
            // Show success message
            Toast.makeText(getContext(), "Order placed successfully!", Toast.LENGTH_LONG).show();
        }, 2000);
    }
    
    private void saveOrderToFirebase() {
        String userId = auth.getCurrentUser().getUid();
        
        // First get user's address, then create order
        db.collection("users").document(userId).get()
                .addOnSuccessListener(userDoc -> {
                    String deliveryAddress = userDoc.getString("address");
                    if (deliveryAddress == null || deliveryAddress.trim().isEmpty()) {
                        deliveryAddress = "Home Delivery"; // fallback
                    }
                    
                    // Create order data
                    Map<String, Object> order = new HashMap<>();
                    order.put("orderId", "ORD" + System.currentTimeMillis());
                    order.put("userId", userId);
                    order.put("orderDate", new java.util.Date());
                    order.put("status", "Confirmed");
                    order.put("subtotal", calculateSubtotal());
                    order.put("deliveryFee", DELIVERY_FEE);
                    order.put("total", calculateTotal());
                    order.put("deliveryAddress", deliveryAddress); // Add delivery address
                    
                    // Add cart items
                    ArrayList<Map<String, Object>> orderItems = new ArrayList<>();
                    for (CartItem item : cartItems) {
                        Map<String, Object> orderItem = new HashMap<>();
                        orderItem.put("name", item.getGrocery().getName());
                        orderItem.put("price", item.getGrocery().getPrice());
                        orderItem.put("quantity", item.getQuantity());
                        orderItem.put("image", item.getGrocery().getImage());
                        orderItems.add(orderItem);
                    }
                    order.put("items", orderItems);
                    
                    // Save to Firebase with better error handling
                    db.collection("users").document(userId)
                            .collection("orders").add(order)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(getContext(), "Order saved successfully!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Failed to save order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to get user address", Toast.LENGTH_SHORT).show();
                });
    }

    private void updateUI() {
        updateEmptyState();
        updateCartCount();
        updatePrices();
    }

    private void updateEmptyState() {
        if (cartItems == null || cartItems.isEmpty()) {
            emptyCartContainer.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            checkoutContainer.setVisibility(View.GONE);
        } else {
            emptyCartContainer.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            checkoutContainer.setVisibility(View.VISIBLE);
        }
    }

    private void updateCartCount() {
        if (cartItems != null) {
            int totalItems = 0;
            for (CartItem item : cartItems) {
                totalItems += item.getQuantity();
            }
            cartItemCountText.setText(totalItems + " items");
        } else {
            cartItemCountText.setText("0 items");
        }
    }

    private void updatePrices() {
        if (cartItems != null && !cartItems.isEmpty()) {
            double subtotal = calculateSubtotal();
            double total = subtotal + DELIVERY_FEE;
            
            subtotalPriceText.setText("৳" + String.format("%.0f", subtotal));
            totalPriceText.setText("৳" + String.format("%.0f", total));
        } else {
            subtotalPriceText.setText("৳0");
            totalPriceText.setText("৳" + String.format("%.0f", DELIVERY_FEE));
        }
    }

    private double calculateSubtotal() {
        double subtotal = 0;
        if (cartItems != null) {
            for (CartItem item : cartItems) {
                try {
                    double unitPrice = Double.parseDouble(item.getGrocery().getPrice().replaceAll("[^\\d.]", ""));
                    subtotal += unitPrice * item.getQuantity();
                } catch (NumberFormatException e) {
                    // Skip invalid price items
                }
            }
        }
        return subtotal;
    }

    private double calculateTotal() {
        return calculateSubtotal() + DELIVERY_FEE;
    }

    private void showLoadingState() {
        loadingState.setVisibility(View.VISIBLE);
        cartRecyclerView.setVisibility(View.GONE);
        emptyCartContainer.setVisibility(View.GONE);
        checkoutContainer.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        loadingState.setVisibility(View.GONE);
        updateUI();
    }

    // Public method to refresh cart from external sources
    public void refreshCart() {
        if (cartManager != null) {
            ArrayList<CartItem> updatedItems = cartManager.getCartItems();
            if (cartItems != null) {
                cartItems.clear();
                cartItems.addAll(updatedItems);
                
                if (cartAdapter != null) {
                    cartAdapter.notifyDataSetChanged();
                }
            }
            updateUI();
            
            // Save to Firestore if user is logged in
            if (auth.getCurrentUser() != null) {
                saveCartToFirestore();
            }
        }
    }

    @Override
    public void onQuantityChanged() {
        // Update prices when quantity changes
        updatePrices();
        updateCartCount();
        
        // Save updated cart to CartManager
        cartManager.saveCartItems(cartItems);
        
        // Save to Firestore for logged-in users
        if (auth.getCurrentUser() != null) {
            saveCartToFirestore();
        }
    }

    @Override
    public void onItemRemoved() {
        // Update UI when item is removed
        updateUI();
        
        // Save updated cart to CartManager
        cartManager.saveCartItems(cartItems);
        
        // Save to Firestore for logged-in users
        if (auth.getCurrentUser() != null) {
            saveCartToFirestore();
        }
        
        Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Don't automatically save to prevent clearing Firestore data
        // Cart will be saved when items are actually modified
    }

    @Override
    public void onResume() {
        super.onResume();
        // Just refresh UI, don't reload from Firestore
        refreshCart();
    }
    
    private void loadAndMergeCartFromFirestore() {
        String userId = auth.getCurrentUser().getUid();
        showLoadingState();
        
        // Save current local cart items before loading from Firestore
        ArrayList<CartItem> localCartItems = new ArrayList<>(cartItems);
        
        db.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    try {
                        ArrayList<CartItem> firestoreItems = new ArrayList<>();
                        
                        // Get items from Firestore
                        for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            try {
                                CartItem item = document.toObject(CartItem.class);
                                if (item != null && item.getGrocery() != null) {
                                    firestoreItems.add(item);
                                }
                            } catch (Exception e) {
                                // Skip invalid items
                            }
                        }
                        
                        // Merge local cart with Firestore cart
                        cartItems.clear();
                        
                        // Add Firestore items first
                        cartItems.addAll(firestoreItems);
                        
                        // Merge local items (avoid duplicates)
                        for (CartItem localItem : localCartItems) {
                            boolean exists = false;
                            for (CartItem cartItem : cartItems) {
                                if (localItem.getGrocery().getName().equals(cartItem.getGrocery().getName())) {
                                    // Item exists, increase quantity
                                    cartItem.setQuantity(cartItem.getQuantity() + localItem.getQuantity());
                                    exists = true;
                                    break;
                                }
                            }
                            if (!exists) {
                                // Add new item from local cart
                                cartItems.add(localItem);
                            }
                        }
                        
                        // Save merged cart
                        cartManager.saveCartItems(cartItems);
                        saveCartToFirestore();
                        
                        if (cartAdapter != null) {
                            cartAdapter.notifyDataSetChanged();
                        }
                        
                        hideLoadingState();
                    } catch (Exception e) {
                        hideLoadingState();
                        refreshCart();
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoadingState();
                    refreshCart();
                });
    }
    
    private void saveCartToFirestore() {
        if (auth.getCurrentUser() == null || cartItems == null) return;
        
        try {
            String userId = auth.getCurrentUser().getUid();
            
            // Use batch operation for efficiency
            db.collection("users").document(userId)
                    .collection("cart")
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        try {
                            // Use batch for atomic operations
                            com.google.firebase.firestore.WriteBatch batch = db.batch();
                            
                            // Delete existing items
                            for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                batch.delete(document.getReference());
                            }
                            
                            // Add current cart items
                            for (CartItem item : cartItems) {
                                if (item != null && item.getGrocery() != null) {
                                    com.google.firebase.firestore.DocumentReference newDoc = 
                                        db.collection("users").document(userId).collection("cart").document();
                                    batch.set(newDoc, item);
                                }
                            }
                            
                            // Commit batch
                            batch.commit()
                                .addOnFailureListener(e -> {
                                    if (getContext() != null) {
                                        Toast.makeText(getContext(), "Failed to sync cart", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            
                        } catch (Exception e) {
                            if (getContext() != null) {
                                Toast.makeText(getContext(), "Cart sync error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Failed to access cart data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            // Silent fail for save operation
        }
    }
    
    private void clearCartFromFirestore() {
        if (auth.getCurrentUser() == null) return;
        
        String userId = auth.getCurrentUser().getUid();
        db.collection("users").document(userId)
                .collection("cart")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (com.google.firebase.firestore.QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        document.getReference().delete();
                    }
                });
    }
}
