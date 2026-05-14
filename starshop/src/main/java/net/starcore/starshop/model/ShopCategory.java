package net.starcore.starshop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ShopCategory {
    private final String id;
    private final String displayName;
    private final String icon;
    private final List<ShopItem> items;
    private final int pageSize;

    public ShopCategory(String id, String displayName, String icon, List<ShopItem> items, int pageSize) {
        this.id = Objects.requireNonNull(id);
        this.displayName = Objects.requireNonNull(displayName);
        this.icon = Objects.requireNonNull(icon);
        this.items = Collections.unmodifiableList(new ArrayList<>(items));
        this.pageSize = pageSize;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIcon() {
        return icon;
    }

    public List<ShopItem> getItems() {
        return items;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalPages() {
        return Math.max(1, (int) Math.ceil((double) items.size() / pageSize));
    }

    public List<ShopItem> getPage(int pageNumber) {
        int startIndex = pageNumber * pageSize;
        int endIndex = Math.min(startIndex + pageSize, items.size());
        if (startIndex >= items.size()) {
            return Collections.emptyList();
        }
        return items.subList(startIndex, endIndex);
    }
}
