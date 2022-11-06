package org.pipeman.pa;

import io.javalin.Javalin;
import org.pipeman.pa.config.Config;
import org.pipeman.pa.console.CommandManager;
import org.pipeman.pa.login.Api;
import org.pipeman.pa.users.Users;

import java.nio.file.Files;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;


public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(c -> c.showJavalinBanner = false).start(Config.conf().serverPort);

        CommandManager.init();
        try {
            Users.reloadCache();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load users.json", e);
        }

        app.routes(() -> {
            get("", ctx -> ctx.redirect("/login"));
            get("login", ctx -> ctx.html(Files.readString(Config.conf().loginHtml)));

            path("api", () -> {
                get("is-authorized", Api::isAuthorized);
                get("login", Api::login);
                get("logout", Api::logout);
            });
        });
    }
}