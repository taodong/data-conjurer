package tao.dong.dataconjurer.common.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import tao.dong.dataconjurer.common.model.CompoundValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CompoundValuePropertyRetriever {
    public static String DEFAULT_QUALIFIER = "value";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder(new YAMLFactory()).build();
    static final Map<String, Map<String, String>> SUPPORTED = new HashMap<>();

    static {
        try (var inputStream = CompoundValuePropertyRetriever.class.getClassLoader().getResourceAsStream("compound.yaml")) {
            Map<String, Map<String, String>> loaded = OBJECT_MAPPER.readValue(inputStream, new TypeReference<>() {
            });
            SUPPORTED.putAll(loaded);
        } catch (IOException e) {
            LOG.error("Failed to load compound.yaml in class path, compound value support is disabled.", e);
        }

    }

    public Object getValue(CompoundValue obj, String compound, String property) {
        if (property == null) {
            property = DEFAULT_QUALIFIER;
        }
        var targetClass = getTargetClassName(compound, property);
        if (targetClass != null) {
            var jsonNode = OBJECT_MAPPER.valueToTree(obj);
            if (jsonNode.has(property)) {
                return extractPropertyValue(jsonNode.findValue(property), targetClass);
            }
        } else {
            LOG.warn("{}.{} is not supported", compound, property);
        }
        return null;
    }

    private Object extractPropertyValue(JsonNode valueNode, String type) {
        return switch (type) {
            case "java.lang.Long" -> valueNode.asLong();
            case "java.lang.Double" -> valueNode.asDouble();
            default -> valueNode.isNull() ? null : valueNode.asText();
        };
    }

    public static String getTargetClassName(String compound, String property) {
        if (SUPPORTED.containsKey(compound)) {
            return SUPPORTED.get(compound).get(property);
        }
        return null;
    }
}
