package com.kynake.minecraft.directionablediscord;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

// Forge
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.kynake.minecraft.directionablediscord.blocks.util.BlockLists;
import com.kynake.minecraft.directionablediscord.items.util.BaseModItem;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// You can use EventBusSubscriber to automatically subscribe events on the
// contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
  private static final Logger LOGGER = LogManager.getLogger();

  // Events
  @SubscribeEvent
  public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
    // Register blocks here
    IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();

    BlockLists.getBlockInstances().forEach(block -> registry.register(block));
    LOGGER.info("Blocks Registered");
  }

  @SubscribeEvent
  public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
    // Register items here
    IForgeRegistry<Item> registry = itemRegistryEvent.getRegistry();

    // Blocks with default item
    BlockLists.getBlocksWithDefaultItem().forEach(block -> {
      // Add block-in-item-form to Mod's creative tab
      Item.Properties properties = BaseModItem.defaultBlockItemProperties();

      Item blockItem = new BlockItem(block, properties).setRegistryName(block.getRegistryName());
      registry.register(blockItem);
    });
    LOGGER.info("BlockItems Registered");
  }
}
