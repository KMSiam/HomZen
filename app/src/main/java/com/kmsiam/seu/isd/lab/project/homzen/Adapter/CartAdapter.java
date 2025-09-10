package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
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
        void onItemRemoved();
    }

    public CartAdapter(Context context, ArrayList<CartItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    public void setOnCartItemChangeListener(OnCartItemChangeListener listener) {
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
        holder.bind(cartItems.get(position), position);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName, productType, productPrice, totalPrice, quantityText;
        MaterialButton decreaseButton, increaseButton, deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productType = itemView.findViewById(R.id.product_type);
            productPrice = itemView.findViewById(R.id.product_price);
            totalPrice = itemView.findViewById(R.id.total_price);
            quantityText = itemView.findViewById(R.id.quantity_text);
            decreaseButton = itemView.findViewById(R.id.decrease_quantity);
            increaseButton = itemView.findViewById(R.id.increase_quantity);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }

        public void bind(CartItem cartItem, int position) {
            // Set product details
            productImage.setImageResource(cartItem.getGrocery().getImage());
            productName.setText(cartItem.getGrocery().getName());
            productType.setText(cartItem.getGrocery().getType());
            
            // Format and display prices
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("bn", "BD"));
            formatter.setCurrency(java.util.Currency.getInstance("BDT"));
            
            double unitPriceValue = Double.parseDouble(cartItem.getGrocery().getPrice());
            double totalPriceValue = unitPriceValue * cartItem.getQuantity();
            
            productPrice.setText("৳" + cartItem.getGrocery().getPrice());
            totalPrice.setText("৳" + String.format("%.0f", totalPriceValue));
            quantityText.setText(String.valueOf(cartItem.getQuantity()));

            // Set up quantity controls
            decreaseButton.setOnClickListener(v -> {
                if (cartItem.getQuantity() > 1) {
                    cartItem.setQuantity(cartItem.getQuantity() - 1);
                    notifyItemChanged(position);
                    if (listener != null) {
                        listener.onQuantityChanged();
                    }
                }
            });

            increaseButton.setOnClickListener(v -> {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                notifyItemChanged(position);
                if (listener != null) {
                    listener.onQuantityChanged();
                }
            });

            // Set up delete button
            deleteButton.setOnClickListener(v -> {
                cartItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, cartItems.size());
                if (listener != null) {
                    listener.onItemRemoved();
                }
            });
        }
    }

    public double getTotalPrice() {
        double total = 0;
        for (CartItem item : cartItems) {
            double unitPrice = Double.parseDouble(item.getGrocery().getPrice());
            total += unitPrice * item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        cartItems.clear();
        notifyDataSetChanged();
    }
}
