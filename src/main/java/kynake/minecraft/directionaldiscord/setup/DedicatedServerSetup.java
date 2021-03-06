package kynake.minecraft.directionaldiscord.setup;

// Internal
import kynake.minecraft.directionaldiscord.DirectionalDiscord;

// Forge
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent; // MOD Bus Event
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid = DirectionalDiscord.ModID, value = { Dist.DEDICATED_SERVER }, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DedicatedServerSetup {
  private static final Logger LOGGER = LogManager.getLogger();

  static { // Add Events that trigger on the  MOD Bus, rather than the FORGE Bus
    FMLJavaModLoadingContext.get().getModEventBus().addListener(DedicatedServerSetup::onModDedicatedServerSetup);
  }

  public static void onModDedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
    // Runs after onCommonSetup if on Dedicated Server
    LOGGER.info("HELLO from dedicated server setup");
  }
}
