package kynake.minecraft.directionaldiscord.modules.speaker;

// Internal
import kynake.minecraft.directionaldiscord.modules.base.block.BaseModBlock;

// Minecraft
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
public class SpeakerBlock extends BaseModBlock {

  public SpeakerBlock() {
    super(Properties
      .create(Material.WOOD)
      .sound(SoundType.WOOD)
      .hardnessAndResistance(2.0f)
    );

    setRegistryName("speaker");
  }
}
