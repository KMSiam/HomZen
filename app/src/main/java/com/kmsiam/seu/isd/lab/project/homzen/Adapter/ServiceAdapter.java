package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmsiam.seu.isd.lab.project.homzen.Activity.ServiceDetailActivity;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Service;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements Filterable {
    int lastPosition = -1;
    Context context;
    List<Service> serviceList;
    List<Service> serviceListFull;

    public ServiceAdapter(Context context, List<Service> serviceList) {
        this.context = context;
        this.serviceList = serviceList;
        this.serviceListFull = new ArrayList<>(serviceList);
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.txtServiceName.setText(service.getName());
        holder.txtServicePrice.setText("৳" + service.getPrice());
        holder.txtServiceDesc.setText(service.getDescription());

        // Load image from drawable resource
        holder.imgService.setImageResource(service.getImageResId());

        holder.itemView.setOnClickListener(v -> navigateToDetail(service));
        holder.btnNext.setOnClickListener(v -> navigateToDetail(service));

        // Add this ONE line for animation
        setSlideInAnimation(holder.itemView, position);
    }

    // Add this ONE method
    private void setSlideInAnimation(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_in_bottom);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void navigateToDetail(Service service) {
        Service.ServiceProvider provider = service.getProvider();
        Intent intent = new Intent(context, ServiceDetailActivity.class);
        
        // Service details
        intent.putExtra("name", service.getName());
        intent.putExtra("price", "৳" + service.getPrice());
        intent.putExtra("desc", service.getDescription());
        intent.putExtra("imageResId", service.getImageResId());
        
        // Provider details
        intent.putExtra("providerName", provider.getName());
        intent.putExtra("providerDesc", provider.getDescription());
        intent.putExtra("providerPhone", provider.getPhone());
        intent.putExtra("providerEmail", provider.getEmail());
        intent.putExtra("providerRating", provider.getFormattedRating());
        intent.putExtra("providerImageResId", provider.getImageResId());
        
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    @Override
    public Filter getFilter() {
        return serviceFilter;
    }

    private Filter serviceFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Service> filteredList = new ArrayList<>();

            if (constraint == null || constraint.isEmpty()) {
                // If search is empty, show all services
                filteredList.addAll(serviceListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Service item : serviceListFull) {
                    // Search in both name and category (case insensitive)
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                            item.getCategory().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            serviceList.clear();
            serviceList.addAll((List<Service>) results.values);
            notifyDataSetChanged(); // This refreshes the RecyclerView
        }
    };

    // ServiceViewHolder class
    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgService;
        TextView txtServiceName, txtServicePrice, txtServiceDesc;
        Button btnNext;
        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgService = itemView.findViewById(R.id.imgService);
            txtServiceName = itemView.findViewById(R.id.txtServiceName);
            txtServicePrice = itemView.findViewById(R.id.txtServicePrice);
            txtServiceDesc = itemView.findViewById(R.id.txtServiceDesc);
            btnNext = itemView.findViewById(R.id.btnNext);
        }
    }
}