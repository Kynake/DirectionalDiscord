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
import net.minecraft.entity.player.ServerPlayerEntity;
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
    ServerPlayerEntity player;
    try {
      player = context.getSource().asPlayer();
    } catch(CommandSyntaxException e) {
      sendMessage(context, "This command must be run as a Player", true);
      return 0;
    }

    String discordTag = context.getArgument(argName, String.class);

    switch(Verify.verifyUser (player, discordTag)) {
      case UnknownUser:
        sendMessage(context, "Invalid Discord User ID", true);
        break;

      case AlreadyVerified:
        sendMessage(context, "You're all set!", false);
        break;

      case DiscordUserVerified:
        sendMessage(context, discordTag + " is already verified to a different Minecraft user", true);
        break;

      case MinecraftUserVerified:
        sendMessage(context, "You're already verified to a different Discord user. You can unverify yourself by running the command /directionaldiscord unverify", false);
        break;

      case StillUnverified:
        sendMessage(context, "No verification attempt for " + discordTag + " exists. As " + discordTag + " send a DM to the bot with the message '/verify <Minecraft_Username>'", true);
        break;

      case VerificationComplete:
        sendMessage(context, "Verification successful! You're now verified with " + discordTag, false);
        break;
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
