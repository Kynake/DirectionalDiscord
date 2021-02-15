package com.kynake.minecraft.directionablediscord;

// Internal
import com.kynake.minecraft.directionablediscord.setup.IProxy;
import com.kynake.minecraft.directionablediscord.setup.ClientProxy;
import com.kynake.minecraft.directionablediscord.setup.ServerProxy;

// Minecraft
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

// Forge
import net.minecraftforge.common.MinecraftForge;

import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
@Mod(DirectionableDiscord.ModID)
public class DirectionableDiscord {
  public final static String ModID = "directionablediscord";
  private static final Logger LOGGER = LogManager.getLogger();

  public static IProxy proxy  = DistExecutor.runForDist(
      () -> () -> new ClientProxy(), // Clientside instance of proxy
      () -> () -> new ServerProxy()  // Serverside instance of proxy
  );

  public DirectionableDiscord() {
    /**
     * Setup listeners and cofigurations here
     */

    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

    // Register the enqueueIMC method for modloading
    // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

    // Register the processIMC method for modloading
    // FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);

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
    LOGGER.info("HELLO FROM PREINIT");
    LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
  }

  // private void doClientStuff(final FMLClientSetupEvent event) {
  //   // do something that can only be done on the client
  //   LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
  // }

  // private void enqueueIMC(final InterModEnqueueEvent event) {
  //   // some example code to dispatch IMC to another mod
  //   InterModComms.sendTo("examplemod", "helloworld", () -> {
  //     LOGGER.info("Hello world from the MDK");
  //     return "Hello world";
  //   });
  // }

  // private void processIMC(final InterModProcessEvent event) {
  //   // some example code to receive and process InterModComms from other mods
  //   LOGGER.info("Got IMC {}", event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
  // }

  // // You can use SubscribeEvent and let the Event Bus discover methods to call
  // @SubscribeEvent
  // public void onServerStarting(FMLServerStartingEvent event) {
  //   // do something when the server starts
  //   LOGGER.info("HELLO from server starting");
  // }


}
