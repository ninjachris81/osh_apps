package com.osh.ui.components;

import android.content.Context;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ArrayAdapterBase<T> extends ArrayAdapter<T> {

    public ArrayAdapterBase(@NonNull Context context, int resource) {
        super(context, resource);
    }

    public ArrayAdapterBase(@NonNull Context context, int resource, @NonNull List<T> objects) {
        super(context, resource, objects);
    }
}
