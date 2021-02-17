package com.kynake.minecraft.directionablediscord.setup;

// Internal
import com.kynake.discord.ListeningBot;
import com.kynake.minecraft.directionablediscord.DirectionableDiscord;
import com.kynake.minecraft.directionablediscord.modules.broadcast.AudioBroadcast;
import com.kynake.minecraft.directionablediscord.modules.lists.BlockLists;
import com.kynake.minecraft.directionablediscord.network.Networking;

// Forge
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent; // MOD Bus Event
import net.minecraftforge.fml.event.server.FMLServerStartingEvent; // FORGE Bus Event
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
// Minecraft
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

@Mod.EventBusSubscriber(modid = DirectionableDiscord.ModID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonSetup {
  static { // Add Events that trigger on the  MOD Bus, rather than the FORGE Bus
    FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onModCommonSetup);
  }

  public static ItemGroup creativeTabGroup = new ItemGroup(DirectionableDiscord.ModID) {
    @Override
    public ItemStack createIcon() {
      // Create creative tab icon from this block/item
      return new ItemStack(BlockLists.SPEAKER);
    }
  };

  // Events
  public static void onModCommonSetup(FMLCommonSetupEvent event) {
    /**
     * Runs after all blocks, TileEntities, Biomes, etc
     * from all mods have been registered
     */
    Networking.registerNetworkMessages();

  }

  @SubscribeEvent
  public static void onServerStarting(FMLServerStartingEvent event) {
    AudioBroadcast broadcaster = new AudioBroadcast();
    DirectionableDiscord.discordBot = new ListeningBot(broadcaster::sendAudioToAllPlayers);
  }
}