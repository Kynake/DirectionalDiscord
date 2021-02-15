package com.kynake.minecraft.directionablediscord.blocks;

// Internal
import com.kynake.minecraft.directionablediscord.blocks.util.BaseModBlock;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

// Minecraft

public class Speaker extends BaseModBlock {
  public Speaker() {
    super(Properties
      .create(Material.WOOD)
      .sound(SoundType.WOOD)
      .hardnessAndResistance(2.0f)
    );

    setRegistryName("speaker");
  }
}
