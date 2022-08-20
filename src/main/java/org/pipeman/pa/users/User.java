package org.pipeman.pa.users;

public final class User {
    private final String name;
    private final String password;
    private final String[] allowedPaths;
    private final String[] deniedPaths;
    private long lastTokenUpdate;

    public User(String name, String password, String[] allowedPaths, String[] deniedPaths, long lastTokenUpdate) {
        this.name = name;
        this.password = password;
        this.allowedPaths = allowedPaths;
        this.deniedPaths = deniedPaths;
        this.lastTokenUpdate = lastTokenUpdate;
    }

    public String name() {return name;}

    public String password() {return password;}

    public String[] allowedPaths() {return allowedPaths;}

    public String[] deniedPaths() {return deniedPaths;}

    public long lastTokenUpdate() {return lastTokenUpdate;}

    public void lastTokenUpdate(long newLastTokenUpdate) {
        this.lastTokenUpdate = newLastTokenUpdate;
        Users.save();
    }
}
