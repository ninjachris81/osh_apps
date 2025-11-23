package com.osh.core.datamodel.meta;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.HashMap;
import java.util.Map;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.osh.core.actor.ActorBase;
import com.osh.core.value.ValueBase;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = KnownRoom.TABLE_NAME)
@Entity(tableName = KnownRoom.TABLE_NAME)
public class KnownRoom {

	@Ignore
	public static final String TABLE_NAME = "dm_known_rooms";

	@DatabaseField(id = true)
	@PrimaryKey
	@NotNull
	@ColumnInfo(name = "id")
	public String id;

    @DatabaseField(canBeNull = false)
	@NotNull
	@ColumnInfo(name = "name")
	public String name;

    @DatabaseField(canBeNull = true, columnName = "full_name")
    @ColumnInfo(name = "full_name")
    public String fullName;

	@DatabaseField(canBeNull = false, columnName = "known_area_id")
	@NotNull
	@ColumnInfo(name = "known_area_id")
	public String knownAreaId;

	@Ignore
	private KnownArea knownArea;

	@Ignore
	private Map<String, ActorBase> actors = new HashMap<>();

	@Ignore
	private Map<String, ValueBase> values = new HashMap<>();

	public KnownRoom() {
	}

	@Ignore
	public KnownRoom(String id, String name, String fullName) {
		this.id = id;
		this.name = name;
        this.fullName = fullName;
	}

	public String getId() {
		return id;
	}

	public String getKnownAreaId() {
		return knownAreaId;
	}

	public KnownArea getKnownArea() {
		return knownArea;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
	
	public Map<String, ActorBase> getActors() {
		return actors;
	}

	public void setActors(Map<String, ActorBase> actors) {
		this.actors = actors;
	}

	public Map<String, ValueBase> getValues() {
		return values;
	}

	public void setValues(Map<String, ValueBase> values) {
		this.values = values;
	}

	public void setKnownArea(KnownArea knownArea) {
		this.knownArea = knownArea;
		this.knownAreaId = knownArea.getId();
	}

	public void addValue(ValueBase value) {
		this.values.put(value.getFullId(), value);
	}

	public void addActor(ActorBase actor) {
		this.actors.put(actor.getFullId(), actor);
	}
}
