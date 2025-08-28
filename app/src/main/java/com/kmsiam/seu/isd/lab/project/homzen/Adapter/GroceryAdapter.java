package com.kmsiam.seu.isd.lab.project.homzen.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

public class GroceryAdapter extends RecyclerView.Adapter<GroceryAdapter.ViewHolder> implements Filterable {

    Context context;
    ArrayList<Grocery> arrGrocery;
    public GroceryAdapter(Context context, ArrayList<Grocery> arrGrocery) {
        this.context = context;
        this.arrGrocery = arrGrocery;
        this.arrGroceryFull = new ArrayList<>(arrGrocery);
    }
    private List<Grocery> arrGroceryFull;
    
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
            
            if (constraint == null || constraint.length() == 0) {
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
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.groceryImage.setImageResource(arrGrocery.get(position).getImage());
        holder.groceryType.setText(arrGrocery.get(position).getType());
        holder.groceryName.setText(arrGrocery.get(position).getName());
        holder.groceryPrice.setText("৳" + arrGrocery.get(position).getPrice());
        holder.groceryAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Add to cart functionality can be implemented here
                Toast.makeText(context, " Item added to the cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrGrocery.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView groceryImage;
        TextView groceryType, groceryName, groceryPrice;
        Button groceryAddButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            groceryImage = itemView.findViewById(R.id.grocery_item_image);
            groceryType = itemView.findViewById(R.id.grocery_item_type);
            groceryName = itemView.findViewById(R.id.grocery_item_name);
            groceryPrice = itemView.findViewById(R.id.grocery_item_price);
            groceryAddButton = itemView.findViewById(R.id.grocery_btn_add);

        }
    }
}
