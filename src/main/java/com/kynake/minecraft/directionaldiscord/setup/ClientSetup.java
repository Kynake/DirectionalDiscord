package com.kynake.minecraft.directionaldiscord.setup;

import com.kynake.minecraft.directionaldiscord.DirectionalDiscord;
import com.kynake.minecraft.directionaldiscord.modules.broadcast.AudioPlayer;

// Forge
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent; // MOD Bus Event
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod.EventBusSubscriber(modid = DirectionalDiscord.ModID, value = { Dist.CLIENT }, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientSetup {
  static { // Add Events that trigger on the  MOD Bus, rather than the FORGE Bus
    FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::onModClientSetup);
  }

  public static AudioPlayer clientPlayer = null;

  public static void onModClientSetup(FMLClientSetupEvent event) {
    // Runs after onCommonSetup if on Client
    DirectionalDiscord.LOGGER.info("HELLO from client setup");
  }

  @SubscribeEvent
  public static void onClientLogin(ClientPlayerNetworkEvent.LoggedInEvent event) {
    if(clientPlayer == null) {
      // Start Audio Player
      DirectionalDiscord.LOGGER.info("Starting new AudioPlayer");
      clientPlayer = new AudioPlayer();
    }
  }

  @SubscribeEvent
  public static void onClientLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
    if(clientPlayer != null) {
      // Stop Audio Player
      DirectionalDiscord.LOGGER.info("Stopping AudioPlayer");
      clientPlayer.close();
      clientPlayer = null;
    }
  }
}
