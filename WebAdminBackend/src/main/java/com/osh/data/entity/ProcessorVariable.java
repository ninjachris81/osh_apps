package com.osh.data.entity;

import jakarta.persistence.Entity;

@Entity(name = "dm_processor_variables")
public class ProcessorVariable extends AbstractEntity {

    private String value;

    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }

}
