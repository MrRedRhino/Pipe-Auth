package org.pipeman.pa.login;

import io.github.bucket4j.Bucket;
import io.javalin.http.Context;
import io.javalin.http.Cookie;
import org.pipeman.pa.RateLimiter;
import org.pipeman.pa.Utils;
import org.pipeman.pa.config.Config;
import org.pipeman.pa.permissions.May;
import org.pipeman.pa.users.User;
import org.pipeman.pa.users.Users;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.Random;
import java.util.StringJoiner;

public class Api {
    private static final Random random = new Random();
    private static final RateLimiter ratelimit = new RateLimiter();

    public static void isAuthorized(Context ctx) {
        String path = sanitizeUrl(ctx.header("X-Original-URI"));
        String domain = ctx.header("X-Original-Domain");
        String token = ctx.cookie("token");

        boolean authorized = May.access(path, domain, Users.getDefaultUserPermissions());
        if (!authorized) {
            User user = getUserIfAuthorized(token);
            if (user != null) authorized = May.access(path, domain, user.permissions());
        }

        ctx.status(authorized ? 200 : 401).json(Map.of("authorized", authorized));
    }

    private static User getUserIfAuthorized(String cookie) {
        if (cookie == null || cookie.isBlank()) return null;

        try {
            Token token = Token.fromString(cookie);
            if (token == null || token.expiresAt() < System.currentTimeMillis()) return null;

            User user = Users.getUser(token.username());
            if (user == null || user.lastTokenUpdate() > token.createdAt()) return null;

            return user;
        } catch (Exception ignored) {
            return null;
        }
    }

    public static Context login(Context ctx) throws InterruptedException {
        Bucket bucket = ratelimit.resolveBucket(Utils.orElse(ctx.header("X-Real-IP"), ctx.ip()));
        if (!bucket.asBlocking().tryConsume(1, Duration.ofSeconds(1))) return ctx.status(429);

        Thread.sleep(150 + random.nextInt(-100, 100));
        String name = ctx.header("name");
        String password = ctx.header("password");

        User user = Users.getUser(name);
        if (user == null || !user.password().equals(password)) return returnLogin(ctx, false, null);

        long createdAt = System.currentTimeMillis();
        long expirationTime = System.currentTimeMillis() + 600_000;

        String token = new Token(name, createdAt, expirationTime).encode();
        ctx.cookie(new Cookie("token",
                token,
                "/",
                -1,
                false,
                0,
                false,
                null,
                Config.conf().cookieDomain,
                null)
        );

        return returnLogin(ctx, true, token);
    }

    public static void logout(Context ctx) {
        String token = Utils.orElse(ctx.header("token"), ctx.cookie("token"));

        Token tokenToken = Token.fromString(token);
        if (tokenToken != null) {
            User user = Users.getUser(tokenToken.username());
            if (user != null) user.lastTokenUpdate(System.currentTimeMillis());
        }
        ctx.status(200);
    }

    private static Context returnLogin(Context ctx, boolean authorized, String token) {
        return ctx.status(authorized ? 200 : 401).json(Map.of(
                "authorized", authorized,
                "token", token == null || token.isBlank() ? "" : token
        ));
    }

    private static String sanitizeUrl(String url) {
        if (url == null) return "";

        String decoded = URLDecoder.decode(url, Charset.defaultCharset());
        String[] pathParts = decoded.split("/");
        String[] out = new String[pathParts.length];
        int idx = 0;

        for (String pathPart : pathParts) {
            if (pathPart.equals(".") || pathPart.isEmpty()) continue;

            if (pathPart.equals("..")) {
                idx--;
                if (idx >= 0) out[idx] = null;
            } else {
                if (idx >= 0) out[idx] = pathPart;
                idx++;
            }
        }

        StringJoiner joiner = new StringJoiner("/");
        for (String s : out) {
            if (s != null) joiner.add(s);
        }

        if (joiner.length() == 0) return "/";
        return "/" + (decoded.charAt(decoded.length() - 1) == '/' ? joiner + "/" : joiner.toString());
    }
}
