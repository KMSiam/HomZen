package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;
import com.kmsiam.seu.isd.lab.project.homzen.R;
import com.kmsiam.seu.isd.lab.project.homzen.Utils.CartManager;

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements Filterable {

    int lastPosition = -1;
    Context context;
    ArrayList<Grocery> arrGrocery;
    public GroceryAdapter(Context context, ArrayList<Grocery> arrGrocery) {
        this.context = context;
        this.arrGrocery = arrGrocery;
        this.arrGroceryFull = new ArrayList<>(arrGrocery);
    }
    private List<Grocery> arrGroceryFull;
    
    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<Grocery> filteredList) {
        this.arrGrocery = new ArrayList<>(filteredList);
        this.arrGroceryFull = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }
    
    @Override
    public Filter getFilter() {
        return groceryFilter;
    }
    
    private Filter groceryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Grocery> filteredList = new ArrayList<>();

            if (constraint == null || constraint.isEmpty()) {
                // If search is empty, show all groceries
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
        holder.groceryImage.setImageResource(arrGrocery.get(position).getImage());
        holder.groceryType.setText(arrGrocery.get(position).getType());
        holder.groceryName.setText(arrGrocery.get(position).getName());
        holder.groceryPrice.setText("৳" + arrGrocery.get(position).getPrice());
        // Add to cart button click listener
        holder.groceryAddedToTheCartBtn.setOnClickListener(view -> {
            // Get the current grocery item
            Grocery grocery = arrGrocery.get(position);
            // Add to cart using CartManager
            CartManager cartManager = new CartManager(context);
            cartManager.addToCart(grocery);
            
            // Show success message
            Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show();
            
            // Optional: Notify any listeners that the cart was updated
            if (context instanceof OnCartUpdateListener) {
                ((OnCartUpdateListener) context).onCartUpdated();
            }
        });
        // Add this ONE line for animation
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
        TextView groceryType, groceryName, groceryPrice;
        Button groceryAddedToTheCartBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            groceryImage = itemView.findViewById(R.id.grocery_item_image);
            groceryType = itemView.findViewById(R.id.grocery_item_type);
            groceryName = itemView.findViewById(R.id.grocery_item_name);
            groceryPrice = itemView.findViewById(R.id.grocery_item_price);
            groceryAddedToTheCartBtn = itemView.findViewById(R.id.grocery_btn_add);

        }
    }
}