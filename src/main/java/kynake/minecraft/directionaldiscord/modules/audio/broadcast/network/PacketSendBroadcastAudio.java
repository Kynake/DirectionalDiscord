package kynake.minecraft.directionaldiscord.modules.audio.broadcast.network;

// Internal
import kynake.minecraft.directionaldiscord.setup.ClientSetup;

// Forge
import net.minecraftforge.fml.network.NetworkEvent;

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
        ClientSetup.clientPlayer.playPCMSample(audioSample, null, null, null);
      }
    });

    return true;
  }
}
