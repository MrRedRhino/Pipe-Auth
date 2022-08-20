package org.pipeman.pa;

import org.pipeman.pa.config.ConfigProvider;
import org.pipeman.pa.login.Api;
import org.pipeman.pa.login.Encryptor;
import org.rapidoid.setup.On;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static final ConfigProvider CONFIG = new ConfigProvider(Path.of("config.properties"));
    public static Encryptor tokenEncryptor;

    public static void main(String[] args) throws IOException {
        tokenEncryptor = new Encryptor(CONFIG.getConfig().tokenEncryptorPassword);

        On.port(14000);
        On.error(Throwable.class).handler((req, resp, error) -> resp.code(500).plain("13"));

        On.get("/").plain("nonsense");
        On.get("/login").html(Files.readString(CONFIG.getConfig().loginHtml));

        On.get("/api/is-authorized").json(Api::getUserIfAuthorized);
        On.get("/api/login").json(Api::login);
        On.get("/api/logout").json(Api::logout);
    }
}