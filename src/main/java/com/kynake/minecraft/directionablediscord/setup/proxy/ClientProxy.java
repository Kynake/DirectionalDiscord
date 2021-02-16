package com.kynake.minecraft.directionablediscord.setup.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class ClientProxy implements IProxy {

  @Override
  public World getClientWorld() throws IllegalStateException {
    return Minecraft.getInstance().world;
  }
}
