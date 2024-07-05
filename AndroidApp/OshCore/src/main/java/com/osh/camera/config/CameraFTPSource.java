package com.osh.camera.config;

public class CameraFTPSource {

    String id;
    String host;
    String user;
    String password;
    String remoteDir;

    public CameraFTPSource() {
    }

    public CameraFTPSource(String id, String host, String user, String password, String remoteDir) {
        this.id = id;
        this.host = host;
        this.user = user;
        this.password = password;
        this.remoteDir = remoteDir;
    }

    public String getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getRemoteDirImages() {
        return remoteDir + "/images";
    }

    public String getRemoteDirVideos() {
        return remoteDir + "/videos";
    }
}
