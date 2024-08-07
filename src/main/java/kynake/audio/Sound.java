package kynake.audio;

import java.util.UUID;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

public class Sound {
  public UUID source;
  public short[] pcmSample;
  public Vector3d sourceLocation;

  public Sound(UUID source, short[] pcmSample, Vector3d sourceLocation) {
    this.source = source;
    this.pcmSample = pcmSample;
    this.sourceLocation = sourceLocation;
  }
}
