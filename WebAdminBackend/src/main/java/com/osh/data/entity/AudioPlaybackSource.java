package com.osh.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity(name = "dm_audio_playback_sources")
public class AudioPlaybackSource extends AbstractEntity {

    private String name;
    private String sourceUrl;
    //@Lob
    @Column(length = 1000000)
    private byte[] imageIcon;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSourceUrl() {
        return sourceUrl;
    }
    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
    public byte[] getImageIcon() {
        return imageIcon;
    }
    public void setImageIcon(byte[] imageIcon) {
        this.imageIcon = imageIcon;
    }

}
