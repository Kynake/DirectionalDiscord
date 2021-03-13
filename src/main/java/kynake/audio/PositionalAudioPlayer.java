package kynake.audio;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;

// Forge
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

// Minecraft
import net.minecraft.client.Minecraft;

// Minecraft
import net.minecraft.util.math.vector.Vector3d;

// Java
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
/**
 * Audio player that plays audio relative to sources and listener position in 3D Space
 */
public class PositionalAudioPlayer implements AudioPlayer {
  public PositionalAudioPlayer() {

  }

  @Override
  public void playPCMSample(byte[] pcmSample, UUID sourceID, Vector3d sourceLocation) {
    Vector3d listenerLocation = getPlayerPosition();
    LOGGER.debug("Playing pcm at: [{},\t{},\t{}]",   sourceLocation.x,   sourceLocation.y,   sourceLocation.z);
    LOGGER.debug("Listening at:   [{},\t{},\t{}]", listenerLocation.x, listenerLocation.y, listenerLocation.z);
  }

  @Override
  public void close() {

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
