package com.kynake.minecraft.directionaldiscord;

// Internal
import com.kynake.discord.ListeningBot;
import com.kynake.minecraft.directionaldiscord.config.Config;

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
  public static final Logger LOGGER = LogManager.getLogger();

  public static ListeningBot discordBot;

  public DirectionalDiscord() throws IOException {
    LOGGER.info("HELLO from Mod Build");
    Config.acquireConfigs();

    LOGGER.info(Config.getServerID());
    LOGGER.info(Config.getVoiceChannelID());
    LOGGER.info(Config.getVerifiedUsers());
  }


}
