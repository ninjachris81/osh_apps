package com.osh.ui.components;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;

import java.util.List;

public class TextImageSpinnerArrayAdapter extends ArrayAdapterBase<String>  {
    public TextImageSpinnerArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public TextImageSpinnerArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }
}
