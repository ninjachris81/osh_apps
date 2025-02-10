package com.osh.ui.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.osh.R;

public class ShutterModeButton extends ChipGroup {

    public ShutterModeButton(Context context) {
        super(context);
        setup(context);
    }

    public ShutterModeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context);
    }

    public ShutterModeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context);
    }

    protected Chip autoSelection;
    protected Chip upSelection;
    protected Chip downSelection;

    protected void setup(Context context) {

        autoSelection = new Chip(context);
        //autoSelection.setId(R.id.shutter_mode_button_auto_id);
        //autoSelection.setText(getResources().getString(R.string.shutter_mode_auto));

        Drawable autoIcon = AppCompatResources.getDrawable(context, R.drawable.ic_calendar_clock);
        autoSelection.setChipIcon(autoIcon);
        autoSelection.setEnsureMinTouchTargetSize(false);
        autoSelection.setCheckable(true);
        //autoSelection.setCheckedIconVisible(false);

        Drawable upIcon = AppCompatResources.getDrawable(context, R.drawable.ic_chevron_up);
        upSelection = new Chip(getContext());
        upSelection.setChipIcon(upIcon);
        upSelection.setEnsureMinTouchTargetSize(false);

        Drawable downIcon = AppCompatResources.getDrawable(context, R.drawable.ic_chevron_down);
        downSelection = new Chip(getContext());
        downSelection.setChipIcon(downIcon);
        downSelection.setEnsureMinTouchTargetSize(false);

        this.addView(autoSelection);
        this.addView(upSelection);
        this.addView(downSelection);

        setChipSpacingResource(R.dimen.shutter_mode_button_spacing);
    }

    public void setAuto(boolean isAuto) {
        autoSelection.setChecked(isAuto);
    }

    @BindingAdapter("isAuto")
    public static void setAuto(ShutterModeButton view, boolean newValue) {
        view.setAuto(newValue);
    }

    @InverseBindingAdapter(attribute = "isAuto")
    public static boolean getAuto(ShutterModeButton view) {
        return view.autoSelection.isChecked();
    }

    @BindingAdapter("android:layout_marginBottom")
    public static void setMarginBottom(ShutterModeButton view, float value) {
        if (value > 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) layoutParams).bottomMargin = (int) value;
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("android:layout_marginTop")
    public static void setMarginTop(ShutterModeButton view, float value) {
        if (value > 0) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ((ConstraintLayout.LayoutParams) layoutParams).topMargin = (int) value;
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("android:layout_constraintBottom_toBottomOf")
    public static void setContraintBottom(ShutterModeButton view, String constraint) {
        if (!constraint.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout parentLayout = (ConstraintLayout) view.getParent();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);

                if (constraint.equals("parent")) {
                    constraintSet.connect(view.getId(), ConstraintSet.BOTTOM, parentLayout.getId(), ConstraintSet.BOTTOM);
                }

                constraintSet.applyToLayoutParams(view.getId(), (ConstraintLayout.LayoutParams) layoutParams);
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("android:layout_constraintTop_toTopOf")
    public static void setContraintTop(ShutterModeButton view, String constraint) {
        if (!constraint.isEmpty()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout parentLayout = (ConstraintLayout) view.getParent();
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(parentLayout);

                if (constraint.equals("parent")) {
                    constraintSet.connect(view.getId(), ConstraintSet.TOP, parentLayout.getId(), ConstraintSet.TOP);
                }

                constraintSet.applyToLayoutParams(view.getId(), (ConstraintLayout.LayoutParams) layoutParams);
            }
            view.setLayoutParams(layoutParams);
        }
    }

    @BindingAdapter("clickEnabled")
    public static void setClickEnabled(ShutterModeButton view, boolean enable) {
        for (int i = 0; i<view.getChildCount();i++) {
            view.getChildAt(i).setClickable(enable);
        }
    }

    public void setAutoClickListener(OnClickListener listener) {
        autoSelection.setOnClickListener(listener);
    }
    public void setUpClickListener(OnClickListener listener) {
        upSelection.setOnClickListener(listener);
    }

    public void setDownClickListener(OnClickListener listener) {
        downSelection.setOnClickListener(listener);
    }
}
