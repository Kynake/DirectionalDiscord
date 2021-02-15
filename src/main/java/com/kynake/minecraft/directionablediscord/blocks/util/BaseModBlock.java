package com.kynake.minecraft.directionablediscord.blocks.util;

// Internal

// Minecraft
import net.minecraft.block.Block;

/**
 * Extension of a default minecraft block that superclasses all
 * blocks created in this mod
 */
public abstract class BaseModBlock extends Block {
  protected ItemType itemType = ItemType.DEFAULT;

  public BaseModBlock(Properties properties) {
    super(properties);
  }

  public ItemType getItemType() {
    return this.itemType;
  }

  public boolean hasNoItem() {
    return this.itemType == ItemType.NONE;
  }

  public boolean hasDefaultItem() {
    return this.itemType == ItemType.DEFAULT;
  }

  public boolean hasCustomItem() {
    return this.itemType == ItemType.CUSTOM;
  }
}