package com.osh.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

import java.util.List;

@Entity(name = "dm_known_areas")
public class KnownArea extends AbstractEntity {

    private String name;
    private Integer displayOrder;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "knownArea")
    @OrderBy("name")
    private List<KnownRoom> knownRooms;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public List<KnownRoom> getKnownRooms() {
        return knownRooms;
    }
}
