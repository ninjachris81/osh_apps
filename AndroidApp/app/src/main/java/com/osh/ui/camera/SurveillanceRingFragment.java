package com.osh.ui.camera;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.osh.R;
import com.osh.camera.config.CameraFTPSource;
import com.osh.databinding.FragmentSurveillanceListBinding;
import com.osh.log.LogFacade;
import com.osh.ui.dialogs.SelectAudioDialogFragment;
import com.osh.ui.dialogs.ShowImageDetailsFragment;
import com.osh.ui.dialogs.ShowVideoDetailsFragment;
import com.osh.worker.FTPImageWorker;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 */
public class SurveillanceRingFragment extends Fragment {

    FragmentSurveillanceListBinding binding;

    private static final String TAG = SurveillanceRingFragment.class.getName();
    CameraFTPSource source;

    final List<CameraImageContent.ThumbnailImageItem> imageList = new ArrayList<>();
    private MySurveillancePictureRecyclerViewAdapter recyclerViewAdapter;

    private final FTPImageWorker worker = new FTPImageWorker();
    private CameraImageFolderArrayAdapter folderArrayAdapter;

    public SurveillanceRingFragment() {
    }

    public SurveillanceRingFragment(CameraFTPSource source) {
        this.source = source;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSurveillanceListBinding.inflate(getLayoutInflater(), container, false);
        View view =  binding.getRoot();

        RecyclerView recyclerView = binding.surveillanceRecyclerView;

        // Set the adapter
        Context context = view.getContext();
        recyclerView.setLayoutManager(new GridLayoutManager(context, 4));

        recyclerViewAdapter = new MySurveillancePictureRecyclerViewAdapter(imageList, new MySurveillancePictureRecyclerViewAdapter.ImageClickListener() {
            @Override
            public void onImageClicked(CameraImageContent.ThumbnailImageItem item) {
                worker.fetchVideo(source, item, getContext(), file -> {
                    getActivity().runOnUiThread(() -> {
                        ShowVideoDetailsFragment dialog = ShowVideoDetailsFragment.newInstance();
                        dialog.setVideoFile(file);
                        dialog.show(getChildFragmentManager(), SelectAudioDialogFragment.TAG);
                    });
                });
            }
        });
        recyclerView.setAdapter(recyclerViewAdapter);

        binding.listStatus.setText("Loading folders");
        folderArrayAdapter = new CameraImageFolderArrayAdapter(getContext(), R.layout.spinner_dropdown_item);
        binding.surveillanceDaySelectionTextview.setAdapter(folderArrayAdapter);

        worker.fetchFolders(source, source.getRemoteDirVideos(), folders -> {
            folderArrayAdapter.clear();
            folderArrayAdapter.addAll(folders);

            if (!folders.isEmpty()) {
                getActivity().runOnUiThread(() -> {
                    binding.listStatus.setText("");
                    //binding.surveillanceDaySelectionTextview.setText(folderArrayAdapter.getItem(0).toString());
                });
            }
        });

        binding.surveillanceDaySelectionTextview.setOnItemClickListener((adapterView, view1, i, l) -> {
            loadFolder(folderArrayAdapter.getItem(i));
        });

        return view;
    }

    void loadFolder(CameraImageFolder folder) {
        binding.listStatus.setText("Loading " + folder);
        binding.progressBar.setMin(0);
        binding.progressBar.setVisibility(View.VISIBLE);
        imageList.clear();
        recyclerViewAdapter.refresh();

        worker.fetchThumbnails(source, source.getRemoteDirVideos() + "/" + folder.getName() + "/thumbnails", getResources(), new FTPImageWorker.GetThumbnailCallback() {
            @Override
            public void onComplete(List<CameraImageContent.ThumbnailImageItem> thumbnails) {
                LogFacade.d(TAG, "" + thumbnails.size());
                for (CameraImageContent.ThumbnailImageItem thumbnail : thumbnails) {
                    imageList.add(thumbnail);
                }

                getActivity().runOnUiThread(() -> {
                    recyclerViewAdapter.refresh();
                    binding.listStatus.setText(thumbnails.isEmpty() ? "Empty folder" : "");
                    binding.progressBar.setVisibility(View.INVISIBLE);
                });
            }

            @Override
            public void onProgress(int total, int progress) {
                getActivity().runOnUiThread(() -> {
                    binding.progressBar.setMax(total);
                    binding.progressBar.setProgress(progress);
                });
            }
        });
    }
}