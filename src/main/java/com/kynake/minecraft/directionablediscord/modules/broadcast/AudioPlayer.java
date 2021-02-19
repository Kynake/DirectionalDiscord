package com.kynake.minecraft.directionablediscord.modules.broadcast;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;


// Java
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioPlayer implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger();

  // TODO: find a better way to get this value without hardcoding it
  // This is the constant size of the byte[]'s sent over by the Discord Bot
  private static final int bufferSize = 3840;
  private SourceDataLine audioLine;

  // Written by the Main Thread, Read from by the Audio Thread
  private Queue<byte[]> pcmBuffer = new ConcurrentLinkedQueue<>();

  public AudioPlayer() {
    audioLine = createDataLine();
    if(audioLine == null) {
      return;
    }

    Thread t = new Thread(this, "DD Voice Chat");
    t.start();
  }

  public void playPCMSample(byte[] pcmSample) {
    if(audioLine == null || !audioLine.isOpen()) {
      LOGGER.debug("Cannot play audio, audioLine is " + (audioLine ==  null? "null" : "closed"));
      return;
    }

    // Add current pcm sample to the queue, which get removed and played in the Audio Thread
    pcmBuffer.add(pcmSample);
  }

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


  public void close() {
    audioLine.stop();
    audioLine.flush();

    // Closes the AudioDataLine, which in turn causes the Audio Thread to stop running
    audioLine.close();
  }

  private SourceDataLine createDataLine() {
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, AudioReceiveHandler.OUTPUT_FORMAT, bufferSize);

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
}