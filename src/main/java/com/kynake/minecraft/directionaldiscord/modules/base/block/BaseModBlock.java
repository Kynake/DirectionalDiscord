package com.kynake.minecraft.directionaldiscord.modules.base.block;

// Internal

// Minecraft
import net.minecraft.block.Block;

/**
 * Extension of a default minecraft block that superclasses all
 * blocks created in this mod
 */
public abstract class BaseModBlock extends Block {
  public BlockItemType itemType = BlockItemType.DEFAULT;

  public BaseModBlock(Properties properties) {
    super(properties);
  }

  public BlockItemType getItemType() {
    return this.itemType;
  }

  public boolean hasNoItem() {
    return this.itemType == BlockItemType.NONE;
  }

  public boolean hasDefaultItem() {
    return this.itemType == BlockItemType.DEFAULT;
  }

  public boolean hasCustomItem() {
    return this.itemType == BlockItemType.CUSTOM;
  }
}