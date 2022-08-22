package org.pipeman.pa.users;

import org.pipeman.pa.permissions.DomainPermission;

public final class User {
    private final String name;
    private final String password;
    private final DomainPermission[] permissions;
    private long lastTokenUpdate;

    public User(String name, String password, DomainPermission[] permissions, long lastTokenUpdate) {
        this.name = name;
        this.password = password;
        this.permissions = permissions;
        this.lastTokenUpdate = lastTokenUpdate;
    }

    public String name() {return name;}

    public String password() {return password;}

    public DomainPermission[] permissions() {return permissions;}

    public long lastTokenUpdate() {return lastTokenUpdate;}

    public void lastTokenUpdate(long newLastTokenUpdate) {
        this.lastTokenUpdate = newLastTokenUpdate;
        Users.save();
    }
}
