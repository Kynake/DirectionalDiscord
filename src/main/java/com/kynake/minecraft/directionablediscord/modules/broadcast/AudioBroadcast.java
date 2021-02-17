package com.kynake.minecraft.directionablediscord.modules.broadcast;

// Forge
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.kynake.minecraft.directionablediscord.modules.broadcast.network.PacketSendAudio;
import com.kynake.minecraft.directionablediscord.network.Networking;

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

  public void sendAudioToAllPlayers(byte[] audioSample) {
    serverInstance.getPlayerList().getPlayers().forEach(player -> Networking.sendToClient(new PacketSendAudio(audioSample), player));
  }
}
