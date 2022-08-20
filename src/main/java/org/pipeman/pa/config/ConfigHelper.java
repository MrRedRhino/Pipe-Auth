package org.pipeman.pa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigHelper.class);
    private final Properties properties;

    public ConfigHelper(Properties properties) {
        this.properties = properties;
    }

    public static Properties loadFromFile(Path path) {
        Properties properties = new Properties();

        File f = path.toFile();
        try {
            f.createNewFile();
        } catch (IOException ioException) {
            LOGGER.error("Failed to create config file: " + ioException);
            return new Properties();
        }

        try (InputStream inputstream = Files.newInputStream(path)) {
            properties.load(inputstream);
        } catch (IOException ioexception) {
            LOGGER.error("Failed to load config from file: " + path);
        }

        return properties;
    }

    public void store(Path source) {
        try (OutputStream outputstream = Files.newOutputStream(source)) {
            properties.store(outputstream, "Pipe-Auth Config");
        } catch (IOException ioexception) {
            LOGGER.error("Failed to store config to file: " + source);
        }
    }

    protected int get(String key, int defaultValue) {
        try {
            return Integer.parseInt(getValue(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    protected Path get(String key, Path defaultValue) {
        try {
            return Paths.get(getValue(key, defaultValue.toString()));
        } catch (InvalidPathException ignored) {
            return defaultValue;
        }
    }

    protected String get(String key, String defaultValue) {
        return getValue(key, defaultValue);
    }

    protected String get(String key) {
        return getValue(key, "");
    }

    private String getValue(String key, String defaultValue) {
        String s = (String) this.properties.get(key);
        if (s == null) {
            properties.put(key, defaultValue);
            return defaultValue;
        }
        return s;
    }
}
