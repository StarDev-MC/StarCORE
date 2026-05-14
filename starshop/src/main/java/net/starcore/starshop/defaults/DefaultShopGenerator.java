package net.starcore.starshop.defaults;

import net.starcore.starshop.model.Shop;
import net.starcore.starshop.model.ShopCategory;
import net.starcore.starshop.model.ShopItem;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultShopGenerator {
    private static final int PAGE_SIZE = 45;

    public static Shop generateDefaultShop() {
        Shop shop = new Shop("default", "StarMC Shop");
        shop.addCategory(createArmorCategory());
        shop.addCategory(createWeaponsCategory());
        shop.addCategory(createToolsCategory());
        shop.addCategory(createFoodCategory());
        shop.addCategory(createBlocksCategory());
        shop.addCategory(createBuildingBlocksCategory());
        shop.addCategory(createDecorationCategory());
        shop.addCategory(createFarmingCategory());
        shop.addCategory(createMobDropsCategory());
        shop.addCategory(createOresAndMineralsCategory());
        shop.addCategory(createRedstoneCategory());
        shop.addCategory(createTransportationCategory());
        shop.addCategory(createBrewingCategory());
        shop.addCategory(createMiscellaneousCategory());
        shop.addCategory(createAllItemsCategory(shop));
        return shop;
    }

    private static ShopCategory createArmorCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("LEATHER_HELMET", 80, 40));
        items.add(item("LEATHER_CHESTPLATE", 150, 75));
        items.add(item("LEATHER_LEGGINGS", 140, 70));
        items.add(item("LEATHER_BOOTS", 80, 40));
        items.add(item("CHAINMAIL_HELMET", 200, 100));
        items.add(item("CHAINMAIL_CHESTPLATE", 400, 200));
        items.add(item("CHAINMAIL_LEGGINGS", 380, 190));
        items.add(item("CHAINMAIL_BOOTS", 200, 100));
        items.add(item("IRON_HELMET", 350, 175));
        items.add(item("IRON_CHESTPLATE", 700, 350));
        items.add(item("IRON_LEGGINGS", 650, 325));
        items.add(item("IRON_BOOTS", 350, 175));
        items.add(item("DIAMOND_HELMET", 1200, 600));
        items.add(item("DIAMOND_CHESTPLATE", 2400, 1200));
        items.add(item("DIAMOND_LEGGINGS", 2200, 1100));
        items.add(item("DIAMOND_BOOTS", 1200, 600));
        items.add(item("NETHERITE_HELMET", 3600, 1800));
        items.add(item("NETHERITE_CHESTPLATE", 7200, 3600));
        items.add(item("NETHERITE_LEGGINGS", 6600, 3300));
        items.add(item("NETHERITE_BOOTS", 3600, 1800));
        return new ShopCategory("armor", "§6Armor", "IRON_CHESTPLATE", items, PAGE_SIZE);
    }

    private static ShopCategory createWeaponsCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("WOODEN_SWORD", 30, 15));
        items.add(item("STONE_SWORD", 60, 30));
        items.add(item("IRON_SWORD", 300, 150));
        items.add(item("DIAMOND_SWORD", 1000, 500));
        items.add(item("NETHERITE_SWORD", 3000, 1500));
        items.add(item("WOODEN_AXE", 40, 20));
        items.add(item("STONE_AXE", 80, 40));
        items.add(item("IRON_AXE", 350, 175));
        items.add(item("DIAMOND_AXE", 1100, 550));
        items.add(item("NETHERITE_AXE", 3200, 1600));
        items.add(item("BOW", 200, 100));
        items.add(item("ARROW", 2, 1));
        items.add(item("CROSSBOW", 400, 200));
        items.add(item("TRIDENT", 500, 250));
        items.add(item("MACE", 800, 400));
        return new ShopCategory("weapons", "§cWeapons", "IRON_SWORD", items, PAGE_SIZE);
    }

    private static ShopCategory createToolsCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("WOODEN_PICKAXE", 35, 17));
        items.add(item("STONE_PICKAXE", 70, 35));
        items.add(item("IRON_PICKAXE", 320, 160));
        items.add(item("DIAMOND_PICKAXE", 1050, 525));
        items.add(item("NETHERITE_PICKAXE", 3100, 1550));
        items.add(item("WOODEN_SHOVEL", 25, 12));
        items.add(item("STONE_SHOVEL", 50, 25));
        items.add(item("IRON_SHOVEL", 280, 140));
        items.add(item("DIAMOND_SHOVEL", 950, 475));
        items.add(item("NETHERITE_SHOVEL", 2900, 1450));
        items.add(item("WOODEN_HOE", 25, 12));
        items.add(item("STONE_HOE", 50, 25));
        items.add(item("IRON_HOE", 280, 140));
        items.add(item("DIAMOND_HOE", 950, 475));
        items.add(item("NETHERITE_HOE", 2900, 1450));
        items.add(item("SHEARS", 100, 50));
        return new ShopCategory("tools", "§eTools", "IRON_PICKAXE", items, PAGE_SIZE);
    }

    private static ShopCategory createFoodCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("COOKED_BEEF", 8, 4));
        items.add(item("COOKED_PORK", 8, 4));
        items.add(item("COOKED_CHICKEN", 6, 3));
        items.add(item("COOKED_SALMON", 10, 5));
        items.add(item("COOKED_COD", 8, 4));
        items.add(item("STEAK", 12, 6));
        items.add(item("WHEAT", 2, 1));
        items.add(item("BREAD", 6, 3));
        items.add(item("CARROT", 4, 2));
        items.add(item("POTATO", 4, 2));
        items.add(item("BAKED_POTATO", 6, 3));
        items.add(item("APPLE", 10, 5));
        items.add(item("GOLDEN_APPLE", 500, 250));
        items.add(item("ENCHANTED_GOLDEN_APPLE", 5000, 2500));
        items.add(item("BEETROOT", 3, 1));
        items.add(item("BEETROOT_SOUP", 12, 6));
        items.add(item("MUSHROOM_STEW", 15, 7));
        items.add(item("PUMPKIN_PIE", 20, 10));
        items.add(item("COOKIE", 3, 1));
        items.add(item("MELON", 3, 1));
        return new ShopCategory("food", "§6Food", "COOKED_BEEF", items, PAGE_SIZE);
    }

    private static ShopCategory createBlocksCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("OAK_LOG", 8, 4));
        items.add(item("OAK_WOOD", 12, 6));
        items.add(item("OAK_PLANKS", 3, 1));
        items.add(item("BIRCH_LOG", 8, 4));
        items.add(item("BIRCH_WOOD", 12, 6));
        items.add(item("BIRCH_PLANKS", 3, 1));
        items.add(item("SPRUCE_LOG", 8, 4));
        items.add(item("SPRUCE_WOOD", 12, 6));
        items.add(item("SPRUCE_PLANKS", 3, 1));
        items.add(item("DARK_OAK_LOG", 10, 5));
        items.add(item("DARK_OAK_WOOD", 15, 7));
        items.add(item("DARK_OAK_PLANKS", 4, 2));
        items.add(item("STONE", 3, 1));
        items.add(item("COBBLESTONE", 2, 1));
        items.add(item("DEEPSLATE", 5, 2));
        items.add(item("COBBLED_DEEPSLATE", 5, 2));
        items.add(item("STONE_BRICKS", 8, 4));
        items.add(item("MOSSY_STONE_BRICKS", 12, 6));
        items.add(item("CRACKED_STONE_BRICKS", 10, 5));
        items.add(item("CHISELED_STONE_BRICKS", 10, 5));
        items.add(item("OAK_LEAVES", 5, 2));
        items.add(item("DIRT", 1, 0));
        items.add(item("GRASS_BLOCK", 3, 1));
        items.add(item("PODZOL", 5, 2));
        items.add(item("MYCELIUM", 10, 5));
        return new ShopCategory("blocks", "§8Blocks", "STONE", items, PAGE_SIZE);
    }

    private static ShopCategory createBuildingBlocksCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("GLASS", 10, 5));
        items.add(item("TINTED_GLASS", 15, 7));
        items.add(item("WHITE_CONCRETE", 12, 6));
        items.add(item("ORANGE_CONCRETE", 12, 6));
        items.add(item("MAGENTA_CONCRETE", 12, 6));
        items.add(item("LIGHT_BLUE_CONCRETE", 12, 6));
        items.add(item("YELLOW_CONCRETE", 12, 6));
        items.add(item("LIME_CONCRETE", 12, 6));
        items.add(item("PINK_CONCRETE", 12, 6));
        items.add(item("GRAY_CONCRETE", 12, 6));
        items.add(item("LIGHT_GRAY_CONCRETE", 12, 6));
        items.add(item("CYAN_CONCRETE", 12, 6));
        items.add(item("PURPLE_CONCRETE", 12, 6));
        items.add(item("BLUE_CONCRETE", 12, 6));
        items.add(item("BROWN_CONCRETE", 12, 6));
        items.add(item("GREEN_CONCRETE", 12, 6));
        items.add(item("RED_CONCRETE", 12, 6));
        items.add(item("BLACK_CONCRETE", 12, 6));
        items.add(item("WHITE_TERRACOTTA", 8, 4));
        items.add(item("ORANGE_TERRACOTTA", 8, 4));
        items.add(item("MAGENTA_TERRACOTTA", 8, 4));
        items.add(item("OAK_STAIRS", 8, 4));
        items.add(item("STONE_STAIRS", 8, 4));
        items.add(item("BRICK_STAIRS", 10, 5));
        items.add(item("OAK_SLAB", 5, 2));
        items.add(item("STONE_SLAB", 5, 2));
        items.add(item("BRICK_WALL", 12, 6));
        items.add(item("STONE_WALL", 12, 6));
        return new ShopCategory("building_blocks", "§bBuilding Blocks", "GLASS", items, PAGE_SIZE);
    }

    private static ShopCategory createDecorationCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("LANTERN", 25, 12));
        items.add(item("SOUL_LANTERN", 30, 15));
        items.add(item("TORCH", 3, 1));
        items.add(item("SOUL_TORCH", 4, 2));
        items.add(item("CANDLE", 8, 4));
        items.add(item("CAMPFIRE", 30, 15));
        items.add(item("SOUL_CAMPFIRE", 35, 17));
        items.add(item("ITEM_FRAME", 20, 10));
        items.add(item("GLOW_ITEM_FRAME", 30, 15));
        items.add(item("PAINTING", 15, 7));
        items.add(item("ARMOR_STAND", 50, 25));
        items.add(item("FLOWER_POT", 10, 5));
        items.add(item("POPPY", 5, 2));
        items.add(item("CORNFLOWER", 5, 2));
        items.add(item("WITHER_ROSE", 20, 10));
        items.add(item("AZALEA", 15, 7));
        items.add(item("HANGING_ROOTS", 10, 5));
        items.add(item("SMALL_DRIPLEAF", 8, 4));
        items.add(item("BIG_DRIPLEAF", 15, 7));
        items.add(item("AMETHYST_BLOCK", 200, 100));
        items.add(item("AMETHYST_CLUSTER", 150, 75));
        return new ShopCategory("decoration", "§dDecoration", "LANTERN", items, PAGE_SIZE);
    }

    private static ShopCategory createFarmingCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("WHEAT_SEEDS", 3, 1));
        items.add(item("CARROT", 4, 2));
        items.add(item("POTATO", 4, 2));
        items.add(item("BEETROOT_SEEDS", 3, 1));
        items.add(item("MELON_SEEDS", 5, 2));
        items.add(item("PUMPKIN_SEEDS", 5, 2));
        items.add(item("FARMLAND", 5, 2));
        items.add(item("HAY_BLOCK", 15, 7));
        items.add(item("MOSS_BLOCK", 20, 10));
        items.add(item("BONE_MEAL", 10, 5));
        items.add(item("COMPOSTER", 30, 15));
        items.add(item("CHEST", 40, 20));
        items.add(item("BARREL", 35, 17));
        items.add(item("HOE", 150, 75));
        return new ShopCategory("farming", "§2Farming", "WHEAT_SEEDS", items, PAGE_SIZE);
    }

    private static ShopCategory createMobDropsCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("STRING", 5, 2));
        items.add(item("SPIDER_EYE", 15, 7));
        items.add(item("FERMENTED_SPIDER_EYE", 30, 15));
        items.add(item("GUNPOWDER", 20, 10));
        items.add(item("BONE", 10, 5));
        items.add(item("ROTTEN_FLESH", 5, 2));
        items.add(item("ENDER_PEARL", 100, 50));
        items.add(item("ENDERMAN_HEAD", 500, 250));
        items.add(item("BLAZE_ROD", 80, 40));
        items.add(item("BLAZE_POWDER", 40, 20));
        items.add(item("GHAST_TEAR", 200, 100));
        items.add(item("SLIMEBALL", 20, 10));
        items.add(item("MAGMA_CREAM", 40, 20));
        items.add(item("WITHER_SKELETON_SKULL", 600, 300));
        items.add(item("CREEPER_HEAD", 500, 250));
        items.add(item("ZOMBIE_HEAD", 500, 250));
        items.add(item("SKELETON_SKULL", 500, 250));
        items.add(item("PLAYER_HEAD", 1000, 500));
        return new ShopCategory("mob_drops", "§5Mob Drops", "SPIDER_EYE", items, PAGE_SIZE);
    }

    private static ShopCategory createOresAndMineralsCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("RAW_IRON", 30, 15));
        items.add(item("IRON_ORE", 40, 20));
        items.add(item("DEEPSLATE_IRON_ORE", 40, 20));
        items.add(item("RAW_COPPER", 20, 10));
        items.add(item("COPPER_ORE", 25, 12));
        items.add(item("DEEPSLATE_COPPER_ORE", 25, 12));
        items.add(item("RAW_GOLD", 100, 50));
        items.add(item("GOLD_ORE", 120, 60));
        items.add(item("DEEPSLATE_GOLD_ORE", 120, 60));
        items.add(item("COAL", 15, 7));
        items.add(item("COAL_ORE", 20, 10));
        items.add(item("DEEPSLATE_COAL_ORE", 20, 10));
        items.add(item("LAPIS_ORE", 150, 75));
        items.add(item("LAPIS_LAZULI", 120, 60));
        items.add(item("DIAMOND_ORE", 1500, 750));
        items.add(item("DEEPSLATE_DIAMOND_ORE", 1500, 750));
        items.add(item("DIAMOND", 1200, 600));
        items.add(item("EMERALD_ORE", 2000, 1000));
        items.add(item("EMERALD", 1800, 900));
        items.add(item("REDSTONE_ORE", 100, 50));
        items.add(item("DEEPSLATE_REDSTONE_ORE", 100, 50));
        items.add(item("REDSTONE", 80, 40));
        items.add(item("ANCIENT_DEBRIS", 5000, 2500));
        items.add(item("NETHERITE_SCRAP", 3000, 1500));
        items.add(item("NETHERITE_INGOT", 12000, 6000));
        return new ShopCategory("ores_minerals", "§7Ores & Minerals", "DIAMOND", items, PAGE_SIZE);
    }

    private static ShopCategory createRedstoneCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("REDSTONE", 80, 40));
        items.add(item("REDSTONE_BLOCK", 800, 400));
        items.add(item("REPEATER", 50, 25));
        items.add(item("COMPARATOR", 60, 30));
        items.add(item("PISTON", 80, 40));
        items.add(item("STICKY_PISTON", 100, 50));
        items.add(item("OBSERVER", 100, 50));
        items.add(item("DISPENSER", 120, 60));
        items.add(item("DROPPER", 100, 50));
        items.add(item("HOPPER", 150, 75));
        items.add(item("REDSTONE_LAMP", 80, 40));
        items.add(item("LEVER", 10, 5));
        items.add(item("STONE_BUTTON", 5, 2));
        items.add(item("CHEST", 40, 20));
        items.add(item("BARREL", 35, 17));
        items.add(item("RAIL", 15, 7));
        items.add(item("POWERED_RAIL", 40, 20));
        items.add(item("DETECTOR_RAIL", 50, 25));
        items.add(item("ACTIVATOR_RAIL", 50, 25));
        items.add(item("TNT", 200, 100));
        items.add(item("SLIME_BLOCK", 100, 50));
        items.add(item("HONEY_BLOCK", 120, 60));
        items.add(item("TARGET", 30, 15));
        return new ShopCategory("redstone", "§4Redstone", "REDSTONE", items, PAGE_SIZE);
    }

    private static ShopCategory createTransportationCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("SADDLE", 200, 100));
        items.add(item("MINECART", 150, 75));
        items.add(item("CHEST_MINECART", 200, 100));
        items.add(item("FURNACE_MINECART", 200, 100));
        items.add(item("HOPPER_MINECART", 250, 125));
        items.add(item("POWERED_RAIL", 40, 20));
        items.add(item("RAIL", 15, 7));
        items.add(item("ACTIVATOR_RAIL", 50, 25));
        items.add(item("DETECTOR_RAIL", 50, 25));
        items.add(item("SPRUCE_BOAT", 50, 25));
        items.add(item("OAK_BOAT", 50, 25));
        items.add(item("DARK_OAK_BOAT", 50, 25));
        items.add(item("BIRCH_BOAT", 50, 25));
        return new ShopCategory("transportation", "§3Transportation", "MINECART", items, PAGE_SIZE);
    }

    private static ShopCategory createBrewingCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("BREWING_STAND", 80, 40));
        items.add(item("CAULDRON", 60, 30));
        items.add(item("NETHER_WART", 50, 25));
        items.add(item("REDSTONE", 80, 40));
        items.add(item("GLOWSTONE_DUST", 60, 30));
        items.add(item("DRAGON_BREATH", 300, 150));
        items.add(item("FERMENTED_SPIDER_EYE", 30, 15));
        items.add(item("BLAZE_POWDER", 40, 20));
        items.add(item("SUGAR", 5, 2));
        items.add(item("RABBIT_FOOT", 100, 50));
        items.add(item("PHANTOM_MEMBRANE", 150, 75));
        items.add(item("PUFFERFISH", 50, 25));
        return new ShopCategory("brewing", "§5Brewing", "BREWING_STAND", items, PAGE_SIZE);
    }

    private static ShopCategory createMiscellaneousCategory() {
        List<ShopItem> items = new ArrayList<>();
        items.add(item("STICK", 2, 1));
        items.add(item("SAND", 5, 2));
        items.add(item("RED_SAND", 5, 2));
        items.add(item("GRAVEL", 5, 2));
        items.add(item("SOUL_SAND", 15, 7));
        items.add(item("OBSIDIAN", 500, 250));
        items.add(item("CRYING_OBSIDIAN", 600, 300));
        items.add(item("LAVA_BUCKET", 400, 200));
        items.add(item("WATER_BUCKET", 50, 25));
        items.add(item("BUCKET", 30, 15));
        items.add(item("ENDER_CHEST", 500, 250));
        items.add(item("ENCHANTING_TABLE", 800, 400));
        items.add(item("ANVIL", 400, 200));
        items.add(item("CRAFTING_TABLE", 50, 25));
        items.add(item("FURNACE", 60, 30));
        items.add(item("SMOKER", 70, 35));
        items.add(item("BLAST_FURNACE", 80, 40));
        items.add(item("BOOK", 10, 5));
        items.add(item("WRITTEN_BOOK", 50, 25));
        items.add(item("NAME_TAG", 200, 100));
        items.add(item("SADDLE", 200, 100));
        items.add(item("MAP", 100, 50));
        items.add(item("COMPASS", 100, 50));
        items.add(item("CLOCK", 150, 75));
        return new ShopCategory("miscellaneous", "§eOther", "CRAFTING_TABLE", items, PAGE_SIZE);
    }

    private static ShopCategory createAllItemsCategory(Shop shop) {
        Map<String, ShopItem> allItems = new LinkedHashMap<>();
        for (ShopCategory category : shop.getCategories().values()) {
            if (!category.getId().equals("all")) {
                for (ShopItem item : category.getItems()) {
                    allItems.putIfAbsent(item.getMaterial(), item);
                }
            }
        }
        List<ShopItem> items = new ArrayList<>(allItems.values());
        return new ShopCategory("all", "§fAll Items", "CHEST", items, PAGE_SIZE);
    }

    private static ShopItem item(String material, long buy, long sell) {
        return new ShopItem(material, buy, sell, 64, null, null);
    }
}
