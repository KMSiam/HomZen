package com.kmsiam.seu.isd.lab.project.homzen.Utils;

import android.content.Context;
import android.content.res.Resources;
import com.kmsiam.seu.isd.lab.project.homzen.R;

public class ResourceValidator {
    
    /**
     * Validates if a resource ID is a valid drawable resource
     * @param context Application context
     * @param resourceId Resource ID to validate
     * @return true if valid drawable, false otherwise
     */
    public static boolean isValidDrawableResource(Context context, int resourceId) {
        if (resourceId == 0) return false;
        
        try {
            // Check if it's a known invalid resource (view ID)
            if (resourceId == R.id.helpContainer) {
                return false;
            }
            
            // Try to get the resource type
            Resources resources = context.getResources();
            String resourceType = resources.getResourceTypeName(resourceId);
            return "drawable".equals(resourceType);
        } catch (Resources.NotFoundException e) {
            return false;
        }
    }
    
    /**
     * Gets a safe drawable resource ID, returns default if invalid
     * @param context Application context
     * @param resourceId Resource ID to validate
     * @param defaultDrawable Default drawable to use if invalid
     * @return Valid drawable resource ID
     */
    public static int getSafeDrawableResource(Context context, int resourceId, int defaultDrawable) {
        if (isValidDrawableResource(context, resourceId)) {
            return resourceId;
        }
        return defaultDrawable;
    }
}
