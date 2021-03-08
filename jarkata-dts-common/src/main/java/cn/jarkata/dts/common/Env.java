package cn.jarkata.dts.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

public class Env {

    private static final Logger logger = LoggerFactory.getLogger(Env.class);
    private static final String BASE_PATH = "env.properties";
    private static final Properties prop = new Properties();

    static {
        InputStream stream = Env.class.getClassLoader().getResourceAsStream(BASE_PATH);
        Objects.requireNonNull(stream, "env.properties not exist");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            prop.load(reader);
        } catch (IOException e) {
            logger.error("家在文件异常", e);
        }
    }

    public static String getProperty(String key, String defualtVal) {
        String value = System.getProperty(key);
        return Optional.ofNullable(value).orElse(prop.getProperty(key, defualtVal));
    }

    public static String getProperty(String key) {
        String value = System.getProperty(key);
        return Optional.ofNullable(value).orElse(prop.getProperty(key));
    }

    public static int getIntProperty(String key, int defaultVal) {
        return Integer.parseInt(getProperty(key, String.valueOf(defaultVal)));
    }
}
