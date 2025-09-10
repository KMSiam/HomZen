package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmsiam.seu.isd.lab.project.homzen.Model.Booking;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {
    
    private Context context;
    private ArrayList<Booking> bookingList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault());
    
    public BookingAdapter(Context context, ArrayList<Booking> bookingList) {
        this.context = context;
        this.bookingList = bookingList;
    }
    
    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking booking = bookingList.get(position);
        
        holder.bookingId.setText(booking.getBookingId());
        holder.serviceName.setText(booking.getServiceName());
        holder.providerName.setText("Provider: " + booking.getProviderName());
        holder.bookingDateTime.setText(booking.getBookingDate() + " at " + booking.getBookingTime());
        holder.createdDate.setText(dateFormat.format(booking.getCreatedDate()));
        holder.duration.setText(booking.getDuration() + " hours");
        holder.totalAmount.setText("৳" + String.format("%.0f", booking.getTotalAmount()));
        holder.status.setText(booking.getStatus());
        
        // Set status color
        if ("Confirmed".equals(booking.getStatus())) {
            holder.status.setBackgroundColor(context.getResources().getColor(R.color.teal_700));
            holder.status.setTextColor(context.getResources().getColor(android.R.color.white));
        } else if ("Complete".equals(booking.getStatus())) {
            holder.status.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_dark));
            holder.status.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.status.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
            holder.status.setTextColor(context.getResources().getColor(android.R.color.white));
        }
    }
    
    @Override
    public int getItemCount() {
        return bookingList.size();
    }
    
    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView bookingId, serviceName, providerName, bookingDateTime, createdDate, duration, totalAmount, status;
        
        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            bookingId = itemView.findViewById(R.id.booking_id);
            serviceName = itemView.findViewById(R.id.service_name);
            providerName = itemView.findViewById(R.id.provider_name);
            bookingDateTime = itemView.findViewById(R.id.booking_date_time);
            createdDate = itemView.findViewById(R.id.created_date);
            duration = itemView.findViewById(R.id.duration);
            totalAmount = itemView.findViewById(R.id.total_amount);
            status = itemView.findViewById(R.id.booking_status);
        }
    }
}
