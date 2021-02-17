package com.kynake.discord;

// JDA
import net.dv8tion.jda.api.audio.AudioReceiveHandler;
import net.dv8tion.jda.api.audio.UserAudio;
import net.dv8tion.jda.api.managers.AudioManager;

// Java
import java.util.function.Consumer;

public class AudioCapture implements AudioReceiveHandler {
  private Consumer<byte[]> handler;

  public AudioCapture(Consumer<byte[]> handler) {
    this.handler = handler;
    AudioManager manager = ListeningBot.getGuild().getAudioManager();

    manager.setReceivingHandler(this);
    manager.openAudioConnection(ListeningBot.getVoiceChannel());
  }

  @Override
  public boolean canReceiveUser() {
    return true;
  }

  @Override
  public void handleUserAudio(UserAudio userAudio) {
    ListeningBot.LOGGER.debug("Got AudioPacket from user " + userAudio.getUser().getName());
    handler.accept(userAudio.getAudioData(1.0f));
  }
}
