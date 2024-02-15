package tao.dong.dataconjurer.common.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CompoundValuePropertyRetriever {
    public static final String DEFAULT_QUALIFIER = "value";
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder(new YAMLFactory()).build();
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Map<String, String>> supported = new HashMap<>();

    public CompoundValuePropertyRetriever() {
       loadDefaultConfig();
    }

    private void loadDefaultConfig() {
        try (var inputStream = CompoundValuePropertyRetriever.class.getClassLoader().getResourceAsStream("compound.yaml")) {
            Map<String, Map<String, String>> loaded = OBJECT_MAPPER.readValue(inputStream, new TypeReference<>() {
            });
            supported.putAll(loaded);
        } catch (IOException e) {
            LOG.error("Failed to load compound.yaml in class path, compound value support is disabled.", e);
        }
    }

    public Object getValue(CompoundValue obj, String compound, String property) {
        if (!isCompoundSupported(compound)) {
            LOG.warn("Compound type {} isn't supported", compound);
            return null;
        }

        var appliedProperty = isCompoundPropertySupported(compound, property) ? property : DEFAULT_QUALIFIER;

        var targetClass = getTargetClassName(compound, appliedProperty);
        if (targetClass != null) {
            var jsonNode = OBJECT_MAPPER.valueToTree(obj);
            if (jsonNode.has(appliedProperty)) {
                return extractPropertyValue(jsonNode.findValue(appliedProperty), targetClass);
            }
        } else {
            LOG.warn("{}.{} is not supported", compound, property);
        }
        return null;
    }

    public void loadCompoundValueConfiguration(Map<String, Map<String, String>> extraConfig) {
        if (MapUtils.isNotEmpty(extraConfig)) {
            supported.putAll(extraConfig);
        }
    }

    private Object extractPropertyValue(JsonNode valueNode, String type) {
        return switch (type) {
            case "java.lang.Long" -> valueNode.asLong();
            case "java.lang.Double" -> valueNode.asDouble();
            default -> valueNode.isNull() ? null : valueNode.asText();
        };
    }

    public String getTargetClassName(String compound, String property) {
        if (supported.containsKey(compound)) {
            return supported.get(compound).get(property);
        }
        return null;
    }

    private boolean isCompoundSupported(String compound) {
        return supported.containsKey(compound);
    }

    private boolean isCompoundPropertySupported(String compound, String property) {
        return property != null && supported.get(compound).containsKey(property);
    }
}
