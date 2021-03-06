package kynake.minecraft.directionaldiscord.setup;

// Internal
import kynake.audio.SimpleAudioPlayer;
import kynake.minecraft.directionaldiscord.DirectionalDiscord;

// Forge
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent; // MOD Bus Event
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = DirectionalDiscord.ModID, value = { Dist.CLIENT }, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientSetup {
  private static final Logger LOGGER = LogManager.getLogger();

  static { // Add Events that trigger on the  MOD Bus, rather than the FORGE Bus
    FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModClientSetup);
  }

  public static SimpleAudioPlayer clientPlayer = null;

  public static void onModClientSetup(FMLClientSetupEvent event) {
    // Runs after onCommonSetup if on Client
    LOGGER.info("HELLO from client setup");
  }

  @SubscribeEvent
  public static void onClientLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
    if(clientPlayer == null) {
      // Start Audio Player
      LOGGER.info("Starting new AudioPlayer");
      clientPlayer = new SimpleAudioPlayer();
    }
  }

  @SubscribeEvent
  public static void onClientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
    if(clientPlayer != null) {
      // Stop Audio Player
      LOGGER.info("Stopping AudioPlayer");
      clientPlayer.close();
      clientPlayer = null;
    }
  }
}
