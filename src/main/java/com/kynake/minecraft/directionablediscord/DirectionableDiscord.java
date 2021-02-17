package com.kynake.minecraft.directionablediscord;

// Internal
import com.kynake.discord.ListeningBot;

// Forge
import net.minecraftforge.fml.common.Mod;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DirectionableDiscord.ModID)
public class DirectionableDiscord {
  // Constants
  public final static String ModID = "directionablediscord";
  public static final Logger LOGGER = LogManager.getLogger();

  public static ListeningBot discordBot;

  public DirectionableDiscord() {
    LOGGER.info("HELLO from Mod Build");
  }


}
