package kynake.minecraft.directionaldiscord.modules.verification;

// Minecraft
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CommandVerify implements Command<CommandSource> {
  private static final Logger LOGGER = LogManager.getLogger();

  public static final String commandString = "verify";
  public static final String argName = "Discord_Tag";
  private static final CommandVerify CMD = new CommandVerify();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.literal(commandString)
      .requires(cs -> cs.hasPermissionLevel(0))
      .then(Commands.argument(argName, StringArgumentType.string())
      .executes(CMD));
  }

  @Override
  public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
    String discordTag = context.getArgument(argName, String.class);

    context.getSource().sendFeedback(new StringTextComponent("Echoed " + discordTag), false);

    return 0;
  }

}
