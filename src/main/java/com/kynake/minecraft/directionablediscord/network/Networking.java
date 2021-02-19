package com.kynake.minecraft.directionablediscord.network;

// Internal
import com.kynake.minecraft.directionablediscord.DirectionableDiscord;
import com.kynake.minecraft.directionablediscord.modules.broadcast.network.PacketSendAudio;

// Forge
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;


public class Networking {
  private static final String PROTOCOL_VERSION = "1";
  private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
    new ResourceLocation(DirectionableDiscord.ModID, "main"),
    () -> PROTOCOL_VERSION,
    PROTOCOL_VERSION::equals,
    PROTOCOL_VERSION::equals
  );

  private static int ID = 0;
  private static int nextID() {
    return ID++;
  }

  public static void registerNetworkMessages() {

    // TODO: Create generic packet class and generic builder method
    // maybe 1 for data packets and one for empty packets?
    INSTANCE.messageBuilder(PacketSendAudio.class, nextID())
            .encoder(PacketSendAudio::toBytes)
            .decoder(PacketSendAudio::new)
            .consumer(PacketSendAudio::handle)
            .add();
  }

  public static void sendToClient(Object packet, ServerPlayerEntity player) {
    INSTANCE.sendTo(packet, player.connection.netManager, NetworkDirection.PLAY_TO_CLIENT);
  }

  public static void sendToServer(Object packet) {
    INSTANCE.sendToServer(packet);
  }
}
