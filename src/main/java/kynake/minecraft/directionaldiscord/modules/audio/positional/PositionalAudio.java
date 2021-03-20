package kynake.minecraft.directionaldiscord.modules.audio.positional;


import java.util.List;
import java.util.UUID;

// Internal
import kynake.minecraft.directionaldiscord.config.Config;
import kynake.minecraft.directionaldiscord.modules.audio.positional.network.PacketSendPositionalAudio;
import kynake.minecraft.directionaldiscord.network.Networking;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// Minecraft
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;

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
    String minecraftUUID = Config.getVerifiedUsers().get(discordUserID);
    if(minecraftUUID == null) {
      return null;
    }

    return serverInstance.getPlayerList().getPlayerByUUID(UUID.fromString(minecraftUUID));
  }

  private boolean isNearbySpeaker(ServerPlayerEntity listener, ServerPlayerEntity speaker) {
    double distance = 100 + 10; // TODO define this in server config

    // Players in different dimensions are not nearby one another
    if(listener.world.getDimensionKey() != speaker.world.getDimensionKey()) {
      return false;
    }

    return listener.getDistanceSq(speaker) < (distance * distance);
  }
}
