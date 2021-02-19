package com.kynake.minecraft.directionablediscord.modules.broadcast.network;

import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkDirection;
// Forge
import net.minecraftforge.fml.network.NetworkEvent;

// Minecraft
import net.minecraft.network.PacketBuffer;

// Java
import java.util.function.Supplier;

import com.kynake.minecraft.directionablediscord.DirectionableDiscord;
import com.kynake.minecraft.directionablediscord.setup.ClientSetup;

public class PacketSendAudio {
  private final byte[] audioSample;

  public PacketSendAudio(PacketBuffer buffer) {
    audioSample = buffer.readByteArray();
  }

  public PacketSendAudio(byte[] audioSample) {
    this.audioSample = audioSample;
  }

  public void toBytes(PacketBuffer buffer) {
    buffer.writeByteArray(audioSample);
  }

  public boolean handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      // Make sure we are on the client before playing Audio
      if(context.get().getDirection().getReceptionSide().isClient()) {
        ClientSetup.clientPlayer.playPCMSample(audioSample);
      }
    });

    return true;
  }
}
