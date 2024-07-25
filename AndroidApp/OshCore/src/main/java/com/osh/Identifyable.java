package com.osh;

import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import org.jetbrains.annotations.NotNull;

public abstract class Identifyable {

	@Ignore
	public final String PROPERTY_ID = "id";

	@PrimaryKey
	@NotNull
	protected String id;

	public Identifyable(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
