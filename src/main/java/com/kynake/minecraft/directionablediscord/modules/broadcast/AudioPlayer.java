package com.kynake.minecraft.directionablediscord.modules.broadcast;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;

// Java
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AudioPlayer {
  private static final int bufferSize = 4096;
  private static final Logger LOGGER = LogManager.getLogger();

  private SourceDataLine audioLine;

  public AudioPlayer() {
    audioLine = createDataLine();
    if(audioLine == null) {
      return;
    }

    try {
      audioLine.open();
      audioLine.start();
    } catch(LineUnavailableException e) {
      LOGGER.debug("Unable to create SourceDataLine");
      LOGGER.catching(e);
    }

  }

  public void playPCMSample(byte[] pcmSample) {
    if(audioLine == null) {
      LOGGER.debug("Cannot play audio, audioLine is null");
      return;
    }

    if(!audioLine.isOpen()) {
      LOGGER.debug("Cannot play audio, audioLine is closed");
      return;
    }

    // if(pcmSample.length % AudioReceiveHandler.OUTPUT_FORMAT.getFrameSize() != 0) {
    //   // pad frame
    // }

    audioLine.write(pcmSample, 0, pcmSample.length);
  }

  public void close() {
    audioLine.stop();
    audioLine.flush();
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
