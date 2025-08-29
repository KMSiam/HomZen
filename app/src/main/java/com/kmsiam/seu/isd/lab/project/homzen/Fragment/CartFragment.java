package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kmsiam.seu.isd.lab.project.homzen.Adapter.CartAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.CartItem;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.CartManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.OnCartItemChangeListener {
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private ArrayList<CartItem> cartItems;
    private CartManager cartManager;
    private TextView totalPriceText;
    private View emptyCartContainer;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        
        // Initialize views
        cartRecyclerView = view.findViewById(R.id.cart_recycler_view);
        totalPriceText = view.findViewById(R.id.total_price);
        emptyCartContainer = view.findViewById(R.id.empty_cart_container);
        
        // Initialize cart manager
        cartManager = new CartManager(requireContext());
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup place order button
        view.findViewById(R.id.place_order_button).setOnClickListener(v -> placeOrder());
        
        return view;
    }

    private void setupRecyclerView() {
        // Get cart items from CartManager
        cartItems = cartManager.getCartItems();
        
        // Set up RecyclerView
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cartAdapter = new CartAdapter(getContext(), cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);
        
        // Show/hide empty cart message
        updateEmptyState();
        
        // Update total price
        updateTotalPrice();
    }

    private void updateEmptyState() {
        if (getView() == null) return;
        
        if (cartItems == null || cartItems.isEmpty()) {
            if (cartRecyclerView != null) cartRecyclerView.setVisibility(View.GONE);
            if (emptyCartContainer != null) emptyCartContainer.setVisibility(View.VISIBLE);
        } else {
            if (cartRecyclerView != null) cartRecyclerView.setVisibility(View.VISIBLE);
            if (emptyCartContainer != null) emptyCartContainer.setVisibility(View.GONE);
        }
        
        // Also update the checkout section visibility
        View checkoutContainer = getView().findViewById(R.id.checkout_container);
        if (checkoutContainer != null) {
            boolean hasItems = cartItems != null && !cartItems.isEmpty();
            checkoutContainer.setVisibility(hasItems ? View.VISIBLE : View.GONE);
        }
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.getTotalPrice();
        }
        
        // Format the price with BDT symbol
        String formattedPrice = String.format(Locale.US, "৳%.2f", total);
        totalPriceText.setText(formattedPrice);
    }

    @Override
    public void onQuantityChanged() {
        // Update total price when quantity changes
        updateTotalPrice();
    }

    @Override
    public void onItemRemoved(int position) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove Item")
                .setMessage("Are you sure you want to remove this item from your cart?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Remove item from cart
                    cartManager.removeFromCart(position);
                    
                    // Update the adapter
                    cartItems.remove(position);
                    cartAdapter.notifyItemRemoved(position);
                    
                    // Update UI
                    updateTotalPrice();
                    updateEmptyState();
                    
                    // Show toast
                    Toast.makeText(requireContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void placeOrder() {
        if (cartItems.isEmpty()) {
            Toast.makeText(requireContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Order")
                .setMessage("Are you sure you want to place this order?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Here you would typically process the order
                    // For now, we'll just clear the cart and show a success message
//                    cartManager.clearCart();
//                    cartItems.clear();
//                    cartAdapter.notifyDataSetChanged();
//                    updateEmptyState();
//                    updateTotalPrice();
                    
                    Toast.makeText(requireContext(), "Placed order code doesn't implement right now!", Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh cart when fragment is resumed
        if (cartAdapter != null) {
            cartItems = cartManager.getCartItems();
            cartAdapter.updateItems(cartItems);
            updateTotalPrice();
            updateEmptyState();
        }
    }
}