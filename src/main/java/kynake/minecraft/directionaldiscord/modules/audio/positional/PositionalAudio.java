package kynake.minecraft.directionaldiscord.modules.audio.positional;

// Internal
import kynake.minecraft.directionaldiscord.config.Constants;
import kynake.minecraft.directionaldiscord.config.PrivateConfig;
import kynake.minecraft.directionaldiscord.modules.audio.positional.network.PacketSendPositionalAudio;
import kynake.minecraft.directionaldiscord.network.Networking;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

// Java
import java.util.UUID;

public class PositionalAudio {
  private MinecraftServer serverInstance;

  public PositionalAudio() {
    serverInstance = ServerLifecycleHooks.getCurrentServer();
    if(serverInstance == null) {
      throw new IllegalStateException("Not on Server Side");
    }
  }

  public void sendAudioToNearbyPlayers(byte[] audioSample, String discordUserID) {
    ServerPlayerEntity speaker = getPlayerFromDiscordID(discordUserID);
    if(speaker == null) {
      return;
    }

    serverInstance.getPlayerList().getPlayers().forEach(player -> {
      // Dont echo audio back to the player that spoke it
      if(player == speaker) {
        return;
      }

      // Only send the sudio to the players that are nearby the speaker
      if(!isNearbySpeaker(player, speaker)) {
        return;
      }

      Networking.sendToClient(new PacketSendPositionalAudio(audioSample, speaker.getUniqueID(), speaker.getPositionVec()), player);
    });
  }

  private ServerPlayerEntity getPlayerFromDiscordID(String discordUserID) {
    String minecraftUUID = PrivateConfig.getVerifiedUsers().get(discordUserID);
    if(minecraftUUID == null) {
      return null;
    }

    return serverInstance.getPlayerList().getPlayerByUUID(UUID.fromString(minecraftUUID));
  }

  private boolean isNearbySpeaker(ServerPlayerEntity listener, ServerPlayerEntity speaker) {
    double distance = Constants.maxDistance + Constants.minDistance;

    // Players in different dimensions are not nearby one another
    if(listener.world.getDimensionKey() != speaker.world.getDimensionKey()) {
      return false;
    }

    return listener.getDistanceSq(speaker) < (distance * distance);
  }
}
