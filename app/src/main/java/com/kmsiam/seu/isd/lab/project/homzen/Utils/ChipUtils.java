package com.kmsiam.seu.isd.lab.project.homzen.Utils;

import android.content.Context;
import android.widget.Filter;

import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class ChipUtils {
    
    public interface OnChipSelectedListener {
        void onChipSelected(String category);
    }
    
    public static void setupChips(Context context, ChipGroup chipGroup, String[] categories, 
                                Filter filter, OnChipSelectedListener listener) {
        chipGroup.removeAllViews();
        
        // Add "All" chip first
        Chip allChip = createModernChip(context, "All");
        allChip.setChecked(true);
        chipGroup.addView(allChip);
        
        // Add category chips
        for (String category : categories) {
            Chip chip = createModernChip(context, category);
            chipGroup.addView(chip);
        }
        
        // Set up chip selection listener
        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (!checkedIds.isEmpty()) {
                int checkedId = checkedIds.get(0);
                Chip checkedChip = group.findViewById(checkedId);
                if (checkedChip != null) {
                    String selectedCategory = checkedChip.getText().toString();
                    
                    // Apply filter
                    if (selectedCategory.equals("All")) {
                        filter.filter("");
                    } else {
                        filter.filter(selectedCategory);
                    }
                    
                    // Notify listener
                    if (listener != null) {
                        listener.onChipSelected(selectedCategory);
                    }
                }
            }
        });
    }
    
    private static Chip createModernChip(Context context, String text) {
        Chip chip = new Chip(context);
        chip.setText(text);
        chip.setCheckable(true);
        
        // Apply modern styling
        chip.setChipBackgroundColorResource(R.color.chip_background_selector);
        chip.setTextColor(ContextCompat.getColorStateList(context, R.color.chip_text_selector));
        chip.setChipStrokeColorResource(R.color.primary_green);
        chip.setChipStrokeWidth(context.getResources().getDimension(R.dimen.spacing_xs) / 4);
        chip.setChipCornerRadius(context.getResources().getDimension(R.dimen.radius_xl));
        
        // Set padding
        int paddingHorizontal = (int) context.getResources().getDimension(R.dimen.spacing_md);
        int paddingVertical = (int) context.getResources().getDimension(R.dimen.spacing_sm);
        chip.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
        
        // Set margins
        ChipGroup.LayoutParams params = new ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) context.getResources().getDimension(R.dimen.spacing_xs);
        params.setMargins(margin, 0, margin, 0);
        chip.setLayoutParams(params);
        
        return chip;
    }
}
