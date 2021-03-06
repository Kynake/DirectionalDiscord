package kynake.minecraft.directionaldiscord.modules.lists;

// Internal
import kynake.minecraft.directionaldiscord.DirectionalDiscord;
import kynake.minecraft.directionaldiscord.modules.base.block.BaseModBlock;
import kynake.minecraft.directionaldiscord.modules.base.block.BlockItemType;
import kynake.minecraft.directionaldiscord.modules.speaker.SpeakerBlock;

// Forge
import net.minecraftforge.registries.ObjectHolder;

// Java
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Helper class that lists all block created by this mod and possibly
 * blocks from other mods that are of interest to this mod
 *
 * Also initializes and holds instances of Blocks that will be
 * registered in Forge's BlockRegistry
 */
public class BlockLists {
  private static final Logger LOGGER = LogManager.getLogger();

  // Block References
  @ObjectHolder(DirectionalDiscord.ModID + ":speaker")  public static SpeakerBlock SPEAKER;

  // Other attributes
  private static ArrayList<BaseModBlock> blocksCache = null;

  // Methods

  /**
   * Reflectively get a list of instances of blocks created by this mod
   * Creates new Block instances if such List hasn't yet been initialized
   *
   * This allows the registering of blocks in Forge's BlockRegistry without having
   * to modify the code in multiple places to reference the new Blocks
   */
  public static ArrayList<BaseModBlock> getBlockInstances() {
    // Don't recreate instances, use the ones in Class cache
    if(blocksCache != null) {
      return blocksCache;
    }

    Field[] staticFields = BlockLists.class.getDeclaredFields();

    ArrayList<BaseModBlock> blocksListed = new ArrayList<BaseModBlock>();

    for(Field field : staticFields) {
      // Ignore non mod Block attributes
      if(!BaseModBlock.class.isAssignableFrom(field.getType())) {
        continue;
      }

      try {
        BaseModBlock blockInstance = (BaseModBlock) field.getType().getDeclaredConstructor().newInstance();
        blocksListed.add(blockInstance);
      } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
        LOGGER.warn(String.format("WARNING: Block field %s could not be reflectively accessed.", field.getName()), e);
      }
    }

    blocksCache = blocksListed;
    return blocksListed;
  }

  public static ArrayList<BaseModBlock> getBlocksWithNoItem() {
    return filterByItemType(BlockItemType.NONE);
  }

  public static ArrayList<BaseModBlock> getBlocksWithDefaultItem() {
    return filterByItemType(BlockItemType.DEFAULT);
  }

  public static ArrayList<BaseModBlock> getBlocksWithCustomItem() {
    return filterByItemType(BlockItemType.CUSTOM);
  }

  private static ArrayList<BaseModBlock> filterByItemType(BlockItemType itemType) {
    ArrayList<BaseModBlock> filteredList = new ArrayList<BaseModBlock>(getBlockInstances());
    filteredList.removeIf(block -> block.itemType != itemType);
    return filteredList;
  }
}