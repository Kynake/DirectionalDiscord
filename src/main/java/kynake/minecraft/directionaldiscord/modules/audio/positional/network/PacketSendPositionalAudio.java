package kynake.minecraft.directionaldiscord.modules.audio.positional.network;

// Internal
import kynake.minecraft.directionaldiscord.setup.ClientSetup;

// Forge
import net.minecraftforge.fml.network.NetworkEvent;

// Minecraft
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;

// Java
import java.util.function.Supplier;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketSendPositionalAudio {
  private static final Logger LOGGER = LogManager.getLogger();

  private final Vector3d speakerPosition;
  private final byte[] audioSample;

  public PacketSendPositionalAudio(PacketBuffer buffer) {
    double x = buffer.readDouble();
    double y = buffer.readDouble();
    double z = buffer.readDouble();
    audioSample = buffer.readByteArray();
    speakerPosition = new Vector3d(x, y, z);
  }

  public PacketSendPositionalAudio(byte[] audioSample, Vector3d speakerPosition) {
    this.speakerPosition = speakerPosition;
    this.audioSample = audioSample;
  }

  public void toBytes(PacketBuffer buffer) {
    buffer.writeDouble(speakerPosition.x);
    buffer.writeDouble(speakerPosition.y);
    buffer.writeDouble(speakerPosition.z);
    buffer.writeByteArray(audioSample);
  }

  public boolean handle(Supplier<NetworkEvent.Context> context) {
    context.get().enqueueWork(() -> {
      // Make sure we are on the client before playing Audio
      if(context.get().getDirection().getReceptionSide().isClient()) {
        LOGGER.debug("Received audio at: [" + speakerPosition.x + ", " + speakerPosition.y + ", " + speakerPosition.z + "]");
        // ClientSetup.clientPlayer.playPCMSample(audioSample, speakerPosition);
        ClientSetup.clientPlayer.playPCMSample(audioSample);
      }
    });

    return true;
  }
}
