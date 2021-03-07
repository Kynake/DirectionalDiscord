package kynake.minecraft.directionaldiscord.modules.verification;

// Internal
import kynake.discord.ListeningBot;

// Minecraft
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;

public class CommandUnverify implements Command<CommandSource> {
  public static final String commandString = "unverify";
  private static final CommandUnverify CMD = new CommandUnverify();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.literal(commandString)
      .requires(cs -> cs.hasPermissionLevel(0))
      .executes(CMD);
  }

  @Override
  public int run(CommandContext<CommandSource> context) {
    ServerPlayerEntity player;
    try {
      player = context.getSource().asPlayer();
    } catch(CommandSyntaxException e) {
      sendMessage(context, "This command must be run as a Player", true);
      return 0;
    }

    if(Unverify.unverifyMinecraftUser(player)) {
      // Was able to unverify this user
      sendMessage(context, "Done!\nYou are now unverified.", false);
    } else {
      // This user wasn't verified
      sendMessage(context, "You are not verified to a Discord user", true);
    }

    return 0;
  }

  private void sendMessage(CommandContext<CommandSource> context, String message, boolean error) {
    CommandSource src = context.getSource();
    StringTextComponent text = new StringTextComponent(message);

    if(error) {
     src.sendErrorMessage(text);
    } else {
     src.sendFeedback(text, false);
    }
  }
}
