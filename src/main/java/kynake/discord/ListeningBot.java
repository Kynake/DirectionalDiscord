package kynake.discord;

// Internal
import kynake.discord.audio.AudioCapture;
import kynake.minecraft.directionaldiscord.config.PrivateConfig;

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
  private static final Logger LOGGER = LogManager.getLogger();
  public static JDA jda = null;

  private BiConsumer<byte[], String> audioHandler;
  private AudioCapture audioCapturer;
  private CommandHandler commandHandler;
  private VoiceChannelHandler voiceChannelHandler;

  public static Guild getGuild() {
    return jda == null?
      null :
      jda.getGuildById(PrivateConfig.getServerID());
  }

  public static VoiceChannel getVoiceChannel() {
    return jda == null?
      null :
      jda.getVoiceChannelById(PrivateConfig.getVoiceChannelID());
  }

  public static Long getSelfID() {
    return jda == null?
      null :
      jda.getSelfUser().getIdLong();
  }

  public ListeningBot(BiConsumer<byte[], String> handler) throws LoginException {
    try {
      voiceChannelHandler = new VoiceChannelHandler();
      commandHandler = new CommandHandler();

      jda = JDABuilder.create(PrivateConfig.getToken(),
        GatewayIntent.GUILD_MESSAGES,
        GatewayIntent.GUILD_VOICE_STATES,
        GatewayIntent.DIRECT_MESSAGES
      ).addEventListeners(
        this,
        voiceChannelHandler,
        commandHandler
      ).build();

      // disableCache(CacheFlag.ACTIVITY, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)

    } catch(LoginException | IllegalArgumentException e) {
      LOGGER.error(e.getMessage());
      throw new LoginException(e.getMessage());
    }

    this.audioHandler = handler;
  }

  public void shutdown() {
    LOGGER.info("Shutting down Discord Bot...");
    jda.shutdown();
  }

  @Override
  public void onReady(ReadyEvent event) {
    Guild targetGuild = getGuild();
    if(targetGuild == null || jda.getGuilds().stream().noneMatch(guild -> guild.getIdLong() == targetGuild.getIdLong())) {
      LOGGER.error("This Bot is not part of the specified Guild!");
      shutdown();
      return;
    }

    try {
      // Open audio connection
      audioCapturer = new AudioCapture(audioHandler);

    } catch(Exception e) {
      if(jda != null) {
        jda.shutdownNow();
      }

      LOGGER.error("Unhandled Exception on ListeningBot");
      e.printStackTrace();
    }
  }
}
