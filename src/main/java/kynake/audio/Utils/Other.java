package kynake.audio.Utils;

// Minecraft
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;

public class Other {

  @SuppressWarnings({"resource"})
  public static Vector3d getListenerLocation() {
    return  Minecraft.getInstance().player.getPositionVec();
  }

  @SuppressWarnings({"resource"})
  public static Vector3d getListenerLook() {
    return Minecraft.getInstance().player.getLookVec();
  }

  @SuppressWarnings({"resource"})
  public static Vector3d getListenerUp() {
    return Minecraft.getInstance().player.getUpVector(1.0f).normalize();
  }
}
