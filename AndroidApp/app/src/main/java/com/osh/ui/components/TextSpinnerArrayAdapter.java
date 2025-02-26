package com.osh.ui.components;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class TextSpinnerArrayAdapter extends ArrayAdapterBase<String> {

    public TextSpinnerArrayAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    public TextSpinnerArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

}
