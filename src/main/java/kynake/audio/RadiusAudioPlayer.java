package kynake.audio;

// Internal
import kynake.audio.Utils.Audio;
import kynake.audio.Utils.Other;

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
public class RadiusAudioPlayer implements AudioPlayer, Runnable {
  // TODO define these in server config
  public static double maxDistance = 100;
  public static double minDistance = 10;

  // This is the constant size of the byte[]'s sent over by the Discord Bot
  private static final byte[] emptySample = new byte[Audio.BUFFER_SIZE];

  private SourceDataLine audioLine;
  private Map<UUID, ConcurrentLinkedQueue<Sound>> sourceBuffers = new HashMap<>();

  public RadiusAudioPlayer() {
    audioLine = Audio.createDataLine();
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
    sourceBuffers.computeIfAbsent(sourceID, k -> new ConcurrentLinkedQueue<Sound>()).add(new Sound(pcmSample, sourceLocation));
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

  @Nullable
  private byte[] generateCombinedSample() {
    List<short[]> scaledByDistance = scaleSamplesVolume();
    byte[] mixedSourcesSample = combineSourceSamples(scaledByDistance);
    return mixedSourcesSample;
  }

  // Audio volume scaling
  @Nullable
  private List<short[]> scaleSamplesVolume() {
    Vector3d listenerLocation = Other.getListenerLocation();
    List<short[]> res = new ArrayList<>(sourceBuffers.size());
    sourceBuffers.forEach((uuid, buffer) -> {
      // Check all sources for buffered audio, unbuffer if existing
      Sound sound = buffer.poll();
      if(sound != null) {
        // If a source has audio, scale it and add to the list of audio to play
        double volumeScale = calculateVolumeScalingByDistance(sound.sourceLocation, listenerLocation);
        res.add(scalePCMSample(sound.pcmSample, volumeScale));
      }
    });

    return res;
  }

  private double calculateVolumeScalingByDistance(Vector3d source, Vector3d listener) {
    double dist = listener.distanceTo(source);
    if(dist <= minDistance) {
      return 1.0d;
    }

    if(dist >= maxDistance + minDistance) {
      return 0.0d;
    }

    return 1.0d / (dist - minDistance + 1.0d);
  }

  private short[] scalePCMSample(byte[] sample, double scaleFactor) {

    short[] shortSample = Audio.byteToShortArray(sample, Audio.FORMAT.isBigEndian());
    for(int i = 0; i < shortSample.length; i++) {
      shortSample[i] = (short) Math.round(shortSample[i] * scaleFactor);
    }

    return shortSample;
  }

  // Audio Combining
  @Nullable
  private byte[] combineSourceSamples(@Nonnull List<short[]> sourceSamples) {
    short[] shortCombined;
    try {
      shortCombined = sourceSamples.stream().reduce((combined, sample) -> combineSamples(combined, sample)).get();
    } catch(NoSuchElementException e) {
      return null;
    }

    return Audio.shortToByteArray(shortCombined, Audio.FORMAT.isBigEndian());
  }

  private short[] combineSamples(@Nonnull short[] base, @Nonnull short[] other) {
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

  // private Vector3d debugSourceLocation(Vector3d source) {
  //   return source.add(25, 0, 0);
  // }

  // private double debugScaleFactor() {
  //   return 0.5;
  // }
}
