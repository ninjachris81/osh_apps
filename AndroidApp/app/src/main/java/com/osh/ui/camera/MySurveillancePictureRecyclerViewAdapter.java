package com.osh.ui.camera;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.osh.databinding.FragmentSurveillanceItemBinding;
import com.osh.ui.camera.CameraImageContent.ThumbnailImageItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ThumbnailImageItem}.
 */
public class MySurveillancePictureRecyclerViewAdapter extends RecyclerView.Adapter<MySurveillancePictureRecyclerViewAdapter.ViewHolder> {

    private final List<ThumbnailImageItem> mValues;
    private final ImageClickListener imageClickListener;


    interface ImageClickListener {
        void onImageClicked(ThumbnailImageItem item);
    }

    public MySurveillancePictureRecyclerViewAdapter(List<ThumbnailImageItem> items, ImageClickListener imageClickListener) {
        mValues = items;
        this.imageClickListener = imageClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentSurveillanceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.imageView.setImageDrawable(mValues.get(position).imageContent);
        holder.labelView.setText(mValues.get(position).getDateString());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void refresh() {
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView imageView;
        public final TextView labelView;

        public ViewHolder(FragmentSurveillanceItemBinding binding) {
            super(binding.getRoot());
            imageView = binding.imageContent;
            labelView = binding.labelContent;

            imageView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int pos = getBindingAdapterPosition();
            ThumbnailImageItem item = mValues.get(pos);
            imageClickListener.onImageClicked(item);
        }
    }
}