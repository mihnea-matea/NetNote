package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class ConfigUtils {

    private static final String CONFIG_FILE_NAME = "config.json";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Config readConfig() {
        File file = new File(CONFIG_FILE_NAME);
        if (!file.exists()) {
            return new Config("en");
        }
        try {
            return objectMapper.readValue(file, Config.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new Config("en");
        }
    }

    public static void writeConfig(Config config) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(CONFIG_FILE_NAME), config);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
