package com.kmsiam.seu.isd.lab.project.homzen.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class ChipUtils {
    
    public interface OnChipSelectedListener {
        void onChipSelected(String category);
    }
    
    public static void setupChips(@NonNull Context context, 
                                @NonNull ChipGroup chipGroup, 
                                @NonNull String[] categories, 
                                @NonNull Filter filter,
                                @NonNull OnChipSelectedListener listener) {
        
        // Clear existing chips and set single selection mode
        chipGroup.removeAllViews();
        chipGroup.setSingleSelection(true);
        
        // Add "All" chip first
        Chip allChip = createChip(context, "All", true);
        allChip.setOnClickListener(v -> {
            clearChipSelections(chipGroup);
            allChip.setChecked(true);
            filter.filter("");
            listener.onChipSelected("");
        });
        chipGroup.addView(allChip);
        
        // Add category chips
        for (String category : categories) {
            Chip chip = createChip(context, category, false);
            chip.setOnClickListener(v -> {
                clearChipSelections(chipGroup);
                chip.setChecked(true);
                filter.filter(category);
                listener.onChipSelected(category);
            });
            chipGroup.addView(chip);
        }
        
        // Trigger initial filter for All
        filter.filter("");
    }
    
    private static Chip createChip(Context context, String text, boolean checked) {
        Chip chip = new Chip(context);
        chip.setText(text);
        chip.setCheckable(true);
        chip.setClickable(true);
        chip.setChecked(checked);
        
        // Set layout params
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) (8 * context.getResources().getDisplayMetrics().density);
        layoutParams.setMargins(0, 0, margin, 0);
        chip.setLayoutParams(layoutParams);
        
        return chip;
    }
    
    private static void clearChipSelections(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            chip.setChecked(false);
        }
    }
}
