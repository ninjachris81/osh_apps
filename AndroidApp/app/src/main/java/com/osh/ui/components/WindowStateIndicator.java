package com.osh.ui.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.BindingAdapter;

import com.osh.R;

public class WindowStateIndicator extends androidx.appcompat.widget.AppCompatImageView {

    private Drawable openIcon;
    private Drawable closedIcon;

    public WindowStateIndicator(Context context) {
        super(context);
        setup();
    }

    public WindowStateIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public WindowStateIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    protected void setup() {
        this.openIcon = AppCompatResources.getDrawable(getContext(), R.drawable.ic_lock_open_variant_outline);
        this.closedIcon = AppCompatResources.getDrawable(getContext(), R.drawable.ic_lock_outline);
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setMarginBottom(WindowStateIndicator view, float value) {
        if (value > 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) layoutParams).bottomMargin = (int) value;
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setMarginTop(WindowStateIndicator view, float value) {
        if (value > 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) layoutParams).topMargin = (int) value;
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("android:layout_constraintBottom_toBottomOf")
    public static void setContraintBottom(WindowStateIndicator view, String constraint) {
        if (!constraint.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout parentLayout = (ConstraintLayout) view.getParent();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);

                if (constraint.equals("parent")) {
                    constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), constraintSet.BOTTOM);
                }

                constraintSet.applyToLayoutParams(view.getId(), (ConstraintLayout.LayoutParams) layoutParams);
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("android:layout_constraintTop_toTopOf")
    public static void setContraintTop(WindowStateIndicator view, String constraint) {
        if (!constraint.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout parentLayout = (ConstraintLayout) view.getParent();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);

                if (constraint.equals("parent")) {
                    constraintSet.connect(view.getId(), ConstraintSet.TOP, parentLayout.getId(), constraintSet.TOP);
                }

                constraintSet.applyToLayoutParams(view.getId(), (ConstraintLayout.LayoutParams) layoutParams);
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("state")
    public static void setState(WindowStateIndicator view, boolean state) {
        view.setImageDrawable(state ? view.closedIcon : view.openIcon);
    }

}
