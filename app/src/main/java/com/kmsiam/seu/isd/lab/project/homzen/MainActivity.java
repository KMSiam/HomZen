package com.kmsiam.seu.isd.lab.project.homzen;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.CartFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.GroceryFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.HomeFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.ProfileFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.ServiceFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView btnNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnNavigationView = findViewById(R.id.btnNavigationView);

        btnNavigationView.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();

            if (id == R.id.nav_Home) {
                loadFrag(new HomeFragment(), true);
            } else if (id == R.id.nav_Grocery) {
                loadFrag(new GroceryFragment(), false);
            } else if (id == R.id.nav_Cart) {
                loadFrag(new CartFragment(), false);
            } else if (id == R.id.nav_Service) {
                loadFrag(new ServiceFragment(), false);
            } else { //profile
                loadFrag(new ProfileFragment(), false);
            }
            return true; // Return true to display the item as the selected item
        });

        btnNavigationView.setSelectedItemId(R.id.nav_Home);
    }
    public void loadFrag(Fragment fragment, boolean flag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.FragmentContainer, fragment);
        if (!flag) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }
}