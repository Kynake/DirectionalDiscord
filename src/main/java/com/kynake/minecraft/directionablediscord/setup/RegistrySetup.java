package com.kynake.minecraft.directionablediscord.setup;

// Internal
import com.kynake.minecraft.directionablediscord.DirectionableDiscord;
import com.kynake.minecraft.directionablediscord.modules.base.item.BaseModItem;
import com.kynake.minecraft.directionablediscord.modules.lists.BlockLists;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

// Forge
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.event.RegistryEvent; // MOD Bus Event
import net.minecraftforge.eventbus.api.SubscribeEvent;

// You can use EventBusSubscriber to automatically subscribe events on the
// contained class (this is subscribing to the MOD
// Event bus for receiving Registry Events)
@Mod.EventBusSubscriber(modid = DirectionableDiscord.ModID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistrySetup {

  // Events
  @SubscribeEvent
  public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
    // Register blocks here
    IForgeRegistry<Block> registry = blockRegistryEvent.getRegistry();

    BlockLists.getBlockInstances().forEach(block -> registry.register(block));
    DirectionableDiscord.LOGGER.info("Blocks Registered");
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
    DirectionableDiscord.LOGGER.info("BlockItems Registered");
  }
}
