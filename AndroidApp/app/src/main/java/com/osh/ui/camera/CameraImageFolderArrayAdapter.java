package com.osh.ui.camera;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.osh.ui.components.ArrayAdapterBase;

import java.util.List;

public class CameraImageFolderArrayAdapter extends ArrayAdapter<CameraImageFolder> {

    public CameraImageFolderArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

}
