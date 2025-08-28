package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmsiam.seu.isd.lab.project.homzen.Adapter.GroceryAdapter;
import com.kmsiam.seu.isd.lab.project.homzen.Model.Grocery;
import com.kmsiam.seu.isd.lab.project.homzen.R;

import java.util.ArrayList;

public class GroceryFragment extends Fragment {
    View groceryView;
    RecyclerView groceryRecyclerView;
    ArrayList<Grocery> arrGrocery ;
    GroceryAdapter groceryAdapter;

    public GroceryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groceryView = inflater.inflate(R.layout.fragment_grocery, container, false);

        groceryRecyclerView = groceryView.findViewById(R.id.groceryRecyclerView);
        groceryRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));

        arrGrocery = new ArrayList<>();
        loadDummyGrocery();
        groceryAdapter = new GroceryAdapter(getContext(), arrGrocery);
        groceryRecyclerView.setAdapter(groceryAdapter);

        return groceryView;
    }

    private void loadDummyGrocery(){
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));
        arrGrocery.add(new Grocery(R.drawable.olio_orolio_olive_oil_5_ltr, "Oil","Par Unit", "Olive Oil 5 Ltr", "874"));

    }
}