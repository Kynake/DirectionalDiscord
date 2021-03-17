package kynake.audio;

// Opus
import tomp2p.opuswrapper.Opus;
import club.minnced.opus.util.OpusLibrary;

// JDA
import net.dv8tion.jda.api.audio.OpusPacket;

// Forge
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// JNA
import com.sun.jna.ptr.PointerByReference;

import java.io.IOException;
// Java
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class OpusHandler {
  private static final Logger LOGGER = LogManager.getLogger();
  private static OpusHandler opusInstance = null;
  private static final int pcmBufferSize = 4096;

  private PointerByReference opusLib = null;

  public static OpusHandler instance() {
    if(opusInstance == null) {
      opusInstance = new OpusHandler();
    }

    return opusInstance;
  }

  private OpusHandler() {
    if(!ensureOpusLib()) {
      return;
    }

    if(opusLib == null) {
      // OpusWrapper represents C's int* with Java's IntBuffer
      IntBuffer error = IntBuffer.allocate(1);
      opusLib = Opus.INSTANCE.opus_decoder_create(OpusPacket.OPUS_SAMPLE_RATE, OpusPacket.OPUS_CHANNEL_COUNT, error);

      if(error.get() != Opus.OPUS_OK && opusLib == null) {
        LOGGER.fatal("Could not create OpusLib");
      }
    }
  }

  @Nullable public short[] toPCMAudio(@Nonnull byte[] rawOpusData) {
    if(opusLib == null) {
      return null;
    }

    ShortBuffer pcmDecodeBuffer = ShortBuffer.allocate(pcmBufferSize);

    // Returns the number of bytes read or an error code
    int result = Opus.INSTANCE.opus_decode(
      opusLib,
      rawOpusData,
      rawOpusData.length,
      pcmDecodeBuffer,
      OpusPacket.OPUS_FRAME_SIZE,
      0
    );

    if(result < 0) {
      LOGGER.error("Error decoding Opus audio: {}", getOpusErrorName(result));
      return null;
    }

    short[] res = new short[result * Short.BYTES];
    pcmDecodeBuffer.get(res);
    return res;
  }

  public synchronized void close() {
    if(opusLib != null) {
      Opus.INSTANCE.opus_decoder_destroy(opusLib);
      opusLib = null;
    }
  }

  private static String getOpusErrorName(int errorCode) {
    switch(errorCode) {
      case Opus.OPUS_BAD_ARG:
        return "OPUS_BAD_ARG";

      case Opus.OPUS_BUFFER_TOO_SMALL:
        return "OPUS_BUFFER_TOO_SMALL";

      case Opus.OPUS_INTERNAL_ERROR:
        return "OPUS_INTERNAL_ERROR";

      case Opus.OPUS_INVALID_PACKET:
        return "OPUS_INVALID_PACKET";

      case Opus.OPUS_UNIMPLEMENTED:
        return "OPUS_UNIMPLEMENTED";

      case Opus.OPUS_INVALID_STATE:
        return "OPUS_INVALID_STATE";

      case Opus.OPUS_ALLOC_FAIL:
        return "OPUS_ALLOC_FAIL";

      default:
        return "UNKNOWN_ERROR";
    }
  }

  private static boolean ensureOpusLib() {
    try {
      OpusLibrary.loadFromJar();
    } catch(UnsupportedOperationException e) {
      LOGGER.fatal("This System does not support opus-lib! What are you even trying to run Minecraft on?");
      e.printStackTrace();
      return false;
    } catch(IOException e) {
      LOGGER.fatal("Error loading opus-lib native");
      e.printStackTrace();
      return false;
    }

    return true;
  }
}
