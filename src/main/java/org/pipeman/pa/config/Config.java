package org.pipeman.pa.config;

import java.nio.file.Path;
import java.util.Properties;

public class Config extends ConfigHelper {
    public final String tokenEncryptorPassword = this.get("token-encryptor-password", "");
    public final String cookieDomain = this.get("cookie-domain", "");
    public final Path loginHtml = this.get("login-html", Path.of("static", "login.html"));


    public Config(Properties properties) {
        super(properties);
    }

    public static Config fromFile(Path path) {
        return new Config(loadFromFile(path));
    }
}
