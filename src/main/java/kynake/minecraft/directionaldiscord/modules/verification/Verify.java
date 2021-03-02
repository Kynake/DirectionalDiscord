package kynake.minecraft.directionaldiscord.modules.verification;

// Internal
import kynake.minecraft.directionaldiscord.config.Config;

// JDA
import net.dv8tion.jda.api.entities.User;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Verify {
  public enum VerificationStatus {
    AlreadyVerified, // User verification is correct on both
    DiscordUserVerified,
    MinecraftUserVerified,
    StillUnverified,
    UnknownUser;
  }

  private static final Logger LOGGER = LogManager.getLogger();
  private static Map<String, List<String>> userVerifications = null;

  private static MinecraftServer serverInstance = null;
  static {
    serverInstance = ServerLifecycleHooks.getCurrentServer();
    if(serverInstance == null) {
      throw new IllegalStateException("Not on Server Side");
    }
  }

  // Add a new user to the list of possible verifications
  public static VerificationStatus addUserForVerification(User discordUser, String minecraftName) {
    ServerPlayerEntity player = null;
    try { // Iterate over all players on the server and find the one whose name was requested
      player = serverInstance.getPlayerList().getPlayers().stream().filter(playerIter -> playerIter.getDisplayName().getString().equals(minecraftName)).findAny().get();
    } catch(NoSuchElementException e) {
      return VerificationStatus.UnknownUser;
    }

    String playerUUID = player.getUniqueID().toString();

    String verifiedUUID = Config.getVerifiedUsers().get(discordUser.getId());
    if(verifiedUUID != null) {
      return verifiedUUID.equalsIgnoreCase(playerUUID)? VerificationStatus.AlreadyVerified : VerificationStatus.DiscordUserVerified;
    }

    for (Map.Entry<String, String> entry : Config.getVerifiedUsers().entrySet()) {
      if(entry.getValue().equalsIgnoreCase(playerUUID)) {
        return VerificationStatus.MinecraftUserVerified;
      }
    }

    String discordTag = discordUser.getAsTag();
    List<String> userList = userVerifications.computeIfAbsent(player.getUniqueID().toString(), k -> new ArrayList<String>(1));
    if(!userList.contains(discordTag)) {
      userList.add(discordTag);
    }
    return VerificationStatus.StillUnverified;

  }
}
