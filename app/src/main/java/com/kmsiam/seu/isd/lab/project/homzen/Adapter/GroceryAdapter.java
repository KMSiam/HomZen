package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.CartManager;

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<Grocery> arrGrocery;
    ArrayList<Grocery> arrGroceryFull; // Full list for filtering
    private int lastPosition = -1;

    public GroceryAdapter(Context context, ArrayList<Grocery> arrGrocery) {
        this.context = context;
        this.arrGrocery = arrGrocery;
        this.arrGroceryFull = new ArrayList<>(arrGrocery); // Create a copy for filtering
    }

    public void setFilteredList(ArrayList<Grocery> filteredList) {
        this.arrGrocery = filteredList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return groceryFilter;
    }

    private final Filter groceryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Grocery> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(arrGroceryFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Grocery item : arrGroceryFull) {
                    // Search in both name and category (case insensitive)
                    if (item.getName().toLowerCase().contains(filterPattern) ||
                        item.getCategory().toLowerCase().contains(filterPattern) ||
                        item.getType().toLowerCase().contains(filterPattern)) {
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
            arrGrocery.clear();
            arrGrocery.addAll((List<Grocery>) results.values);
            notifyDataSetChanged(); // This refreshes the RecyclerView
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grocery, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Grocery grocery = arrGrocery.get(position);
        
        holder.groceryImage.setImageResource(grocery.getImage());
        holder.groceryCategory.setText(grocery.getCategory());
        holder.groceryName.setText(grocery.getName());
        holder.groceryType.setText(grocery.getType());
        holder.groceryPrice.setText("৳" + grocery.getPrice());
        
        // Add to cart button click listener
        holder.groceryAddedToTheCartBtn.setOnClickListener(view -> {
            // Add to cart using CartManager
            CartManager cartManager = new CartManager(context);
            cartManager.addToCart(grocery);
            
            // Show success message
            Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show();
        });
        
        // Add animation
        setSlideInAnimation(holder.itemView, position);
    }

    private void setSlideInAnimation(View view, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.item_slide_in_bottom);
            view.startAnimation(animation);
            lastPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        return arrGrocery.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView groceryImage;
        TextView groceryCategory, groceryName, groceryType, groceryPrice;
        MaterialButton groceryAddedToTheCartBtn;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groceryImage = itemView.findViewById(R.id.grocery_item_image);
            groceryCategory = itemView.findViewById(R.id.grocery_item_category);
            groceryName = itemView.findViewById(R.id.grocery_item_name);
            groceryType = itemView.findViewById(R.id.grocery_item_type);
            groceryPrice = itemView.findViewById(R.id.grocery_item_price);
            groceryAddedToTheCartBtn = itemView.findViewById(R.id.grocery_btn_add);
        }
    }
}
