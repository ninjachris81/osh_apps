package com.osh.ui.sysint;

import android.os.Build;
import android.os.Handler;

import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.osh.device.DeviceBase;
import com.osh.device.KnownDevice;
import com.osh.service.IDeviceDiscoveryService;

import java.util.Collection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SystemInternalsViewModel extends ViewModel {

    private final Timer timer;
    final Handler handler = new Handler();

    private final IDeviceDiscoveryService deviceDiscoveryService;

    public ObservableArrayList<DeviceBase> deviceList = new ObservableArrayList<>();

    public SystemInternalsViewModel(IDeviceDiscoveryService deviceDiscoveryService) {
        this.deviceDiscoveryService = deviceDiscoveryService;

        timer = new Timer();

        TimerTask timerTask = new TimerTask() {
            public void run() {

               handler.post(new Runnable() {
                    public void run() {
                        deviceList.clear();
                        Collection<DeviceBase> resultList;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            resultList = deviceDiscoveryService.getDeviceList().stream().sorted((o1, o2) -> {
                                if (o1 instanceof KnownDevice && o2 instanceof KnownDevice)
                                    return ((KnownDevice) o1).getName().compareTo(((KnownDevice) o2).getName());
                                if (o1 instanceof KnownDevice) return -1;
                                if (o2 instanceof KnownDevice) return 1;
                                return o1.getServiceId().compareTo(o2.getServiceId());
                            }).toList();
                        } else {
                            resultList = deviceDiscoveryService.getDeviceList();
                        }
                        deviceList.addAll(resultList);
                    }
                });
            }
        };

        timer.schedule(timerTask, 1000, 10000);
    }
}