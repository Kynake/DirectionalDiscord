package kynake.minecraft.directionaldiscord.modules.verification;

// Internal
import kynake.discord.ListeningBot;
import kynake.minecraft.directionaldiscord.config.Config;

// JDA
import net.dv8tion.jda.api.entities.User;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

// Java
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

public class Verify {
  public enum VerificationStatus {
    AlreadyVerified, // User verification is correct on both
    DiscordUserVerified,
    MinecraftUserVerified,
    StillUnverified,
    VerificationComplete,
    UnknownUser;
  }

  // MinecraftUUID -> DiscordTag -> DiscordUser
  private static Map<UUID, Map<String, User>> userVerifications = new HashMap<UUID, Map<String, User>>();

  private static MinecraftServer serverInstance = null;
  static {
    serverInstance = ServerLifecycleHooks.getCurrentServer();
    if(serverInstance == null) {
      throw new IllegalStateException("Not on Server Side");
    }
  }

  // Add a new user to the list of possible verifications (Called from Bot)
  public static VerificationStatus addUserForVerification(User discordUser, String minecraftName) {
    // Check Unknown
    ServerPlayerEntity player;
    try { // Iterate over all players on the server and find the one whose name was requested
      player = serverInstance.getPlayerList().getPlayers().stream().filter(playerIter -> playerIter.getDisplayName().getString().equals(minecraftName)).findAny().get();
    } catch(NoSuchElementException e) {
      // This Minecraft user was not found on the server
      return VerificationStatus.UnknownUser;
    }

    // Check Verified
    VerificationStatus configVerification = checkConfigVerification(discordUser.getId(), player.getUniqueID());
    if(configVerification != VerificationStatus.StillUnverified) {
      return configVerification;
    }

    String discordUserTag = discordUser.getAsTag();

    // Add to Verification List
    Map<String, User> discordUserMap = userVerifications.computeIfAbsent(player.getUniqueID(), k -> new HashMap<String, User>(1));
    if(!discordUserMap.containsKey(discordUserTag)) {
      discordUserMap.put(discordUserTag, discordUser);
    }

    return VerificationStatus.StillUnverified;
  }

  // Called from Minecraft server
  public static VerificationStatus verifyUser(ServerPlayerEntity minecraftUser, String discordUserTag) {
    UUID minecraftUUID = minecraftUser.getUniqueID();
    User discordUser = null;

    // Check invalid tag
    try {
      // This might retutn null if the user was not previously cached, even though a user with this tag might exist
      // The only reliable way to query all of Discord for a specific user is using the UserID
      discordUser = ListeningBot.jda.getUserByTag(discordUserTag);
    } catch(IllegalArgumentException e) {
      // Invalid tag
      return VerificationStatus.UnknownUser;
    }

    // Check Verified
    VerificationStatus configVerification = checkConfigVerification(discordUser == null? null : discordUser.getId(), minecraftUUID);
    if(configVerification != VerificationStatus.StillUnverified) {
      return configVerification;
    }


    // Check Verification List
    Map<String, User> verificationsForUUID = userVerifications.get(minecraftUUID);
    if(verificationsForUUID == null) {
      // No verification was started for this Minecraft user
      return VerificationStatus.StillUnverified;
    }

    discordUser = verificationsForUUID.get(discordUserTag);
    if(discordUser == null) {
      // No verification was started for this Discord user
      return VerificationStatus.StillUnverified;
    }

    // Verification started from Discord is complete
    Config.addVerifiedUser(discordUser.getId(), minecraftUUID.toString());

    // Clear other verification attempts for this Minecraft user
    verificationsForUUID.clear();
    userVerifications.remove(minecraftUUID);

    return VerificationStatus.VerificationComplete;
  }

  private static VerificationStatus checkConfigVerification(String discordID, UUID minecraftUUID) {
    String verifiedUUID = Config.getVerifiedUsers().get(discordID);
    String minecraftUUIDString = minecraftUUID.toString();
    if(verifiedUUID != null) {
      // Already verified this minecraft user to this Discord user / The discord user is already verified for a different Minecraft user
      return verifiedUUID.equals(minecraftUUIDString)? VerificationStatus.AlreadyVerified : VerificationStatus.DiscordUserVerified;
    }

    // TODO separate this code from this method,
    // Since the Minecraft command most likely won't cache the discord user, and so the code above this will never return early
    for(Map.Entry<String, String> entry : Config.getVerifiedUsers().entrySet()) {
      if(entry.getValue().equals(minecraftUUIDString)) {
        // Already verified this Minecraft user to a different Discord user
        return VerificationStatus.MinecraftUserVerified;
      }
    }

    return VerificationStatus.StillUnverified;
  }
}
