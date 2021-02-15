package com.kynake.minecraft.directionablediscord.items.util;

// External Imports
import net.minecraft.item.Item;

// Internal Imports


/**
 * Extension of a default minecraft item that superclasses all
 * item created in this mod
 */
public abstract class BaseModItem extends Item {
  public BaseModItem(Properties properties) {
    super(BaseModItem.addDefaultItemProperties(properties));
  }

  public static Item.Properties defaultBlockItemProperties() {
    /* Default properties that every block-in-item-form shares */
    Item.Properties blockItemProperties = new Item.Properties();

    return BaseModItem.addDefaultItemProperties(blockItemProperties);
  }

  private static Item.Properties addDefaultItemProperties(Item.Properties properties) {
    return properties;
      /* Default properties that every Mod item shares */

      // This Mod's Creative Tab
      // .group(ModSetup.creativeTabGroup);
  }
}