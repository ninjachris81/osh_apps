package com.osh.ui.dialogs;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.osh.R;
import com.osh.actor.AudioPlaybackActor;
import com.osh.databinding.FragmentImageDetailsDialogBinding;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.ui.components.AudioPlaybackActorArrayAdapter;
import com.osh.ui.components.AudioPlaybackSourceArrayAdapter;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowImageDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowImageDetailsFragment extends DialogFragment {

    FragmentImageDetailsDialogBinding binding;
    private Drawable image;

    public ShowImageDetailsFragment() {
        // Required empty public constructor
    }

    public static ShowImageDetailsFragment newInstance() {
        ShowImageDetailsFragment fragment = new ShowImageDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentImageDetailsDialogBinding.inflate(getLayoutInflater(), container, false);

        Button closeBtn = binding.closeBtn;
        closeBtn.setOnClickListener(view -> {
            dismiss();
        });

        binding.imageDetail.setImageDrawable(image);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) getResources().getDimension(R.dimen.image_dialog_width);
        params.height = (int) getResources().getDimension(R.dimen.image_dialog_height);
        window.setAttributes(params);
    }

    public void setImage(Drawable image) {
        this.image = image.getConstantState().newDrawable();
    }
}