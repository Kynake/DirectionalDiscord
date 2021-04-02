package kynake.audio;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

// Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * An audio player that scales sound sample volume based on the radius distance between sources and listener
 */
public class PositionalAudioPlayer implements AudioPlayer, Runnable {
  // This is the constant size of the byte[]'s sent over by the Discord Bot
  private static final byte[] emptySample = new byte[Utils.BUFFER_SIZE];

  private SourceDataLine audioLine;
  private Map<UUID, ConcurrentLinkedQueue<Sound>> sourceBuffers = new HashMap<>();

  public PositionalAudioPlayer() {
    audioLine = Utils.createDataLine();
    if(audioLine == null) {
      return;
    }

    Thread t = new Thread(this, "DD Voice Chat");
    t.start();
  }

  @Override
  public void playPCMSample(byte[] pcmSample, UUID sourceID, Vector3d sourceLocation) {
    if(audioLine == null || !audioLine.isOpen()) {
      LOGGER.debug("Cannot play audio, audioLine is " + (audioLine ==  null? "null" : "closed"));
      return;
    }

    // Add current pcm sample to the queue, which gets removed and played in the Audio Thread
    sourceBuffers.computeIfAbsent(sourceID, k -> new ConcurrentLinkedQueue<Sound>()).add(new Sound(Utils.byteToShortArray(pcmSample), sourceLocation));

    // Debug
    // sourceBuffers.computeIfAbsent(sourceID, k -> new ConcurrentLinkedQueue<Sound>()).add(new Sound(Utils.byteToShortArray(pcmSample), debugSourceLocation(sourceLocation)));

  }

  @Override
  public void run() {
    try {
      audioLine.open();
    } catch(LineUnavailableException e) {
      LOGGER.fatal("Unable to create SourceDataLine");
      LOGGER.catching(e);
      throw new IllegalStateException("Cannot continue with without available dataline");
    }

    int discardSamples = 0;

    audioLine.start();
    while(audioLine.isOpen()) { // Keep the Thread open while we process audio
      byte[] nextSample = generateCombinedSample();
      if(nextSample != null) {
        if(discardSamples != 0) {
          discardSamples--;

          // Player audio samples were removed from buffer during generateCombinedSample(),
          // so we discard them here, by playing silence instead of the combined sample
          audioLine.write(emptySample, 0, emptySample.length);
        } else {
          // Add next buffered sample to audioLine
          audioLine.write(nextSample, 0, nextSample.length);
        }
      } else if(audioLine.available() < audioLine.getBufferSize()) {
        // If no new buffered sample has come in since last iteration, playout the remaining buffer on the audioLine
        audioLine.drain();
      } else {
        // If both buffers are empty, it's safe to discard whatever leftover data there might still be there
        audioLine.flush();

        // When no audio is playing, at the start of the next sample,
        // audio that was cut off from the previous sample might still play,
        // so we ignore the samples that contain audio from before
        discardSamples = 2;
      }
    }

    // Cleanup after audioLine has been closed
    sourceBuffers.forEach((uuid, buffer) -> buffer.clear());
    sourceBuffers.clear();
  }

  @Override
  public void close() {
    audioLine.stop();
    audioLine.flush();

    // Closes the AudioDataLine, which in turn causes the Audio Thread to stop running
    audioLine.close();
  }

  // Samples
  @Nullable private byte[] generateCombinedSample() {
    List<Sound> samplesList = getNextSamples();

    List<Sound> scaledByDistance = scaleSamplesVolume(samplesList);
    List<Sound> pannedSounds = panSamples(scaledByDistance);

    byte[] mixedSourcesSample = combineSourceSamples(pannedSounds);
    return mixedSourcesSample;
  }

  @Nonnull private List<Sound> getNextSamples() {
    List<Sound> res = new ArrayList<>(sourceBuffers.size());
    sourceBuffers.forEach((uuid, buffer) -> {
      // Check all sources for buffered audio, unbuffer if existing
      Sound sound = buffer.poll();
      if(sound != null) {
        res.add(sound);
      }
    });

    return res;
  }

  // Gain (Unpanned)
  @Nonnull private List<Sound> scaleSamplesVolume(@Nonnull List<Sound> samples) {
    Vector3d listenerLocation = Utils.getListenerLocation();
    samples.forEach(sound -> {
      // If a source has audio, scale it and add to the list of audio to play
      double volumeScale = calculateVolumeScalingByDistance(sound.sourceLocation, listenerLocation);
      sound.pcmSample = scalePCMSample(sound.pcmSample, volumeScale);
    });

    return samples;
  }

