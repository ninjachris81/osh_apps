package com.osh.user;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class User {

    public static final String TABLE_NAME = "dm_users";

    public String id;

    private String name;

    private String rights;

    public User() {
    }

    public User(@NotNull String id, @NotNull String name, String rights) {
        this.id = id;
        this.name = name;
        this.rights = rights;
    }

    public @NotNull String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public String getRights() {
        return rights;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public boolean hasRight(String right) {
        if (right != null) {
            return Arrays.asList(right.split(" ")).contains(right);
        } else {
            return false;
        }
    }
}
