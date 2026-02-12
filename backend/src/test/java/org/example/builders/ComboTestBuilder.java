package org.example.builders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ComboTestBuilder {

    private String name = "Test Combo";
    private BigDecimal price = new BigDecimal("45.99");
    private List<Map<String, Object>> components = new ArrayList<>();

    public ComboTestBuilder() {
        // Domyślny komponent dla combo
        components.add(Map.of(
                "productId", 1L,
                "quantity", 1
        ));
    }

    public ComboTestBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ComboTestBuilder withPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public ComboTestBuilder withComponent(Long productId, Integer quantity) {
        this.components.add(Map.of(
                "productId", productId,
                "quantity", quantity
        ));
        return this;
    }

    public ComboTestBuilder withComponents(List<Map<String, Object>> components) {
        this.components = components;
        return this;
    }

    public ComboTestBuilder clearComponents() {
        this.components = new ArrayList<>();
        return this;
    }

    private String buildComponentsJson() {
        StringBuilder componentsJson = new StringBuilder("[");
        for (int i = 0; i < components.size(); i++) {
            Map<String, Object> component = components.get(i);
            componentsJson.append("""
                    {
                        "productId": %d,
                        "quantity": %d
                    }""".formatted(component.get("productId"), component.get("quantity")));
            if (i < components.size() - 1) {
                componentsJson.append(",");
            }
        }
        componentsJson.append("]");
        return componentsJson.toString();
    }

    public String buildJson() {
        return """
                {
                    "name": "%s",
                    "price": %s,
                    "components": %s
                }
                """.formatted(name, price, buildComponentsJson());
    }

    public Map<String, Object> buildMap() {
        return Map.of(
                "name", name,
                "price", price,
                "components", components
        );
    }

    /**
     * Builds a JSON string for PATCH requests.
     * Allows selective inclusion of fields (null values are omitted).
     */
    public String buildPatchJson(Boolean includeName, Boolean includePrice, Boolean includeComponents) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        if (includeName != null && includeName && name != null) {
            json.append(String.format("\"name\": \"%s\"", name));
            first = false;
        }

        if (includePrice != null && includePrice && price != null) {
            if (!first) json.append(", ");
            json.append(String.format("\"price\": %s", price));
            first = false;
        }

        if (includeComponents != null && includeComponents) {
            if (!first) json.append(", ");
            json.append("\"components\": ");
            json.append(buildComponentsJson());
        }

        json.append("}");
        return json.toString();
    }

    /**
     * Builds a full PATCH JSON with all fields.
     */
    public String buildFullPatchJson() {
        return buildPatchJson(true, true, true);
    }

    /**
     * Builds a PATCH JSON with only price (partial update).
     */
    public String buildPriceOnlyPatchJson() {
        return buildPatchJson(false, true, false);
    }

    /**
     * Builds a PATCH JSON with only name (partial update).
     */
    public String buildNameOnlyPatchJson() {
        return buildPatchJson(true, false, false);
    }
}

