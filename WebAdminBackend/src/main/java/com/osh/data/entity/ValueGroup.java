package com.osh.data.entity;

import jakarta.persistence.Entity;

@Entity(name = "dm_value_groups")
public class ValueGroup extends AbstractEntity {

    private String comment;

    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

}
