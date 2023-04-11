package com.osh.camera.config;

public class CameraSource {

    private String id;

    private String streamUri;

    public CameraSource() {
    }

    public CameraSource(String id, String streamUri) {
        this.id = id;
        this.streamUri = streamUri;
    }

    public String getId() {
        return id;
    }

    public String getStreamUri() {
        return streamUri;
    }
}
