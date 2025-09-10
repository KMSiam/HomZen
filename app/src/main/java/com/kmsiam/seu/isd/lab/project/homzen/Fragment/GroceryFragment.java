package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kmsiam.seu.isd.lab.project.homzen.Adapter.GroceryAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;

public class GroceryFragment extends Fragment {
    RecyclerView groceryRecyclerView;
    ArrayList<Grocery> arrGrocery;
    ArrayList<Grocery> arrGroceryFull;
    GroceryAdapter groceryAdapter;
    EditText searchView;
    ChipGroup chipGroup;
    ImageView clearSearch;
    ProgressBar searchProgress;
    LinearLayout emptyState, loadingState;
    
    private boolean isChipClicked = false;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grocery, container, false);

        // Initialize views
        groceryRecyclerView = view.findViewById(R.id.groceryRecyclerView);
        searchView = view.findViewById(R.id.searchView);
        chipGroup = view.findViewById(R.id.chipGroup);
        clearSearch = view.findViewById(R.id.clearSearch);
        searchProgress = view.findViewById(R.id.searchProgress);
        emptyState = view.findViewById(R.id.emptyState);
        loadingState = view.findViewById(R.id.loadingState);

        // Setup RecyclerView
        groceryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        
        // Load data immediately
        arrGrocery = new ArrayList<>();
        loadDummyGrocery();
        arrGroceryFull = new ArrayList<>(arrGrocery);
        groceryAdapter = new GroceryAdapter(getContext(), arrGrocery);
        groceryRecyclerView.setAdapter(groceryAdapter);

        setupChips();
        setupSearch();

        return view;
    }

    private void setupChips() {
        ArrayList<String> categories = new ArrayList<>();
        categories.add("All");
        
        for (Grocery item : arrGroceryFull) {
            String category = item.getCategory();
            if (!categories.contains(category)) {
                categories.add(category);
            }
        }
        
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            Chip chip = new Chip(getContext());
            chip.setText(category);
            chip.setCheckable(true);
            chip.setClickable(true);
            
            if (i == 0) {
                chip.setChecked(true);
            }
            
            chip.setOnClickListener(v -> {
                filterByCategory(category);
                isChipClicked = true;
                searchView.setText("");
            });
            chipGroup.addView(chip);
        }
    }

    private void setupSearch() {
        clearSearch.setOnClickListener(v -> {
            searchView.setText("");
            clearSearch.setVisibility(View.GONE);
        });

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearSearch.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
                
                if (isChipClicked) {
                    isChipClicked = false;
                    return;
                }
                
                for (int i = 0; i < chipGroup.getChildCount(); i++) {
                    ((Chip) chipGroup.getChildAt(i)).setChecked(false);
                }
                
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                
                searchRunnable = () -> filterBySearch(s.toString());
                searchHandler.postDelayed(searchRunnable, 300);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterByCategory(String category) {
        showSearchProgress();
        
        new Handler().postDelayed(() -> {
            arrGrocery.clear();
            if (category.equals("All")) {
                arrGrocery.addAll(arrGroceryFull);
            } else {
                for (Grocery item : arrGroceryFull) {
                    if (item.getCategory().equals(category)) {
                        arrGrocery.add(item);
                    }
                }
            }
            groceryAdapter.notifyDataSetChanged();
            updateEmptyState();
            hideSearchProgress();
        }, 200);
    }

    private void filterBySearch(String searchText) {
        showSearchProgress();
        
        new Handler().postDelayed(() -> {
            arrGrocery.clear();
            if (searchText.isEmpty()) {
                arrGrocery.addAll(arrGroceryFull);
            } else {
                String query = searchText.toLowerCase();
                for (Grocery item : arrGroceryFull) {
                    if (item.getName().toLowerCase().contains(query) ||
                        item.getCategory().toLowerCase().contains(query)) {
                        arrGrocery.add(item);
                    }
                }
            }
            groceryAdapter.notifyDataSetChanged();
            updateEmptyState();
            hideSearchProgress();
        }, 200);
    }

    private void showLoadingState() {
        loadingState.setVisibility(View.VISIBLE);
        groceryRecyclerView.setVisibility(View.GONE);
        emptyState.setVisibility(View.GONE);
    }

    private void hideLoadingState() {
        loadingState.setVisibility(View.GONE);
        groceryRecyclerView.setVisibility(View.VISIBLE);
        updateEmptyState();
    }

    private void showSearchProgress() {
        searchProgress.setVisibility(View.VISIBLE);
        clearSearch.setVisibility(View.GONE);
    }

    private void hideSearchProgress() {
        searchProgress.setVisibility(View.GONE);
        if (searchView.getText().length() > 0) {
            clearSearch.setVisibility(View.VISIBLE);
        }
    }

    private void updateEmptyState() {
        if (arrGrocery.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            groceryRecyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            groceryRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadDummyGrocery(){
        arrGrocery.add(new Grocery(R.drawable.fruits, "Fruits", "Per Kg", "Apple", "200"));
        arrGrocery.add(new Grocery(R.drawable.fruits, "Fruits", "Per Kg", "Banana", "80"));
        arrGrocery.add(new Grocery(R.drawable.fruits, "Fruits", "Per Kg", "Orange", "120"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil", "Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil", "Par Unit", "Sunflower Oil 2 Ltr", "450"));
        arrGrocery.add(new Grocery(R.drawable.snacks, "Snacks", "Pack of 1", "Chips", "20"));
        arrGrocery.add(new Grocery(R.drawable.snacks, "Snacks", "Pack of 1", "Biscuits", "15"));
        arrGrocery.add(new Grocery(R.drawable.vegetables, "Vegetables", "Per Kg", "Potato", "30"));
        arrGrocery.add(new Grocery(R.drawable.vegetables, "Vegetables", "Per Kg", "Tomato", "40"));
        arrGrocery.add(new Grocery(R.drawable.drinks, "Drinks", "1.5L Bottle", "Cola", "90"));
        arrGrocery.add(new Grocery(R.drawable.drinks, "Drinks", "1L Pack", "Orange Juice", "120"));
        arrGrocery.add(new Grocery(R.drawable.fruits, "Fruits", "Per Kg", "Mango", "150"));
    }
}
