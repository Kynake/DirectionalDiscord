package com.kynake.minecraft.directionaldiscord.modules.broadcast;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.kynake.minecraft.directionaldiscord.config.Config;
import com.kynake.minecraft.directionaldiscord.modules.broadcast.network.PacketSendAudio;
import com.kynake.minecraft.directionaldiscord.network.Networking;

// Minecraft
import net.minecraft.server.MinecraftServer;


public class AudioBroadcast {
  private MinecraftServer serverInstance;

  public AudioBroadcast() {
    serverInstance = ServerLifecycleHooks.getCurrentServer();
    if(serverInstance == null) {
      throw new IllegalStateException("Not on Server Side");
    }
  }

  public void sendAudioToOtherPlayers(byte[] audioSample, String discordUserID) {
    String minecraftUUID = Config.getVerifiedUsers().get(discordUserID);
    serverInstance.getPlayerList().getPlayers().forEach(player -> {
      if(player.getUniqueID().toString().equalsIgnoreCase(minecraftUUID)) {
        return;
      }

      Networking.sendToClient(new PacketSendAudio(audioSample), player);
    });
  }
}
