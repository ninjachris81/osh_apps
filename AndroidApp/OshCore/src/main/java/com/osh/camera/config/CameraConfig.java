package com.osh.camera.config;

import java.util.HashMap;
import java.util.Map;

public class CameraConfig {

    Map<String, CameraSource> cameraSourceList = new HashMap<>();

    Map<String, CameraFTPSource> cameraFTPSourceMap = new HashMap<>();

    public void addCameraSource(String id, String streamUri) {
        CameraSource cameraSource = new CameraSource(id, streamUri);
        addCameraSource(cameraSource);
    }

    public void addCameraFTPSource(String id, String host, String user, String password, String remoteDir) {
        CameraFTPSource cameraFTPSource = new CameraFTPSource(id, host, user, password, remoteDir);
        addCameraFTPSource(cameraFTPSource);
    }

    public void addCameraSource(CameraSource cameraSource) {
        cameraSourceList.put(cameraSource.getId(), cameraSource);
    }

    public void addCameraFTPSource(CameraFTPSource cameraFTPSource) {
        cameraFTPSourceMap.put(cameraFTPSource.getId(), cameraFTPSource);
    }

    public CameraSource getCameraSource(String id) {
        return cameraSourceList.get(id);
    }

    public CameraFTPSource getCameraFTPSource(String id) {
        return cameraFTPSourceMap.get(id);
    }
}
