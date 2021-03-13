package kynake.audio;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

public class Sound {
  public final byte[] pcmSample;
  public final Vector3d sourceLocation;

  public Sound(byte[] pcmSample, Vector3d sourceLocation) {
    this.pcmSample = pcmSample;
    this.sourceLocation = sourceLocation;
  }
}
