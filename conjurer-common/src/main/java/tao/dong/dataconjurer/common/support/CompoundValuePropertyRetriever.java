package tao.dong.dataconjurer.common.support;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import tao.dong.dataconjurer.common.model.CompoundValue;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CompoundValuePropertyRetriever {
    public static final String DEFAULT_QUALIFIER = "value";
    private static final YAMLMapper YAML_MAPPER = new YAMLMapper();
    @Getter(AccessLevel.PACKAGE)
    private final Map<String, Map<String, String>> supported = new HashMap<>();

    public CompoundValuePropertyRetriever() {
       loadDefaultConfig();
    }

    private void loadDefaultConfig() {
        try (var inputStream = CompoundValuePropertyRetriever.class.getClassLoader().getResourceAsStream("compound.yaml")) {
            Map<String, Map<String, String>> loaded = YAML_MAPPER.readValue(inputStream, new TypeReference<>() {
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
            var jsonNode = YAML_MAPPER.valueToTree(obj);
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
            default -> valueNode.isNull() ? null : valueNode.asString();
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
