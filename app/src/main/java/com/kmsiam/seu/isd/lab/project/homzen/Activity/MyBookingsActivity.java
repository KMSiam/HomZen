package com.kmsiam.seu.isd.lab.project.homzen.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.BookingAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Booking;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;
import java.util.Date;

public class MyBookingsActivity extends AppCompatActivity {
    
    private RecyclerView bookingsRecyclerView;
    private BookingAdapter bookingAdapter;
    private ArrayList<Booking> bookingList;
    private LinearLayout emptyState;
    private TextView emptyText;
    
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);
        
        initViews();
        setupRecyclerView();
        loadBookings();
    }
    
    private void initViews() {
        bookingsRecyclerView = findViewById(R.id.bookings_recycler_view);
        emptyState = findViewById(R.id.empty_state);
        emptyText = findViewById(R.id.empty_text);
        
        // Back button
        findViewById(R.id.back_button).setOnClickListener(v -> finish());
    }
    
    private void setupRecyclerView() {
        bookingList = new ArrayList<>();
        bookingAdapter = new BookingAdapter(this, bookingList);
        bookingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingsRecyclerView.setAdapter(bookingAdapter);
    }
    
    private void loadBookings() {
        if (auth.getCurrentUser() == null) {
            showEmptyState("Please login to view bookings");
            return;
        }
        
        String userId = auth.getCurrentUser().getUid();
        
        db.collection("users").document(userId)
                .collection("bookings")
                .orderBy("createdDate", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    bookingList.clear();
                    
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Booking booking = new Booking();
                            booking.setBookingId(document.getString("bookingId"));
                            booking.setServiceName(document.getString("serviceName"));
                            booking.setProviderName(document.getString("providerName"));
                            booking.setStatus(document.getString("status"));
                            
                            // Safe parsing
                            Long duration = document.getLong("duration");
                            booking.setDuration(duration != null ? duration.intValue() : 0);
                            
                            Double pricePerHour = document.getDouble("pricePerHour");
                            booking.setPricePerHour(pricePerHour != null ? pricePerHour : 0.0);
                            
                            Double totalAmount = document.getDouble("totalAmount");
                            booking.setTotalAmount(totalAmount != null ? totalAmount : 0.0);
                            
                            booking.setBookingDate(document.getString("bookingDate"));
                            booking.setBookingTime(document.getString("bookingTime"));
                            
                            Date createdDate = document.getDate("createdDate");
                            booking.setCreatedDate(createdDate != null ? createdDate : new Date());
                            
                            bookingList.add(booking);
                        } catch (Exception e) {
                            // Skip invalid bookings
                        }
                    }
                    
                    if (bookingList.isEmpty()) {
                        showEmptyState("No bookings found");
                    } else {
                        showBookings();
                    }
                    
                    bookingAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    showEmptyState("Failed to load bookings");
                });
    }
    
    private void showEmptyState(String message) {
        emptyState.setVisibility(View.VISIBLE);
        bookingsRecyclerView.setVisibility(View.GONE);
        emptyText.setText(message);
    }
    
    private void showBookings() {
        emptyState.setVisibility(View.GONE);
        bookingsRecyclerView.setVisibility(View.VISIBLE);
    }
}
