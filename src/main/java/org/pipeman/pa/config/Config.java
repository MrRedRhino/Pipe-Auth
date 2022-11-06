package org.pipeman.pa.config;

import org.pipeman.pa.login.Token;
import org.pipeman.pconf.AbstractConfig;
import org.pipeman.pconf.ConfigProvider;

import java.nio.file.Path;

public class Config extends AbstractConfig {
    public static ConfigProvider<Config> INSTANCE = ConfigProvider.of("config.properties", Config::new);

    public static Config conf() {
        return INSTANCE.c();
    }

    public final String tokenEncryptorPassword = this.get("token-encryptor-password", "");
    public final String cookieDomain = this.get("cookie-domain", "");
    public final Path loginHtml = this.get("login-html", Path.of("static", "login.html"));
    public final int serverPort = this.get("server-port", 14000);

    public Config(String file) {
        super(file);
        store(Path.of(file), "");

        Token.setEncryptorPassword(tokenEncryptorPassword);
    }
}
