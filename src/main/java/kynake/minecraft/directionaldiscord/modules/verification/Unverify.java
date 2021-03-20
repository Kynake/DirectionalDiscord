package kynake.minecraft.directionaldiscord.modules.verification;

// Internal
import kynake.minecraft.directionaldiscord.config.PrivateConfig;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;

// Java
import java.util.Map;

public class Unverify {
  public static boolean unverifyDiscordUser(String discordUserID) {
    String minecraftUUID = PrivateConfig.getVerifiedUsers().get(discordUserID);
    if(minecraftUUID == null) {
      return false;
    }

    PrivateConfig.removeVerifiedUser(discordUserID);
    return true;
  }

  public static boolean unverifyMinecraftUser(ServerPlayerEntity minecraftUser) {
    String minecraftUUID = minecraftUser.getUniqueID().toString();

    String verifiedUser = null;
    for(Map.Entry<String, String> entry : PrivateConfig.getVerifiedUsers().entrySet()) {
      if(entry.getValue().equalsIgnoreCase(minecraftUUID)) {
        // This Minecraft user is verified to a Discord user
        verifiedUser = entry.getKey();
        break;
      }
    }

    if(verifiedUser == null) {
      return false;
    }

    PrivateConfig.removeVerifiedUser(verifiedUser);
    return true;
  }
}
