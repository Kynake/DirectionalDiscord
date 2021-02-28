package kynake.minecraft.directionaldiscord.setup;

// Internal
import kynake.discord.ListeningBot;
import kynake.minecraft.directionaldiscord.DirectionalDiscord;
import kynake.minecraft.directionaldiscord.modules.broadcast.AudioBroadcast;
import kynake.minecraft.directionaldiscord.modules.lists.BlockLists;
import kynake.minecraft.directionaldiscord.network.Networking;

// Forge
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent; // MOD Bus Event
import net.minecraftforge.fml.event.server.FMLServerStartingEvent; // FORGE Bus Event
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// Minecraft
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

@Mod.EventBusSubscriber(modid = DirectionalDiscord.ModID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CommonSetup {
  static { // Add Events that trigger on the  MOD Bus, rather than the FORGE Bus
    FMLJavaModLoadingContext.get().getModEventBus().addListener(CommonSetup::onModCommonSetup);
  }

  public static ItemGroup creativeTabGroup = new ItemGroup(DirectionalDiscord.ModID) {
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
    // Start Discord Bot
    AudioBroadcast broadcaster = new AudioBroadcast();
    DirectionalDiscord.discordBot = new ListeningBot(broadcaster::sendAudioToOtherPlayers);
  }

  @SubscribeEvent
  public static void onServerStopping(FMLServerStoppingEvent event) {
    // Stop Discord Bot
    DirectionalDiscord.discordBot.shutdown();
  }
}