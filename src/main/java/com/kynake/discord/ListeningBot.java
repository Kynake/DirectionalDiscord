package com.kynake.discord;

// Internal
import com.kynake.discord.audio.AudioCapture;
import com.kynake.minecraft.directionaldiscord.config.Config;

// JDA
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

// Java
import java.util.function.BiConsumer;
import javax.security.auth.login.LoginException;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ListeningBot extends ListenerAdapter {
  public static final Logger LOGGER = LogManager.getLogger();
  public static JDA jda = null;

  private BiConsumer<byte[], String> handler;
  private AudioCapture capturer;

  public static Guild getGuild() {
    return jda == null?
      null :
      jda.getGuildById(Config.getServerID());
  }

  public static VoiceChannel getVoiceChannel() {
    return jda == null?
      null :
      jda.getVoiceChannelById(Config.getVoiceChannelID());
  }

  public ListeningBot(BiConsumer<byte[], String> handler) {
    try {
      jda = JDABuilder.create(Config.getToken(), GatewayIntent.GUILD_VOICE_STATES).addEventListeners(this).build();
    } catch(LoginException e) {
      LOGGER.error("Invalid Discord Token");
      return;
    }

    // TODO: This needs to be checked AFTER the bot has initialized
    // Guild targetGuild = jda.getGuildById(Secrets.guildID);
    // if(jda.getGuilds().stream().noneMatch(guild -> guild.getIdLong() == targetGuild.getIdLong())) {
    //   LOGGER.error("This Bot is not part of the specified Guild!");
    //   shutdown();
    //   return;
    // }

    this.handler = handler;
  }

  public void shutdown() {
    LOGGER.info("Shutting down Discord Bot...");
    jda.shutdown();
  }

  @Override
  public void onReady(ReadyEvent event) {
    capturer = new AudioCapture(handler);
  }
}
