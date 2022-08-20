package org.pipeman.pa.users;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Users {
    private static final Path usersFile = Path.of("users.json");
    private static Map<String, User> userCache = new HashMap<>();
    private static String[] defaultUserAllowedPaths;
    private static String[] defaultUserDeniedPaths;

    public static String[] getDefaultUserAllowedPaths() {
        if (defaultUserAllowedPaths == null) reloadCache();
        return defaultUserAllowedPaths;
    }

    public static String[] getDefaultUserDeniedPaths() {
        if (defaultUserDeniedPaths == null) reloadCache();
        return defaultUserDeniedPaths;
    }

    public static User getUser(String name) {
        if (userCache.isEmpty()) {
            reloadCache();
        }

        return userCache.get(name);
    }

    public static void reloadCache() {
        userCache = new HashMap<>();
        try {
            //noinspection ResultOfMethodCallIgnored
            usersFile.toFile().createNewFile();

            String fileContent = Files.readString(usersFile);
            JSONObject file = new JSONObject(fileContent.isBlank() ? "{}" : fileContent);

            if (file.has("default")) {
                JSONObject defaultUser = file.getJSONObject("default");
                JSONArray defaultAllowedPaths = defaultUser.getJSONArray("allowed-paths");
                JSONArray defaultDeniedPaths = defaultUser.getJSONArray("denied-paths");

                defaultUserAllowedPaths = new String[defaultAllowedPaths.length()];
                for (int i = 0; i < defaultAllowedPaths.length(); i++) {
                    defaultUserAllowedPaths[i] = (String) defaultAllowedPaths.get(i);
                }

                defaultUserDeniedPaths = new String[defaultDeniedPaths.length()];
                for (int i = 0; i < defaultDeniedPaths.length(); i++) {
                    defaultUserDeniedPaths[i] = (String) defaultDeniedPaths.get(i);
                }
            } else {
                defaultUserAllowedPaths = new String[0];
                file.put("default", new JSONArray());
            }

            if (file.has("users")) {
                JSONArray users = file.getJSONArray("users");
                for (Object o : users) {
                    User user = deserializeUser((JSONObject) o);
                    userCache.put(user.name(), user);
                }
            } else {
                file.put("users", new JSONArray());
            }

            Files.writeString(usersFile, file.toString(4));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void save() {
        JSONObject out = new JSONObject();

        out.put("default", new JSONObject(Map.of(
                "allowed-paths", defaultUserAllowedPaths,
                "denied-paths", defaultUserDeniedPaths
        )));

        JSONArray users = new JSONArray();
        for (User user : userCache.values()) {
            users.put(serialize(user));
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
        JSONArray allowedPaths = object.getJSONArray("allowed-paths");
        JSONArray deniedPaths = object.getJSONArray("denied-paths");
        long lastTokenUpdate = object.getLong("last-token-update");

        String[] aAllowedPaths = new String[allowedPaths.length()];
        for (int i = 0; i < allowedPaths.length(); i++) {
            aAllowedPaths[i] = (String) allowedPaths.get(i);
        }
        String[] aDeniedPaths = new String[deniedPaths.length()];
        for (int i = 0; i < deniedPaths.length(); i++) {
            aDeniedPaths[i] = (String) deniedPaths.get(i);
        }

        return new User(name, password, aAllowedPaths, aDeniedPaths, lastTokenUpdate);
    }

    private static JSONObject serialize(User user) {
        return new JSONObject()
                .put("name", user.name())
                .put("password", user.password())
                .put("allowed-paths", user.allowedPaths())
                .put("denied-paths", user.deniedPaths())
                .put("last-token-update", user.lastTokenUpdate());
    }
}
