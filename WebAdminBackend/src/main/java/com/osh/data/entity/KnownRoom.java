package com.osh.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;

@Entity(name = "dm_known_rooms")
public class KnownRoom extends AbstractEntity {

    private String name;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    private KnownArea knownArea;

    private
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public KnownArea getKnownArea() {
        return knownArea;
    }
    public void setKnownArea(KnownArea knownArea) {
        this.knownArea = knownArea;
    }

    public int getActors() {
    }
}
