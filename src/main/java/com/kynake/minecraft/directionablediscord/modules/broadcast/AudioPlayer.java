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
  // private static final int bufferSize = 4096;
  private static final Logger LOGGER = LogManager.getLogger();

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
        // if(pcmSample.length % AudioReceiveHandler.OUTPUT_FORMAT.getFrameSize() != 0) {
        //   // pad frame
        // }

        audioLine.write(nextSample, 0, nextSample.length);
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
    // DataLine.Info info = new DataLine.Info(SourceDataLine.class, AudioReceiveHandler.OUTPUT_FORMAT, bufferSize); // Size specifier needed?
    DataLine.Info info = new DataLine.Info(SourceDataLine.class, AudioReceiveHandler.OUTPUT_FORMAT);

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
