package kynake.minecraft.directionaldiscord.modules.audio.broadcast;

// Internal
import kynake.minecraft.directionaldiscord.config.PrivateConfig;
import kynake.minecraft.directionaldiscord.modules.audio.broadcast.network.PacketSendBroadcastAudio;
import kynake.minecraft.directionaldiscord.network.Networking;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

// Minecraft
import net.minecraft.server.MinecraftServer;


public class BroadcastAudio {
  private MinecraftServer serverInstance;

  public BroadcastAudio() {
    serverInstance = ServerLifecycleHooks.getCurrentServer();
    if(serverInstance == null) {
      throw new IllegalStateException("Not on Server Side");
    }
  }

  public void sendAudioToOtherPlayers(byte[] audioSample, String discordUserID) {
    String minecraftUUID = PrivateConfig.getVerifiedUsers().get(discordUserID);
    serverInstance.getPlayerList().getPlayers().forEach(player -> {
      if(player.getUniqueID().toString().equalsIgnoreCase(minecraftUUID)) {
        return;
      }

      Networking.sendToClient(new PacketSendBroadcastAudio(audioSample), player);
    });
  }
}
