package kynake.discord.audio;

// Internal
import kynake.discord.ListeningBot;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.OpusPacket;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.managers.AudioManager;

// Java
import java.util.function.BiConsumer;

public class AudioCapture implements AudioReceiveHandler {
  private BiConsumer<byte[], String> handler;

  public AudioCapture(BiConsumer<byte[], String> handler) {
    this.handler = handler;
    AudioManager manager = ListeningBot.getGuild().getAudioManager();

    manager.setReceivingHandler(this);
    manager.openAudioConnection(ListeningBot.getVoiceChannel());
  }

  @Override
  public boolean canReceiveEncoded() {
    return true;
  }

  @Override
  public void handleUserAudio(UserAudio userAudio) {
    // ListeningBot.LOGGER.debug("Got AudioPacket from user " + userAudio.getUser().getName());
    handler.accept(userAudio.getAudioData(1.0f), userAudio.getUser().getId());
  }

  @Override
  public void handleEncodedAudio(OpusPacket packet) {
    if(packet.canDecode()) {
      // Decode the packet to decoder sequence and timestamp before sending
      packet.getAudioData(1.0f);

      handler.accept(packet.getOpusAudio(), Long.toString(packet.getUserId()));
    }
  }
}
