package com.kynake.minecraft.directionablediscord.setup;

// Internal
import com.kynake.minecraft.directionablediscord.DirectionableDiscord;

// Forge
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent; // MOD Bus Event
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = DirectionableDiscord.ModID, value = { Dist.DEDICATED_SERVER }, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DedicatedServerSetup {
  static { // Add Events that trigger on the  MOD Bus, rather than the FORGE Bus
    FMLJavaModLoadingContext.get().getModEventBus().addListener(DedicatedServerSetup::onModDedicatedServerSetup);
  }

  public static void onModDedicatedServerSetup(FMLDedicatedServerSetupEvent event) {
    // Runs after onCommonSetup if on Dedicated Server
    DirectionableDiscord.LOGGER.info("HELLO from dedicated server setup");
  }
}
