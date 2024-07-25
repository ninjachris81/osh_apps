package com.osh.ui.sysint;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.device.DeviceBase;
import com.osh.device.KnownDevice;
import com.osh.utils.OshValueFormats;

import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DeviceArrayAdapter extends ArrayAdapter<DeviceBase> {

    private static int COLOR_ORANGE;

    public DeviceArrayAdapter(Context context, List<DeviceBase> devices) {
        super(context, 0, devices);

        COLOR_ORANGE = ResourcesCompat.getColor(context.getResources(), R.color.orange, null);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.device_listview_item, parent, false);
        }

        MaterialButton icon =  convertView.findViewById(R.id.icon);
        MaterialButton state = convertView.findViewById(R.id.healthState);
        TextView name = convertView.findViewById(R.id.serviceName);
        TextView deviceId = convertView.findViewById(R.id.deviceId);
        TextView serviceId = convertView.findViewById(R.id.serviceId);
        TextView onlineTime = convertView.findViewById(R.id.onlineTime);

        DeviceBase dev = getItem(position);

        if (dev.isOnline() ){
            icon.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_weather_cloudy));
            icon.setIconTint(ColorStateList.valueOf(Color.GREEN));
        } else {
            icon.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_cloud_off_outline));
            icon.setIconTint(ColorStateList.valueOf(Color.RED));
        }

        switch(dev.getHealthState()) {
            case Unknown -> {
                state.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_cloud_question_outline));
                state.setIconTint(ColorStateList.valueOf(Color.WHITE));
            }
            case Healthy -> {
                state.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_thumb_up));
                state.setIconTint(ColorStateList.valueOf(Color.GREEN));
            }
            case HasWarnings -> {
                state.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_alert_circle_outline));
                state.setIconTint(ColorStateList.valueOf(COLOR_ORANGE));
            }
            case HasErrors -> {
                state.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_alert_circle_outline));
                state.setIconTint(ColorStateList.valueOf(Color.RED));
            }
        }
        state.setVisibility(dev.isOnline() ? View.VISIBLE : View.INVISIBLE);


        if (dev instanceof KnownDevice) {
            name.setText(((KnownDevice) dev).getName());
        } else {
            name.setText("(Unknown service)");
        }

        deviceId.setText(dev.getId());
        serviceId.setText(dev.getServiceId());

        onlineTime.setVisibility(dev.isOnline() ? View.VISIBLE : View.INVISIBLE);
        onlineTime.setText(OshValueFormats.formatTime(dev.getUpTime()));

        return convertView;
    }

    public void refresh() {
        notifyDataSetChanged();
    }
}
