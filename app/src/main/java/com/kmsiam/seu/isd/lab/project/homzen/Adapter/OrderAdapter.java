package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmsiam.seu.isd.lab.project.homzen.Activity.OrderDetailsActivity;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Order;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    
    private Context context;
    private ArrayList<Order> orderList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault());
    
    public OrderAdapter(Context context, ArrayList<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        holder.orderId.setText(order.getOrderId());
        holder.orderDate.setText(dateFormat.format(order.getOrderDate()));
        holder.orderStatus.setText(order.getStatus());
        holder.orderTotal.setText("৳" + String.format("%.0f", order.getTotal()));
        
        // Show item count
        int itemCount = order.getItems() != null ? order.getItems().size() : 0;
        holder.itemCount.setText(itemCount + " items");
        
        // Show delivery address
        String address = order.getDeliveryAddress();
        if (address != null && !address.trim().isEmpty()) {
            holder.deliveryAddress.setText(address);
        } else {
            holder.deliveryAddress.setText("Home Delivery");
        }
        
        // Set status background color
        switch (order.getStatus()) {
            case "Complete":
                holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.success));
                break;
            case "Confirmed":
                holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
                break;
            case "Processing":
                holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.info));
                break;
            case "Cancelled":
                holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.error));
                break;
            default:
                holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.warning));
                break;
        }
        holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.white));
        
        // Handle click on action section
        holder.actionSection.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("orderId", order.getOrderId());
            context.startActivity(intent);
        });
    }
    
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, orderStatus, orderTotal, itemCount, deliveryAddress;
        LinearLayout actionSection;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderTotal = itemView.findViewById(R.id.order_total);
            itemCount = itemView.findViewById(R.id.item_count);
            deliveryAddress = itemView.findViewById(R.id.delivery_address);
            
            // Find the action section (the bottom LinearLayout with "Tap to view details")
            actionSection = itemView.findViewById(R.id.actionSection);
            if (actionSection == null) {
                // If ID doesn't exist, find by the last LinearLayout in the card
                ViewGroup parent = (ViewGroup) itemView;
                if (parent.getChildCount() > 0) {
                    ViewGroup cardContent = (ViewGroup) parent.getChildAt(0);
                    if (cardContent.getChildCount() > 2) {
                        actionSection = (LinearLayout) cardContent.getChildAt(cardContent.getChildCount() - 1);
                    }
                }
            }
        }
    }
}
