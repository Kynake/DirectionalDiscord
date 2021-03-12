package kynake.audio;

// PaulsCode
import paulscode.sound.Library;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.libraries.LibraryJavaSound;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;

// Forge
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PositionalAudioPlayer {
  private static final Logger LOGGER = LogManager.getLogger();

  private static SoundSystem soundSystem = null;
  private Map<UUID, String> speakerSources = new HashMap<>();

  public PositionalAudioPlayer() {
    if(soundSystem == null) {
      // Link Libraries before initializing soundSystem
      Class<? extends Library> soundLibrary = LibraryLWJGLOpenAL.class;

      // Initialize
      try {
        soundSystem = new SoundSystem(soundLibrary);
      } catch(SoundSystemException e) {
        e.printStackTrace();
        LOGGER.error("error linking with the LibraryJavaSound plug-in");
        soundSystem = null;
        return;
      }
    }
  }

  public void playPCMSample(byte[] pcmSample, UUID speaker, Vector3d locationIn) {
    if(soundSystem == null) {
      LOGGER.error("Cannot Play sound, AudioSystem was not initialized");
      return;
    }

    LOGGER.info("here");
    Vector3d location = applyDebugTransform(locationIn);

    // Initialize and set up the speaker and it's position
    // String previousState = speakerSources.get(speaker);

    speakerSources.computeIfAbsent(speaker, uuid -> {
      String newSourceName = uuid.toString();
      soundSystem.rawDataStream(
        AudioReceiveHandler.OUTPUT_FORMAT,
        false,
        newSourceName,

        // Initial position is set on Source initialization
        (float) location.x,
        (float) location.y,
        (float) location.z,

        SoundSystemConfig.ATTENUATION_LINEAR,
        48
      );

      return newSourceName;
    });

    String sourceName = speakerSources.get(speaker);

    // Recalculate position if it was previously initialized
    // if(previousState != null) {
    soundSystem.setPosition(sourceName, (float) location.x, (float) location.y, (float) location.z);
    // }

    // Recalculate listener (the Player) position
    Vector3d playerPosition = getPlayerPosition();
    Vector3d playerLook = getPlayerLook();
    Vector3d playerUp = getPlayerUp();

    soundSystem.setListenerPosition((float) playerPosition.x, (float) playerPosition.y, (float) playerPosition.z);
    soundSystem.setListenerOrientation(
      (float) playerLook.x, (float) playerLook.y, (float) playerLook.z,
      (float) playerUp.x,   (float) playerUp.y,   (float) playerUp.z // 0, 1, 0
    );

    // Feed the received audio and make sure it's playing
    soundSystem.feedRawAudioData(sourceName, pcmSample);

    if(!soundSystem.playing(sourceName)) {
      soundSystem.play(sourceName);
    }

    LOGGER.debug("Playing pcm at: [{},\t{},\t{}]", (float) location.x,       (float) location.y,       (float) location.z);
    LOGGER.debug("Listening at:   [{},\t{},\t{}]", (float) playerPosition.x, (float) playerPosition.y, (float) playerPosition.z);
    LOGGER.debug("Look and Up:    [{},\t{},\t{}] [{},\t{},\t{}]",
      (float) playerLook.x, (float) playerLook.y, (float) playerLook.z,
      (float) playerUp.x,   (float) playerUp.y,   (float) playerUp.z
    );
  }

  public void close() {
    speakerSources.forEach((uuid, speakerName) -> {
      soundSystem.flush(speakerName);
    });

    speakerSources.clear();
    soundSystem.cleanup();

    soundSystem = null;
  }

  private Vector3d getPlayerPosition() {

    return Minecraft.getInstance().player.getPositionVec();
  }

  private Vector3d getPlayerLook() {
    return Minecraft.getInstance().player.getLookVec();
  }

  private Vector3d getPlayerUp() {
    return Minecraft.getInstance().player.getUpVector(1.0f).normalize();
  }
}
