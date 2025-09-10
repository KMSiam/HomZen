package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.OrderAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Order;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;
import java.util.Date;

public class MyOrdersActivity extends AppCompatActivity {
    
    private RecyclerView ordersRecyclerView;
    private OrderAdapter orderAdapter;
    private ArrayList<Order> orderList;
    private LinearLayout emptyState;
    private TextView emptyText;
    
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        
        initViews();
        setupRecyclerView();
        loadOrders();
    }
    
    private void initViews() {
        ordersRecyclerView = findViewById(R.id.orders_recycler_view);
        emptyState = findViewById(R.id.empty_state);
        emptyText = findViewById(R.id.empty_text);
        
        // Back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(this, orderList);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ordersRecyclerView.setAdapter(orderAdapter);
    }
    
    private void loadOrders() {
        if (auth.getCurrentUser() == null) {
            showEmptyState("Please login to view orders");
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .collection("orders")
                .orderBy("orderDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Order order = new Order();
                            order.setOrderId(document.getString("orderId"));
                            order.setStatus(document.getString("status"));
                            
                            // Safe double parsing
                            Double total = document.getDouble("total");
                            order.setTotal(total != null ? total : 0.0);
                            
                            // Safe date parsing
                            Date orderDate = document.getDate("orderDate");
                            order.setOrderDate(orderDate != null ? orderDate : new Date());
                            
                            // Safe items parsing
                            ArrayList items = (ArrayList) document.get("items");
                            order.setItems(items != null ? items : new ArrayList<>());
                            
                            orderList.add(order);
                        } catch (Exception e) {
                            // Skip invalid orders
                        }
                    }
                    
                    if (orderList.isEmpty()) {
                        showEmptyState("No orders found");
                    } else {
                        showOrders();
                    }
                    
                    orderAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    showEmptyState("Failed to load orders");
                });
    }
    
    private void showEmptyState(String message) {
        emptyState.setVisibility(View.VISIBLE);
        ordersRecyclerView.setVisibility(View.GONE);
        emptyText.setText(message);
    }
    
    private void showOrders() {
        emptyState.setVisibility(View.GONE);
        ordersRecyclerView.setVisibility(View.VISIBLE);
    }
}
