package com.osh.data.entity;

import jakarta.persistence.Entity;

@Entity(name = "dm_known_devices")
public class KnownDevice extends AbstractEntity {

    private String serviceId;
    private String name;

    public String getServiceId() {
        return serviceId;
    }
    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

}
