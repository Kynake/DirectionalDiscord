package kynake.audio;

// Internal
import kynake.minecraft.directionaldiscord.config.Constants;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Utils {
  private static final Logger LOGGER = LogManager.getLogger();

  // Audio and JDA
  public static final double minDistance = Constants.minDistance;
  public static final double maxDistance = Constants.maxDistance;

  public static final AudioFormat FORMAT = AudioReceiveHandler.OUTPUT_FORMAT;
  public static final int BUFFER_SIZE = calculateBufferSize();

  @Nonnull public static byte[] shortToByteArray(@Nonnull short[] array) {
    return shortToByteArray(array, FORMAT.isBigEndian());
  }

  @Nonnull public static byte[] shortToByteArray(@Nonnull short[] array, boolean isBigEndian) {
    byte[] res = new byte[array.length * Short.BYTES];
    if(isBigEndian) {
      for(int i = 0; i < array.length; i++) {
        res[i*2] = (byte) (array[i] >> Byte.SIZE);
        res[i*2 + 1] = (byte) array[i];
      }
    } else {
      for(int i = 0; i < array.length; i++) {
        res[i*2] = (byte) array[i];
        res[i*2 + 1] = (byte) (array[i] >> Byte.SIZE);
      }
    }

    return res;
  }

  @Nonnull public static short[] byteToShortArray(@Nonnull byte[] array) {
    return byteToShortArray(array, FORMAT.isBigEndian());
  }

  @Nonnull public static short[] byteToShortArray(@Nonnull byte[] array, boolean isBigEndian) {
    short[] res = new short[array.length / Short.BYTES];
    if(isBigEndian) {
      for(int i = 0; i < res.length; i++) {
        res[i] = (short) (array[i * 2] << 8 | array[i * 2 + 1] & 0xff);
      }
    } else {
      for(int i = 0; i < res.length; i++) {
        res[i] = (short) (array[i * 2 + 1] << 8 | array[i * 2] & 0xff);
      }
    }

    return res;
  }

  public static SourceDataLine createDataLine() {
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, FORMAT, BUFFER_SIZE);

    if(!AudioSystem.isLineSupported(info)) {
      LOGGER.debug("Dataline not supported!", info);
      return null;
    }

    try {
      return (SourceDataLine) AudioSystem.getLine(info);
    } catch(LineUnavailableException e) {
      LOGGER.debug("Unable to create SourceDataLine");
      LOGGER.catching(e);
      return null;
    }
  }

  private static int calculateBufferSize() {
    int ratio = 50; // 20ms (20ms * 50 = 1000ms)
    return ((int) FORMAT.getSampleRate() / ratio) * FORMAT.getChannels() * (FORMAT.getSampleSizeInBits() / Byte.SIZE);
  }

  // Minecraft
  @SuppressWarnings({"resource"})
  public static Vector3d getListenerLocation() {
    return  Minecraft.getInstance().player.getPositionVec();
  }

  @SuppressWarnings({"resource"})
  public static Vector3d getListenerLook() {
    return Minecraft.getInstance().player.getLookVec();
  }

  @SuppressWarnings({"resource"})
  public static Vector3d getListenerUp() {
    return Minecraft.getInstance().player.getUpVector(1.0f).normalize();
  }

  @SuppressWarnings({"resource"})
  public static Vector3d getListenerForward() {
    return Vector3d.fromPitchYaw(0, Minecraft.getInstance().player.getYaw(1));
  }

  // Math

  /**
   * Make sure that NEITHER vector is the ZERO vector, else you'll get a division by zero
   */
  public static double vectorAngle(Vector3d x, Vector3d y) {
    return Math.acos(x.dotProduct(y)/(x.length() * y.length()));
  }
}
