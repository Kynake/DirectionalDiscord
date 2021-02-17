package com.kynake.minecraft.directionablediscord.modules.broadcast.network;

// Forge
import net.minecraftforge.fml.network.NetworkEvent;

// Minecraft
import net.minecraft.network.PacketBuffer;

// Java
import java.util.function.Supplier;

import com.kynake.minecraft.directionablediscord.DirectionableDiscord;

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
      DirectionableDiscord.LOGGER.debug("PLAYING AUDIO SAMPLE", audioSample);
    });

    return true;
  }
}
