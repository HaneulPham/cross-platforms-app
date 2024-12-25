package utils;

import io.restassured.path.json.JsonPath;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.InputStream;
import java.io.File;
import java.util.Map;
import java.util.Objects;

public class CapabilitiesLoader {

    private static final Logger logger = LoggerFactory.getLogger(CapabilitiesLoader.class);

    public DesiredCapabilities loadCapabilities(String platform, String environment) {
        String jsonFilePath = String.format("config/capabilities/%s.%s.json", platform.toLowerCase(), environment.toLowerCase());
        String yamlFilePath = String.format("config/capabilities/%s-%s.yaml", platform.toLowerCase(), environment.toLowerCase());

        var capabilities = new DesiredCapabilities();

        try {
            // Try to load from JSON first
            InputStream is = CapabilitiesLoader.class.getClassLoader().getResourceAsStream(jsonFilePath);
            if (is != null) {
                logger.info("Loading capabilities from JSON file: {}", jsonFilePath);
                capabilities = loadFromJson(is);
            } else {
                // If JSON is not found, try YAML
                is = CapabilitiesLoader.class.getClassLoader().getResourceAsStream(yamlFilePath);
                if (is != null) {
                    logger.info("Loading capabilities from YAML file: {}", yamlFilePath);
                    capabilities = loadFromYaml(yamlFilePath);
                } else {
                    String errorMessage = String.format("Neither JSON nor YAML file found for platform: %s, environment: %s", platform, environment);
                    logger.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            }
        } catch (Exception e) {
            logger.error("Error loading capabilities for platform: {}, environment: {}", platform, environment, e);
            throw new RuntimeException(e);
        }

        return capabilities;
    }
    public DesiredCapabilities loadConfig(String environment){
        // 1. Determine which config file to load
        //    (For example, get the path from an environment variable ENV or default to local)
        String env = System.getenv(environment); // e.g., "staging", "production"
        if (env == null || env.isEmpty()) {
            env = "local"; // default
        }

        String configPath = switch (env) {
            case "staging" -> "config/appium.config.staging.json";
            case "production" -> "config/appium.config.production.json";
            default -> "config/appium.config.json"; // local
        };

        var capabilities = new DesiredCapabilities();

        try {
            // Try to load from JSON
            InputStream is = CapabilitiesLoader.class.getClassLoader().getResourceAsStream(configPath);
                logger.info("Loading configPath from JSON file: {}", configPath);
                capabilities = loadFromJson(is);
        } catch (Exception e) {
            logger.error("Error loading capabilities for environment: {}", environment, e);
            throw new RuntimeException(e);
        }

        return capabilities;
    }

    private DesiredCapabilities loadFromJson(InputStream inputStream) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        try {
            JsonPath jsonPath = new JsonPath(inputStream);

            jsonPath.getMap("").forEach((key, value) -> {
                logger.debug("Setting capability from JSON: {} = {}", key, value);
                capabilities.setCapability(key.toString(), value);
            });
        } catch (Exception e) {
            logger.error("Failed to load capabilities from JSON", e);
            throw new RuntimeException(e);
        }
        return capabilities;
    }

    private DesiredCapabilities loadFromYaml(String yamlFilePath) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        try {
            // Create an ObjectMapper for YAML
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            File yamlFile = new File(Objects.requireNonNull(
                    getClass().getClassLoader().getResource(yamlFilePath),
                    "Resource not found: " + yamlFilePath
            ).toURI());

            // Parse YAML into a Map
            @SuppressWarnings("unchecked")
            Map<String, Object> yamlData = yamlMapper.readValue(yamlFile, Map.class);

            yamlData.forEach((key, value) -> {
                logger.debug("Setting capability from YAML: {} = {}", key, value);
                capabilities.setCapability(key, value instanceof String ? value : value.toString());
            });
        } catch (Exception e) {
            logger.error("Failed to load capabilities from YAML file: {}", yamlFilePath, e);
            throw new RuntimeException(e);
        }
        return capabilities;
    }

    public static void main(String[] args) {
        CapabilitiesLoader loader = new CapabilitiesLoader();

        // Example: Load Android capabilities for "dev" environment
        try {
            DesiredCapabilities capabilities = loader.loadCapabilities("android", "dev");
            DesiredCapabilities capabilities1 = loader.loadConfig("local");
            logger.info("Loaded capabilities: {}", capabilities);
            System.out.println("Loaded capabilities: " + capabilities);
            System.out.println("Loaded capabilities: " + capabilities1);
        } catch (Exception e) {
            logger.error("Failed to load capabilities", e);
        }
    }
}
