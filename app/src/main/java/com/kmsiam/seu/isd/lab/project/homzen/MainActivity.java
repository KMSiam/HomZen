package com.kmsiam.seu.isd.lab.project.homzen;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.CartFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.GroceryFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.HomeFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.ProfileFragment;
import com.kmsiam.seu.isd.lab.project.homzen.Fragment.ServiceFragment;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView btnNavigationView;
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

            Fragment current = getSupportFragmentManager().findFragmentById(R.id.FragmentContainer);

            if (id == R.id.nav_Home) {
                if (current instanceof HomeFragment) return true;// avoid duplicate replace
                loadFrag(new HomeFragment(), true);
            } else if (id == R.id.nav_Grocery) {
                if (current instanceof GroceryFragment) return true;
                loadFrag(new GroceryFragment(), false);
            } else if (id == R.id.nav_Cart) {
                if (current instanceof CartFragment) return true;
                loadFrag(new CartFragment(), false);
            } else if (id == R.id.nav_Service) {
                if (current instanceof ServiceFragment) return true;
                loadFrag(new ServiceFragment(), false);
            } else { // profile
                if (current instanceof ProfileFragment) return true;
                loadFrag(new ProfileFragment(), false);
            }
            return true;
        });

        // Default selected item
        btnNavigationView.setSelectedItemId(R.id.nav_Home);

        //When back stack changes, sync the checked item to the current fragment
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.FragmentContainer);
            if (f instanceof HomeFragment) {
                btnNavigationView.getMenu().findItem(R.id.nav_Home).setChecked(true);
            } else if (f instanceof GroceryFragment) {
                btnNavigationView.getMenu().findItem(R.id.nav_Grocery).setChecked(true);
            } else if (f instanceof CartFragment) {
                btnNavigationView.getMenu().findItem(R.id.nav_Cart).setChecked(true);
            } else if (f instanceof ServiceFragment) {
                btnNavigationView.getMenu().findItem(R.id.nav_Service).setChecked(true);
            } else if (f instanceof ProfileFragment) {
                btnNavigationView.getMenu().findItem(R.id.nav_Profile).setChecked(true);
            }
        });
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
    
    // Method to refresh cart when user logs in
    public void refreshCartAfterLogin() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.FragmentContainer);
        if (currentFragment instanceof CartFragment) {
            ((CartFragment) currentFragment).refreshCart();
        }
    }
}