package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        
        // Set status background color
        if ("Complete".equals(order.getStatus())) {
            holder.orderStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.white));
        } else if ("Confirmed".equals(order.getStatus())) {
            holder.orderStatus.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.orderStatus.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.orderStatus.setTextColor(context.getResources().getColor(android.R.color.white));
        }
    }
    
    @Override
    public int getItemCount() {
        return orderList.size();
    }
    
    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderDate, orderStatus, orderTotal, itemCount;
        
        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.order_id);
            orderDate = itemView.findViewById(R.id.order_date);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderTotal = itemView.findViewById(R.id.order_total);
            itemCount = itemView.findViewById(R.id.item_count);
        }
    }
}
