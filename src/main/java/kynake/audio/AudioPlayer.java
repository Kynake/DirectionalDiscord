package kynake.audio;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.UUID;

public interface AudioPlayer {
  static final Logger LOGGER = LogManager.getLogger();

  /**
   * Play a PCM sample from a given position, with relation to the listener location
   * @param pcmSample
   * @param sourceID
   * @param sourceLocation
   * @param listenerLocation
   */
  public void playPCMSample(byte[] pcmSample, UUID sourceID, Vector3d sourceLocation, Vector3d listenerLocation);

  /**
   * Closes and cleans up this AudioPlayer
   */
  public void close();

}
