package org.pipeman.pa;

import io.javalin.Javalin;
import org.pipeman.pa.config.Config;
import org.pipeman.pa.login.Api;
import org.pipeman.pa.login.Encryptor;
import org.pipeman.pconf.ConfigProvider;

import java.nio.file.Files;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;


public class Main {
    public static final ConfigProvider<Config> CONFIG = ConfigProvider.of("config.properties", Config::new);
    public static final Encryptor tokenEncryptor = new Encryptor(CONFIG.c().tokenEncryptorPassword);

    public static void main(String[] args) {
        Javalin app = Javalin.create(c -> c.showJavalinBanner = false).start(CONFIG.c().serverPort);

        app.routes(() -> {
            get("", ctx -> ctx.redirect("/login"));
            get("login", ctx -> ctx.html(Files.readString(CONFIG.c().loginHtml)));

            path("api", () -> {
                get("is-authorized", Api::isAuthorized);
                get("login", Api::login);
                get("logout", Api::logout);
            });
        });
    }
}