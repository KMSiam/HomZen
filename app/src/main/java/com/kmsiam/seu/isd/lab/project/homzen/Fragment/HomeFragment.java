package com.kmsiam.seu.isd.lab.project.homzen.Fragment;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kmsiam.seu.isd.lab.project.homzen.R;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Handle back button press
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Exit App")
                                .setIcon(R.drawable.ic_logout)
                                .setMessage("Are you sure you want to exit?")
                                .setPositiveButton("Yes", (dialog, which) -> requireActivity().finishAffinity())
                                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                                .show();
                    }
                }
        );
    }
}