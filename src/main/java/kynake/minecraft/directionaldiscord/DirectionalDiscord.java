package kynake.minecraft.directionaldiscord;

// Internal
import kynake.discord.ListeningBot;
import kynake.minecraft.directionaldiscord.config.PrivateConfig;

// Forge
import net.minecraftforge.fml.common.Mod;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.io.IOException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DirectionalDiscord.ModID)
public class DirectionalDiscord {
  // Constants
  public final static String ModID = "directionaldiscord";
  private static final Logger LOGGER = LogManager.getLogger();

  public static ListeningBot discordBot;

  public DirectionalDiscord() throws IOException {
    LOGGER.info("HELLO from Mod Build");
    PrivateConfig.acquireConfigs();

    LOGGER.info(PrivateConfig.getServerID());
    LOGGER.info(PrivateConfig.getVoiceChannelID());
    LOGGER.info(PrivateConfig.getVerifiedUsers());
  }


}
