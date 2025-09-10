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

import com.google.android.material.button.MaterialButton;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.CartAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.MainActivity;
import com.kmsiam.seu.isd.lab.project.homzen.Model.CartItem;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.CartManager;

import java.util.ArrayList;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private CartManager cartManager;
    private TextView totalPriceText, subtotalPriceText, cartItemCountText;
    private LinearLayout emptyCartContainer, loadingState;
    private View checkoutContainer;
    private MaterialButton placeOrderButton, browseProductsButton;
    
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
        // Show loading on button
        placeOrderButton.setText("Placing Order...");
        placeOrderButton.setEnabled(false);
        
        // Simulate order placement
        new Handler().postDelayed(() -> {
            // Clear cart
            cartManager.clearCart();
            cartItems.clear();
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
        }
    }

    @Override
    public void onQuantityChanged() {
        // Update prices when quantity changes
        updatePrices();
        updateCartCount();
        
        // Save updated cart to CartManager
        cartManager.saveCartItems(cartItems);
    }

    @Override
    public void onItemRemoved() {
        // Update UI when item is removed
        updateUI();
        
        // Save updated cart to CartManager
        cartManager.saveCartItems(cartItems);
        
        Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart items when fragment becomes visible
        refreshCart();
    }
}
