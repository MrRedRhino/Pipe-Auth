package org.pipeman.pa.users;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeman.pa.permissions.DomainPermission;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private static final Path usersFile = Path.of("users.json");
    private static Map<String, User> userCache = new HashMap<>();
    private static DomainPermission[] defaultUserPermissions;

    public static DomainPermission[] getDefaultUserPermissions() {
        return defaultUserPermissions;
    }

    public static User getUser(String name) {
        return userCache.get(name);
    }

    public static void reloadCache() throws Exception {
        Map<String, User> newUsers = new HashMap<>();
        //noinspection ResultOfMethodCallIgnored
        usersFile.toFile().createNewFile();

        String fileContent = Files.readString(usersFile);
        JSONObject file = new JSONObject(fileContent.isBlank() ? "{}" : fileContent);

        if (file.has("default")) {
            JSONArray defaultUserPerms = file.getJSONArray("default");

            defaultUserPermissions = new DomainPermission[defaultUserPerms.length()];
            for (int i = 0; i < defaultUserPerms.length(); i++) {
                JSONObject perm = defaultUserPerms.getJSONObject(i);
                defaultUserPermissions[i] = DomainPermission.from(perm);
            }
        } else {
            defaultUserPermissions = new DomainPermission[0];
            file.put("default", new JSONArray());
        }

        if (file.has("users")) {
            JSONArray users = file.getJSONArray("users");
            for (Object o : users) {
                User user = deserializeUser((JSONObject) o);
                newUsers.put(user.name(), user);
            }
        } else {
            file.put("users", new JSONArray());
        }

        Files.writeString(usersFile, file.toString(4));
        userCache = newUsers;
    }

    public static void save() {
        JSONObject out = new JSONObject();

        out.put("default", new JSONArray(defaultUserPermissions));

        JSONArray users = new JSONArray();
        for (User user : userCache.values()) {
            users.put(user.serialize());
        }

        out.put("users", users);

        try {
            Files.writeString(usersFile, out.toString(4));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static User deserializeUser(JSONObject object) {
        String name = object.getString("name");
        String password = object.getString("password");
        long lastTokenUpdate = object.getLong("last-token-update");

        JSONArray domainPermissions = object.getJSONArray("domain-permissions");
        DomainPermission[] perms = new DomainPermission[domainPermissions.length()];
        for (int i = 0; i < perms.length; i++) {
            perms[i] = DomainPermission.from(domainPermissions.getJSONObject(i));
        }

        return new User(name, password, perms, lastTokenUpdate);
    }
}
