# Address Dialog Implementation Test

## What was implemented:

### 1. Updated Dialog Layout (`dialog_current_location.xml`):
- **Current Location EditText**: Shows fresh GPS location each time dialog opens, user can edit manually
- **Saved Location TextView**: Shows previously saved address (read-only)
- Clear labels to distinguish between current and saved locations

### 2. Updated ProfileFragment Logic:
- **etCurrentLocation**: Always gets fresh GPS location when dialog opens
- **tvSavedLocation**: Displays saved address from Firestore or "No saved location"
- **Save functionality**: Saves whatever is in the current location EditText

## How it works:
1. User clicks Address button → Dialog opens
2. Current location EditText gets fresh GPS location
3. Saved location TextView shows previously saved address
4. User can manually edit current location
5. Map interactions update current location EditText
6. Save button saves current location to Firestore

## Test Steps:
1. Open ProfileFragment
2. Click Address button
3. Verify current location shows fresh GPS data
4. Verify saved location shows previous address
5. Edit current location manually
6. Save and verify it updates saved location
