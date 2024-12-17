package utils;

import io.restassured.path.json.JsonPath;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.File;
import java.util.Map;
import java.util.Objects;

public class CapabilitiesLoader {

    public DesiredCapabilities loadCapabilities(String platform, String environment) {
        String jsonFilePath = String.format("config/capabilities/%s-%s.json", platform.toLowerCase(), environment.toLowerCase());
        String yamlFilePath = String.format("config/capabilities/%s-%s.yaml", platform.toLowerCase(), environment.toLowerCase());

        var capabilities = new DesiredCapabilities();

        // Try to load from JSON first
        InputStream is = CapabilitiesLoader.class.getClassLoader().getResourceAsStream(jsonFilePath);
        if (is != null) {
            capabilities = loadFromJson(is);
        } else {
            // If JSON is not found, try YAML
            is = CapabilitiesLoader.class.getClassLoader().getResourceAsStream(yamlFilePath);
            if (is != null) {
                capabilities = loadFromYaml(yamlFilePath);
            } else {
                throw new RuntimeException("Neither JSON nor YAML file found for platform: " + platform + ", environment: " + environment);
            }
        }

        return capabilities;
    }

    private DesiredCapabilities loadFromJson(InputStream inputStream) {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        JsonPath jsonPath = new JsonPath(inputStream);

        jsonPath.getMap("").forEach((key, value) ->
                capabilities.setCapability(key.toString(), value)
        );

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

            yamlData.forEach((key, value) ->
                    capabilities.setCapability(key, value instanceof String ? value : value.toString())
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to load capabilities from YAML file: " + yamlFilePath, e);
        }
        return capabilities;
    }
    public static void main(String[] args) {
        CapabilitiesLoader loader = new CapabilitiesLoader();

        // Example: Load Android capabilities for "dev" environment
        DesiredCapabilities capabilities = loader.loadCapabilities("android", "dev");
        System.out.println(capabilities);
    }
}


