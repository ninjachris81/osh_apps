package com.osh.ui.components;

import android.content.Context;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AutoFitGridLayoutManager extends GridLayoutManager {

    private int columnWidthDp;
    private boolean columnWidthChanged = true;
    DisplayMetrics displayMetrics;

    public AutoFitGridLayoutManager(Context context, DisplayMetrics displayMetrics, int columnWidthDp) {
        super(context, 1);
        this.displayMetrics = displayMetrics;

        setColumnWidth(columnWidthDp);
    }

    public void setColumnWidth(int newColumnWidthDp) {
        if (newColumnWidthDp > 0 && newColumnWidthDp != columnWidthDp) {
            columnWidthDp = newColumnWidthDp;
            columnWidthChanged = true;
        }
    }

    private int getWidthDp() {
        return (int) (getWidth() / displayMetrics.density);
    }

    private int getHeightDp() {
        return (int) (getHeight() / displayMetrics.density);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (columnWidthChanged && columnWidthDp > 0) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = getWidthDp() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeightDp() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / columnWidthDp);
            setSpanCount(spanCount);
            columnWidthChanged = false;
        }
        super.onLayoutChildren(recycler, state);
    }
}