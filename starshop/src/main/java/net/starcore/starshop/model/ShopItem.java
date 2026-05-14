package net.starcore.starshop.model;

import java.util.Objects;

public class ShopItem {
    private final String material;
    private final long buyPrice;
    private final long sellPrice;
    private final int maxStackSize;
    private final String displayName;
    private final String lore;

    public ShopItem(String material, long buyPrice, long sellPrice, int maxStackSize, String displayName, String lore) {
        this.material = Objects.requireNonNull(material);
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.maxStackSize = maxStackSize;
        this.displayName = displayName;
        this.lore = lore;
    }

    public String getMaterial() {
        return material;
    }

    public long getBuyPrice() {
        return buyPrice;
    }

    public long getSellPrice() {
        return sellPrice;
    }

    public int getMaxStackSize() {
        return maxStackSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLore() {
        return lore;
    }

    public boolean isBuyable() {
        return buyPrice > 0;
    }

    public boolean isSellable() {
        return sellPrice > 0;
    }
}
