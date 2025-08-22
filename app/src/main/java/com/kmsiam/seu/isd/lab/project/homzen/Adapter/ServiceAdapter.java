package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.kmsiam.seu.isd.lab.project.homzen.Activity.ServiceDetailActivity;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Service;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> implements Filterable {

    private Context context;
    private List<Service> serviceList;
    private List<Service> serviceListFull;

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

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        Service service = serviceList.get(position);

        holder.txtServiceName.setText(service.getName());
        holder.txtServicePrice.setText("৳" + service.getPrice());
        holder.txtServiceDesc.setText(service.getDescription());

        // Load image using Glide
        Glide.with(context)
                .load(service.getImageUrl())
                .placeholder(R.drawable.ic_launcher_foreground) // Optional placeholder
                .error(R.drawable.ic_launcher_foreground) // Optional error image
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgService);

        holder.itemView.setOnClickListener(v -> navigateToDetail(service));
        holder.btnNext.setOnClickListener(v -> navigateToDetail(service));
    }

    private void navigateToDetail(Service service) {
        Intent intent = new Intent(context, ServiceDetailActivity.class);
        intent.putExtra("name", service.getName());
        intent.putExtra("price", "৳" + service.getPrice());
        intent.putExtra("desc", service.getDescription());
        intent.putExtra("imageUrl", service.getImageUrl()); // Pass URL instead of resource ID
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

            if (constraint == null || constraint.length() == 0) {
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
        TextView txtServiceName;
        TextView txtServicePrice;
        TextView txtServiceDesc;
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