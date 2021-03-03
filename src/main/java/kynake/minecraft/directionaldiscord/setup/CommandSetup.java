package kynake.minecraft.directionaldiscord.setup;

// Internal
import kynake.minecraft.directionaldiscord.DirectionalDiscord;
import kynake.minecraft.directionaldiscord.modules.verification.CommandVerify;

// Minecraft
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class CommandSetup {
  public static void registerCommands(CommandDispatcher<CommandSource> dispatcher) {
    LiteralCommandNode<CommandSource> modBaseCommand = dispatcher.register(
      Commands.literal(DirectionalDiscord.ModID)
        .then(CommandVerify.register(dispatcher))
    );

    // TODO redirect doesnt recognize command pattern?
    // dispatcher.register(Commands.literal("dd").redirect(modBaseCommand));
  }
}
