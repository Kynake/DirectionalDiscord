package com.kynake.minecraft.directionablediscord.setup;

// Internal
import com.kynake.minecraft.directionablediscord.DirectionableDiscord;
import com.kynake.minecraft.directionablediscord.blocks.util.BlockLists;

// Minecraft
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModSetup {

  public static ItemGroup creativeTabGroup = new ItemGroup(DirectionableDiscord.ModID) {
    @Override
    public ItemStack createIcon() {
      // Create creative tab icon from this block/item
      return new ItemStack(BlockLists.SPEAKER);
    }
  };

  public void init() {

  }
}