  private double calculateVolumeScalingByDistance(@Nonnull Vector3d source, @Nonnull Vector3d listener) {
    double dist = listener.distanceTo(source);

    // Full Volume when distance is less than Minimum
    if(dist <= Utils.minDistance) {
      return 1.0d;
    }

    // No Sound when distance is greter that Maximum
    if(dist >= Utils.maxDistance) {
      return 0.0d;
    }

    // Linearly interpolate to zero when distance is very close to the Maximum
    if(dist >= Utils.maxDistance - Utils.fadeoutDistance) {
      double baseFactor = Utils.falloffFactor / (Utils.maxDistance - Utils.fadeoutDistance - Utils.minDistance + Utils.falloffFactor);
      return baseFactor * (1.0d - ((dist - Utils.maxDistance + Utils.fadeoutDistance) / Utils.fadeoutDistance));
    }

    // Default logarithmic interpolation
    return Utils.falloffFactor / (dist - Utils.minDistance + Utils.falloffFactor);
  }

  private short[] scalePCMSample(short[] sample, double scaleFactor) {

    for(int i = 0; i < sample.length; i++) {
      sample[i] = (short) Math.round(sample[i] * scaleFactor);
    }

    return sample;
  }

  // Pan
  @Nonnull private List<Sound> panSamples(@Nonnull List<Sound> samples) {
    Vector3d listenerLocation = Utils.getListenerLocation();

    Vector3d listenerLook = Utils.getListenerLook(); // Z Vector
    Vector3d listenerUp = Utils.getListenerUp(); // Y Vector
    Vector3d listenerSide = listenerUp.crossProduct(listenerLook).normalize(); // X Vector

    samples.forEach(sound -> {
      if(listenerLocation.squareDistanceTo(sound.sourceLocation) == 0) {
        return;
      }

      // Calculate the X and Z magnitudes of the Sound Source relative to the Listener position
      Vector3d sourceDirection = sound.sourceLocation.subtract(listenerLocation);
      double sourceX = sourceDirection.dotProduct(listenerSide);
      double sourceZ = sourceDirection.dotProduct(listenerLook);

      double angle = Math.atan2(sourceX, sourceZ);
      double unfactoredPan = Math.sin(angle); // from -1 to 1
      double panFactor = (unfactoredPan + 1.0D) / 2.0D; // from 0 to 1

      sound.pcmSample = applyPanning(sound.pcmSample, panFactor);
    });

    return samples;
  }

  @Nonnull private short[] applyPanning(@Nonnull short[] pcmSample, double angleFactor) {
    double strengthFactor = angleFactor * (Math.PI / 2.0D);

    // We assume the unpanned GAIN is Always 1, or has already been adjusted
    // TODO Set Panned Gain here directly without pre adjustments
    double leftPan = Math.sin(strengthFactor);
    double rightPan = Math.cos(strengthFactor);

    // LOGGER.debug("Pan Factor: {}, Left Pan: {}, Right Pan: {}", angleFactor, leftPan, rightPan);

    for(int i = 0; i < pcmSample.length; i++) {
      if(i % 2 == 0) { // Left Channel
        pcmSample[i] *= leftPan;
      } else { // Right Channel
        pcmSample[i] *= rightPan;
      }
    }

    return pcmSample;
  }

  // Audio Combining
  @Nullable private byte[] combineSourceSamples(@Nonnull List<Sound> sourceSamples) {
    short[] shortCombined;
    try {
      shortCombined = sourceSamples.stream().map(sound -> sound.pcmSample).reduce((combined, sample) -> combineSamples(combined, sample)).get();
    } catch(NoSuchElementException e) {
      return null;
    }

    return Utils.shortToByteArray(shortCombined);
  }

  @Nonnull private short[] combineSamples(@Nonnull short[] base, @Nonnull short[] other) {
    short[] res = new short[Math.min(base.length, other.length)];
    for(int i = 0; i < res.length; i++) {
      // Cast shorts to ints to catch possible over/underflows
      int unboundedSum = (int) base[i] + (int) other[i];

      // Clamp values of necessary
      if(unboundedSum > Short.MAX_VALUE) { // Overflow
        res[i] = Short.MAX_VALUE;
      } else if(unboundedSum < Short.MIN_VALUE) { // Underflow
        res[i] = Short.MIN_VALUE;
      } else {
        res[i] = (short) unboundedSum;
      }
    }

    return res;
  }

  // Debug
  private Vector3d debugSourceLocation(Vector3d source) {
    return new Vector3d(0, 64, 0);
  }
}
