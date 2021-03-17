package kynake.minecraft.directionaldiscord.modules.audio.positional.network;

// Internal
import kynake.minecraft.directionaldiscord.setup.ClientSetup;

// Forge
import net.minecraftforge.fml.network.NetworkEvent;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

// Java
import java.util.UUID;
import java.util.function.Supplier;
/**
 * Network packet containg audio and positional information about an audio source
 */
public class PacketSendPositionalAudio {
  private final UUID speaker;
  private final Vector3d speakerPosition;
  private final byte[] rawOpusSample;

  public PacketSendPositionalAudio(PacketBuffer buffer) {
    speaker = buffer.readUniqueId();
    double x = buffer.readDouble();
    double y = buffer.readDouble();
    double z = buffer.readDouble();
    rawOpusSample = buffer.readByteArray();
    speakerPosition = new Vector3d(x, y, z);
  }

  public PacketSendPositionalAudio(byte[] rawOpusSample, UUID speaker, Vector3d speakerPosition) {
    this.speaker = speaker;
    this.speakerPosition = speakerPosition;
    this.rawOpusSample = rawOpusSample;
  }

  public void toBytes(PacketBuffer buffer) {
    buffer.writeUniqueId(speaker);
    buffer.writeDouble(speakerPosition.x);
    buffer.writeDouble(speakerPosition.y);
    buffer.writeDouble(speakerPosition.z);
    buffer.writeByteArray(rawOpusSample);
  }

  public boolean handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      // Make sure we are on the client before playing Audio
      if(context.get().getDirection().getReceptionSide().isClient()) {
        short[] pcmSample = ClientSetup.clientOpus.toPCMAudio(rawOpusSample);
        ClientSetup.clientPlayer.playPCMSample(pcmSample, speaker, speakerPosition);
      }
    });

    return true;
  }
}
