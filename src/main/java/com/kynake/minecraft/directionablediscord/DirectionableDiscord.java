package com.kynake.minecraft.directionablediscord;

// Internal
import com.kynake.minecraft.directionablediscord.setup.ModSetup;
import com.kynake.minecraft.directionablediscord.setup.proxy.ClientProxy;
import com.kynake.minecraft.directionablediscord.setup.proxy.IProxy;
import com.kynake.minecraft.directionablediscord.setup.proxy.ServerProxy;

// Forge
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(DirectionableDiscord.ModID)
public class DirectionableDiscord {
  public final static String ModID = "directionablediscord";
  public static final Logger LOGGER = LogManager.getLogger();

  public static IProxy proxy  = DistExecutor.runForDist(
      () -> () -> new ClientProxy(), // Clientside instance of proxy
      () -> () -> new ServerProxy()  // Serverside instance of proxy
  );

  public static ModSetup setup = new ModSetup();

  public DirectionableDiscord() {
    /**
     * Setup listeners and cofigurations here
     */

    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    // Register the doClientStuff method for modloading
    // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

    // Register ourselves for server and other game events we are interested in
    // MinecraftForge.EVENT_BUS.register(this);
  }

  // Methods
  private void setup(final FMLCommonSetupEvent event) {
    /**
     * Runs after all blocks, TileEntities, Biomes, etc
     * from all mods have been registered
     */

    // Do setups
    setup.init();
  }

  // private void doClientStuff(final FMLClientSetupEvent event) {
  //   // do something that can only be done on the client
  //   LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
  // }


  // // You can use SubscribeEvent and let the Event Bus discover methods to call
  // @SubscribeEvent
  // public void onServerStarting(FMLServerStartingEvent event) {
  //   // do something when the server starts
  //   LOGGER.info("HELLO from server starting");
  // }


}
