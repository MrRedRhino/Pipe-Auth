package org.pipeman.pa.login;

import org.pipeman.pa.Main;
import org.pipeman.pa.users.May;
import org.pipeman.pa.users.User;
import org.pipeman.pa.users.Users;
import org.rapidoid.http.Req;
import org.rapidoid.http.Resp;

import java.util.Map;
import java.util.Random;

public class Api {
    private static final Random random = new Random();

    public static Object isAuthorized(Req req, Resp resp) throws InterruptedException {
        String originalPath = req.header("X-Original-URI", null);
        String token = req.cookie("token", req.header("token", null));

        boolean authorized = May.access(originalPath, Users.getDefaultUserAllowedPaths(), Users.getDefaultUserDeniedPaths());
        User user = getUserIfAuthorized(token);
        if (user != null) {
            authorized = May.access(originalPath, user.allowedPaths(), user.deniedPaths());
        }

        Thread.sleep(30 + random.nextInt(-20, 20));
        return returnAuthorized(resp, authorized);
    }

    private static User getUserIfAuthorized(String cookie) {
        if (cookie == null || cookie.isBlank()) return null;

        try {
            Token token = Token.fromString(cookie);
            if (token == null) return null;

            if (token.expiresAt() < System.currentTimeMillis()) return null;

            User user = Users.getUser(token.username());
            if (user == null) return null;
            if (user.lastTokenUpdate() > token.createdAt()) return null;

            return user;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Object login(Req req, Resp resp) throws InterruptedException {
        Thread.sleep(500 + random.nextInt(-100, 100));
        String name = req.header("name", null);
        String password = req.header("password", null);

        User user = Users.getUser(name);
        if (user == null || !user.password().equals(password)) return loginReturn(resp, false, null);

        long createdAt = System.currentTimeMillis();
        long expirationTime = System.currentTimeMillis() + 600_000;

        String token = new Token(name, createdAt, expirationTime).encode();
        resp.cookie("token", token + "; Domain=" + Main.CONFIG.getConfig().cookieDomain);

        return loginReturn(resp, true, token);
    }

    public static Object logout(Req req, Resp resp) {
        String token = req.header("token", req.cookie("token", null));

        Token tokenToken = Token.fromString(token);
        if (tokenToken != null) {
            User user = Users.getUser(tokenToken.username());
            if (user != null) {
                user.lastTokenUpdate(System.currentTimeMillis());
            }
        }
        return resp.code(200).plain("");
    }

    private static Resp returnAuthorized(Resp resp, boolean authorized) {
        return resp.code(authorized ? 200 : 401).json(Map.of("authorized", authorized));
    }

    private static Resp loginReturn(Resp resp, boolean authorized, String token) {
        return resp.code(authorized ? 200 : 401).json(Map.of(
                "authorized", authorized,
                "token", token == null || token.isBlank() ? "" : token
        ));
    }
}
