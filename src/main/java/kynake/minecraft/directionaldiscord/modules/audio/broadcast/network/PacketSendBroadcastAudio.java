package kynake.minecraft.directionaldiscord.modules.audio.broadcast.network;

import kynake.audio.Utils;
// Internal
import kynake.minecraft.directionaldiscord.setup.ClientSetup;

// Forge
import net.minecraftforge.fml.network.NetworkEvent;
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
// Minecraft
import net.minecraft.network.PacketBuffer;

// Java
import java.util.function.Supplier;

public class PacketSendBroadcastAudio {
  private final byte[] audioSample;

  public PacketSendBroadcastAudio(PacketBuffer buffer) {
    audioSample = buffer.readByteArray();
  }

  public PacketSendBroadcastAudio(byte[] audioSample) {
    this.audioSample = audioSample;
  }

  public void toBytes(PacketBuffer buffer) {
    buffer.writeByteArray(audioSample);
  }

  public boolean handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      // Make sure we are on the client before playing Audio
      if(context.get().getDirection().getReceptionSide().isClient()) {
        ClientSetup.clientPlayer.playPCMSample(Utils.byteToShortArray(audioSample, AudioReceiveHandler.OUTPUT_FORMAT.isBigEndian()), null, null);
      }
    });

    return true;
  }
}
