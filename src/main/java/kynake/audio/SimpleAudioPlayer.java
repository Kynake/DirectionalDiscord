package kynake.audio;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

// Java
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


/**
 * Audio player that simply plays the audio without modifications
 */
public class SimpleAudioPlayer implements AudioPlayer, Runnable {
  private SourceDataLine audioLine;

  // Written by the Main Thread, Read from by the Audio Thread
  private Queue<byte[]> pcmBuffer = new ConcurrentLinkedQueue<>();

  public SimpleAudioPlayer() {
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
    pcmBuffer.add(pcmSample);
  }

  @Override
  public void run() throws IllegalStateException {
    try {
      audioLine.open();
    } catch(LineUnavailableException e) {
      LOGGER.fatal("Unable to create SourceDataLine");
      LOGGER.catching(e);
      throw new IllegalStateException("Cannot continue with without available dataline");
    }

    audioLine.start();
    while(audioLine.isOpen()) { // Keep the Thread open while we process audio
      byte[] nextSample =  pcmBuffer.poll();
      if(nextSample != null) {
        // Add next buffered sample to audioLine
        audioLine.write(nextSample, 0, nextSample.length);
      } else if(audioLine.available() < audioLine.getBufferSize()) {
        // If no new buffered sample has come in since last iteration, playout the remaining buffer on the audioLine
        audioLine.drain();
      } else {
        // If both buffers are empty, it's safe to discard whatever leftover data there might still be there
        audioLine.flush();
      }
    }

    // Cleanup after audioLine has been closed
    pcmBuffer.clear();
  }

  @Override
  public void close() {
    audioLine.stop();
    audioLine.flush();

    // Closes the AudioDataLine, which in turn causes the Audio Thread to stop running
    audioLine.close();
  }
}
