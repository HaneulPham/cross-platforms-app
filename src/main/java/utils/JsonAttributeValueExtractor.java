package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class JsonAttributeValueExtractor {

    private static final Logger logger = LoggerFactory.getLogger(JsonAttributeValueExtractor.class);
    private final ObjectMapper objectMapper;

    public JsonAttributeValueExtractor() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Reads a JSON file and parses it into a tree structure.
     *
     * @param filePath Path to the JSON file.
     * @return Root JsonNode of the parsed JSON.
     * @throws IOException If the file cannot be read or parsed.
     */
    public JsonNode parseJsonFile(String filePath) throws IOException {
        validateFile(filePath);
        logger.info("Parsing JSON file: {}", filePath);
        return objectMapper.readTree(new File(filePath));
    }

    /**
     * Prints JSON as attribute-value pairs in a structured format.
     *
     * @param node      Root JsonNode to traverse.
     * @param parentKey Parent key for nested attributes (use "" for root).
     */
    public void printAttributes(JsonNode node, String parentKey) {
        if (node.isObject()) {
            // Process JSON objects
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                String key = parentKey.isEmpty() ? field.getKey() : parentKey + "." + field.getKey();
                printAttributes(field.getValue(), key);
            }
        } else if (node.isArray()) {
            // Process JSON arrays
            int index = 0;
            for (JsonNode arrayElement : node) {
                String key = parentKey + "[" + index + "]";
                printAttributes(arrayElement, key);
                index++;
            }
        } else {
            // Process leaf nodes (attribute-value pairs)
            logger.debug("Extracted: {} = {}", parentKey, node.asText());
            System.out.printf("%-50s = %s%n", parentKey, node.asText());
        }
    }

    /**
     * Validates if the file exists and is readable.
     *
     * @param filePath Path to the file to validate.
     * @throws IOException If the file does not exist or is not readable.
     */
    private void validateFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path) || !Files.isReadable(path)) {
            logger.error("File not found or not readable: {}", filePath);
            throw new IOException("File not found or not readable: " + filePath);
        }
        logger.info("File validated successfully: {}", filePath);
    }
    /**
     * Gets the file path of a resource from the resources' directory.
     *
     * @param resourceFileName Name of the resource file.
     * @return The absolute file path.
     */
    private static String getResourceFilePath(String resourceFileName){
        // Get the file URL using the class loader
        try {
            return Objects.requireNonNull(
                    JsonAttributeValueExtractor.class.getClassLoader().getResource(resourceFileName),
                    "Resource not found: " + resourceFileName
            ).toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Safely gets the value of a JSON node as a string.
     *
     * @param node The JSON node to query.
     * @param key  The key to fetch.
     * @return The value as a string, or "N/A" if the key does not exist.
     */
    private static String getSafeValue(JsonNode node, String key) {
        JsonNode valueNode = node.get(key);
        return valueNode != null ? valueNode.asText() : "N/A";
    }
    public static void main(String[] args) {
        try {

            // Example: Parse a JSON file and print attributes
            String filePath = getResourceFilePath("config/appium.config.json"); // Replace with your JSON file path
            String filePath1 = getResourceFilePath("config/appium.config.staging.json");
            JsonAttributeValueExtractor parser = new JsonAttributeValueExtractor();

            JsonNode rootNode1 = parser.parseJsonFile(filePath);
            System.out.println("Parsed JSON Attribute-Value Pairs:");
            System.out.println("=".repeat(80));
            parser.printAttributes(rootNode1, "");
            parser.printAttributes(parser.parseJsonFile(filePath1), "");

            //Approach 2
            // Parse JSON file
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(new File(filePath));

            // Navigate to the specific path
            JsonNode automationNameNode = rootNode
                    .path("appium")           // Root -> "appium"
                    .path("drivers")         // -> "drivers"
                    .path("xcuitest")        // -> "xcuitest"
                    .path("simulators")      // -> "simulators"
                    .get(0)                  // First element in the array
                    .path("automationName"); // -> "automationName"

            // Extract and print the value
            String automationName = automationNameNode.asText("N/A");
            System.out.println("automationName: " + automationName);

            // Get exact values using paths

            // Safely get exact values using paths
            String platform = getSafeValue(rootNode, "platform");
            String version = getSafeValue(rootNode, "version");
            String deviceName = getSafeValue(rootNode.path("capabilities"), "deviceName");
            String udid = getSafeValue(rootNode.path("capabilities"), "udid");
            String firstFeature = rootNode.path("capabilities").path("features").path(0).asText("Feature not found");

            // Print the extracted values
            System.out.println("Platform: " + platform);
            System.out.println("Version: " + version);
            System.out.println("Device Name: " + deviceName);
            System.out.println("UDID: " + udid);
            System.out.println("First Feature: " + firstFeature);

        } catch (IOException e) {
            logger.error("Error parsing JSON file", e);
        }
    }
}
