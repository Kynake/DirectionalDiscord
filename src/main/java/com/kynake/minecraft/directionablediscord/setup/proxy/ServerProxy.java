package com.kynake.minecraft.directionablediscord.setup.proxy;

import net.minecraft.world.World;

public class ServerProxy implements IProxy {

  @Override
  public World getClientWorld() throws IllegalStateException {
    throw new IllegalStateException("Only run this on the client!");
  }
}
