package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmsiam.seu.isd.lab.project.homzen.Model.CartItem;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private ArrayList<CartItem> cartItems;
    private OnCartItemChangeListener listener;

    public interface OnCartItemChangeListener {
        void onQuantityChanged();
        void onItemRemoved(int position);
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems, OnCartItemChangeListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem, position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateItems(ArrayList<CartItem> newItems) {
        cartItems = new ArrayList<>(newItems);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView productImage;
        private TextView productName, totalPrice, unitPrice, quantityText;
        private ImageButton decreaseButton, increaseButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            totalPrice = itemView.findViewById(R.id.total_price);
            unitPrice = itemView.findViewById(R.id.unit_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            decreaseButton = itemView.findViewById(R.id.decrease_button);
            increaseButton = itemView.findViewById(R.id.increase_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(CartItem cartItem, int position) {
            // Set product details
            productImage.setImageResource(cartItem.getGrocery().getImage());
            productName.setText(cartItem.getGrocery().getName());
            
            // Parse the price string (remove non-numeric characters except decimal point)
            String priceStr = cartItem.getGrocery().getPrice().replaceAll("[^\\d.]", "");
            double unitPriceValue;
            try {
                unitPriceValue = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                unitPriceValue = 0.0;
            }
            
            // Set unit price with BDT symbol and format to 2 decimal places
            unitPrice.setText(String.format(Locale.US, "৳%.2f | Piece", unitPriceValue));
            
            // Set quantity
            quantityText.setText(String.valueOf(cartItem.getQuantity()));
            
            // Set total price for this item
            double total = cartItem.getTotalPrice();
            totalPrice.setText(String.format("৳%.2f", total));

            // Set up quantity buttons
            decreaseButton.setOnClickListener(v -> {
                int currentQuantity = cartItem.getQuantity();
                if (currentQuantity > 1) {
                    cartItem.setQuantity(currentQuantity - 1);
                    quantityText.setText(String.valueOf(cartItem.getQuantity()));
                    totalPrice.setText(String.format("৳%.2f", cartItem.getTotalPrice()));
                    if (listener != null) {
                        listener.onQuantityChanged();
                    }
                }
            });

            increaseButton.setOnClickListener(v -> {
                int currentQuantity = cartItem.getQuantity();
                cartItem.setQuantity(currentQuantity + 1);
                quantityText.setText(String.valueOf(cartItem.getQuantity()));
                totalPrice.setText(String.format("৳%.2f", cartItem.getTotalPrice()));
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            });

            // Set up delete button
            deleteButton.setOnClickListener(v -> {
                if (listener != null) {
                    int currentPosition = getAdapterPosition();
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        listener.onItemRemoved(currentPosition);
                    }
                }
            });
        }
    }
}
