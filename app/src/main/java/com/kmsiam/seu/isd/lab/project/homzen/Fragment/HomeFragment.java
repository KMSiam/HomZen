package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kmsiam.seu.isd.lab.project.homzen.MainActivity;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class HomeFragment extends Fragment {

    private CardView groceryQuickCard, servicesQuickCard;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        initializeViews(view);
        setupClickListeners();
        handleBackPress();
    }

    private void initializeViews(View view) {
        groceryQuickCard = view.findViewById(R.id.grocery_quick_card);
        servicesQuickCard = view.findViewById(R.id.services_quick_card);
    }

    private void setupClickListeners() {
        // Navigate to Grocery fragment when grocery card is clicked
        groceryQuickCard.setOnClickListener(v -> {
            navigateToFragment(R.id.nav_Grocery);
        });

        // Navigate to Services fragment when services card is clicked
        servicesQuickCard.setOnClickListener(v -> {
            navigateToFragment(R.id.nav_Service);
        });
    }

    private void navigateToFragment(int navigationId) {
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            BottomNavigationView bottomNav = mainActivity.findViewById(R.id.btnNavigationView);
            if (bottomNav != null) {
                bottomNav.setSelectedItemId(navigationId);
            }
        }
    }

    private void handleBackPress() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showExitDialog();
                    }
                }
        );
    }

    private void showExitDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Exit HomZen")
                .setIcon(R.drawable.ic_logout)
                .setMessage("Are you sure you want to exit the app?")
                .setPositiveButton("Yes", (dialog, which) -> requireActivity().finishAffinity())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
