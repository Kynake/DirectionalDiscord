package com.kynake.minecraft.directionablediscord.setup.proxy;

import net.minecraft.world.World;

public interface IProxy {
  World getClientWorld() throws IllegalStateException;
}
