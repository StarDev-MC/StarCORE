package net.starcore.starshop.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Shop {
    private final String id;
    private final String name;
    private final Map<String, ShopCategory> categories;

    public Shop(String id, String name) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.categories = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void addCategory(ShopCategory category) {
        categories.put(category.getId(), category);
    }

    public ShopCategory getCategory(String categoryId) {
        return categories.get(categoryId);
    }

    public Map<String, ShopCategory> getCategories() {
        return new HashMap<>(categories);
    }

    public ShopCategory getCategoryByIndex(int index) {
        if (index < 0 || index >= categories.size()) {
            return null;
        }
        return categories.values().stream().skip(index).findFirst().orElse(null);
    }

    public int getTotalCategories() {
        return categories.size();
    }
}
