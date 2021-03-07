package kynake.minecraft.directionaldiscord.modules.verification;

import kynake.discord.ListeningBot;
// Internal
import kynake.minecraft.directionaldiscord.config.Config;

// JDA
import net.dv8tion.jda.api.entities.User;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

// Java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class Verify {
  public enum VerificationStatus {
    AlreadyVerified, // User verification is correct on both
    DiscordUserVerified,
    MinecraftUserVerified,
    StillUnverified,
    VerificationComplete,
    UnknownUser;
  }

  private static Map<String, List<String>> userVerifications = new HashMap<String, List<String>>();

  private static MinecraftServer serverInstance = null;
  static {
    serverInstance = ServerLifecycleHooks.getCurrentServer();
    if(serverInstance == null) {
      throw new IllegalStateException("Not on Server Side");
    }
  }

  // Add a new user to the list of possible verifications
  public static VerificationStatus addUserForVerification(User discordUser, String minecraftName) {
    // Check Unknown
    ServerPlayerEntity player;
    try { // Iterate over all players on the server and find the one whose name was requested
      player = serverInstance.getPlayerList().getPlayers().stream().filter(playerIter -> playerIter.getDisplayName().getString().equals(minecraftName)).findAny().get();
    } catch(NoSuchElementException e) {
      // This Minecraft user was not found on the server
      return VerificationStatus.UnknownUser;
    }

    String minecraftUUID = player.getUniqueID().toString();
    String discordID = discordUser.getId();

    // Check Verified
    VerificationStatus configVerification = checkConfigVerification(discordID, minecraftUUID);
    if(configVerification != VerificationStatus.StillUnverified) {
      return configVerification;
    }

    // Add to Verification List
    List<String> userList = userVerifications.computeIfAbsent(minecraftUUID, k -> new ArrayList<String>(1));
    if(!userList.contains(discordID)) {
      userList.add(discordID);
    }

    return VerificationStatus.StillUnverified;
  }

  public static VerificationStatus verifyUser(ServerPlayerEntity minecraftUser, String discordID) {
    // Check Unknown
    try {
      ListeningBot.jda.getUserById(discordID);
    } catch(IllegalArgumentException e) {
      // Invalid Discord User
      return VerificationStatus.UnknownUser;
    }

    String minecraftUUID = minecraftUser.getUniqueID().toString();

    // Check Verified
    VerificationStatus configVerification = checkConfigVerification(discordID, minecraftUUID);
    if(configVerification != VerificationStatus.StillUnverified) {
      return configVerification;
    }

    // Check Verification List
    List<String> discordUserList = userVerifications.get(minecraftUUID);
    if(discordUserList != null && discordUserList.contains(discordID)) {
      // Verification started from Discord is complete
      Config.addVerifiedUser(discordID, minecraftUUID);
      userVerifications.remove(minecraftUUID); // Clear other verification attempts for this Minecraft user
      return VerificationStatus.VerificationComplete;
    }

    // No verification was started for this Discord user
    return VerificationStatus.StillUnverified;
  }

  private static VerificationStatus checkConfigVerification(String discordID, String minecraftUUID) {
    String verifiedUUID = Config.getVerifiedUsers().get(discordID);
    if(verifiedUUID != null) {
      // Already verified this minecraft user to this Discord user / The discord user is already verified for a different Minecraft user
      return verifiedUUID.equalsIgnoreCase(minecraftUUID)? VerificationStatus.AlreadyVerified : VerificationStatus.DiscordUserVerified;
    }

    for(Map.Entry<String, String> entry : Config.getVerifiedUsers().entrySet()) {
      if(entry.getValue().equalsIgnoreCase(minecraftUUID)) {
        // Already verified this Minecraft user to a different Discord user
        return VerificationStatus.MinecraftUserVerified;
      }
    }

    return VerificationStatus.StillUnverified;
  }
}
