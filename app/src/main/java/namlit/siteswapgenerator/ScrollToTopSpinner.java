package namlit.siteswapgenerator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

/**
 * Custom Spinner that scrolls the dropdown list to show items from the top.
 * Uses reflection to access and control the popup's ListView scroll position.
 */
public class ScrollToTopSpinner extends Spinner {

    private boolean isDropdownOpen = false;

    public ScrollToTopSpinner(Context context) {
        super(context);
    }

    public ScrollToTopSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollToTopSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        boolean result = super.performClick();

        // Schedule the scroll operation after the dropdown is displayed
        post(new Runnable() {
            @Override
            public void run() {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        scrollToShowTop();
                    }
                }, 100); // Wait for dropdown to be fully rendered
            }
        });

        return result;
    }

    private void scrollToShowTop() {
        try {
            // Access the popup window
            java.lang.reflect.Field popupField = Spinner.class.getDeclaredField("mPopup");
            popupField.setAccessible(true);
            Object popup = popupField.get(this);

            if (popup != null) {
                // Check if popup is showing
                java.lang.reflect.Method isShowingMethod = popup.getClass().getMethod("isShowing");
                Boolean isShowing = (Boolean) isShowingMethod.invoke(popup);

                if (isShowing != null && isShowing) {
                    // Get the ListView
                    java.lang.reflect.Method getListViewMethod = popup.getClass().getMethod("getListView");
                    Object listView = getListViewMethod.invoke(popup);

                    if (listView instanceof android.widget.ListView) {
                        android.widget.ListView lv = (android.widget.ListView) listView;
                        int selectedPos = getSelectedItemPosition();
                        int totalItems = getAdapter() != null ? getAdapter().getCount() : 0;

                        // Calculate how many items can be visible in the dropdown
                        int visibleItemCount = calculateVisibleItemCount(lv);

                        // Calculate desired distribution:
                        // 3/4 of visible items (excluding selected) should be above
                        // 1/4 of visible items (including selected) should be below
                        int itemsExcludingSelected = visibleItemCount - 1;
                        int desiredItemsAbove = (int) Math.round(itemsExcludingSelected * 0.75);
                        int desiredItemsBelow = visibleItemCount - desiredItemsAbove - 1; // -1 for selected item itself

                        // Ensure at least 1 item below
                        if (desiredItemsBelow < 1) {
                            desiredItemsBelow = 1;
                            desiredItemsAbove = visibleItemCount - desiredItemsBelow - 1;
                        }

                        int targetScrollPosition;

                        if (selectedPos < desiredItemsAbove) {
                            // Not enough items above to show the desired amount
                            // Just scroll to position 0
                            targetScrollPosition = 0;
                        } else if (selectedPos + desiredItemsBelow >= totalItems) {
                            // Not enough items below to show the desired amount
                            // Scroll so the last item is visible at the bottom
                            targetScrollPosition = Math.max(0, totalItems - visibleItemCount);
                        } else {
                            // We can show the desired distribution
                            targetScrollPosition = selectedPos - desiredItemsAbove;
                        }

                        // Scroll to the calculated position
                        lv.setSelection(targetScrollPosition);
                    }
                }
            }
        } catch (Exception e) {
            // Silently handle reflection failures
            // The spinner will work normally even if this fails
        }
    }

    /**
     * Calculate how many items are visible in the dropdown ListView
     * based on the ListView height and item height.
     */
    private int calculateVisibleItemCount(android.widget.ListView listView) {
        try {
            // Get the ListView height
            int listViewHeight = listView.getHeight();

            if (listViewHeight <= 0) {
                // ListView not yet measured, use screen height as approximation
                android.view.Display display = ((android.view.WindowManager) getContext()
                        .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
                android.graphics.Point size = new android.graphics.Point();
                display.getSize(size);
                // Dropdown typically takes up to 50-60% of screen height
                listViewHeight = (int) (size.y * 0.5);
            }

            // Standard dropdown item height is 48dp
            float density = getContext().getResources().getDisplayMetrics().density;
            int itemHeightPx = (int) (48 * density);

            // Calculate visible items
            int visibleItems = listViewHeight / itemHeightPx;

            // Ensure we have at least 3 visible items and cap at reasonable maximum
            visibleItems = Math.max(3, Math.min(visibleItems, 15));

            return visibleItems;
        } catch (Exception e) {
            // Default fallback if calculation fails
            return 8;
        }
    }
}
