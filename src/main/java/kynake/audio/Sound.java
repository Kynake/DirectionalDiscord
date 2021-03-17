package kynake.audio;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

public class Sound {
  public final short[] pcmSample;
  public final Vector3d sourceLocation;

  public Sound(short[] pcmSample, Vector3d sourceLocation) {
    this.pcmSample = pcmSample;
    this.sourceLocation = sourceLocation;
  }
}
