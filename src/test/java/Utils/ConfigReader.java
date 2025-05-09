package Utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import recipes_Database.Databases;

public class ConfigReader {

	private static Properties prop;
	 private static final Logger logger = LoggerFactory.getLogger(ConfigReader.class);

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try (FileInputStream fis = new FileInputStream("src/test/resources/data/config.properties")) {
            prop = new Properties();
            prop.load(fis);
        } catch (IOException e) {
        	 logger.info("Failed to load config.properties: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static String getRequired(String key) {
        String value = prop.getProperty(key);
        if (value == null) {
            throw new IllegalArgumentException(key + " is not specified in config.properties.");
        }
        return value;
    }


    public static String getBrowser() {
        return getRequired("browser");
    }

    public static boolean isHeadless() {
        return Boolean.parseBoolean(getRequired("headless"));
    }

    
    public static String getBaseUrl() {
        return getRequired("baseUrl");
    }
    public static String getPaginationUrlPattern() {
        return getRequired("paginationurl");
    }
}
