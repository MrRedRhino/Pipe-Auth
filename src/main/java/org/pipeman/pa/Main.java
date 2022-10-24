package org.pipeman.pa;

import io.javalin.Javalin;
import org.pipeman.pa.config.Config;
import org.pipeman.pa.login.Api;

import java.nio.file.Files;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;


public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(c -> c.showJavalinBanner = false).start(Config.conf().serverPort);

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