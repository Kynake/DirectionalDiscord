package kynake.minecraft.directionaldiscord.modules.verification;

// Internal
import kynake.minecraft.directionaldiscord.DirectionalDiscord;

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

public class CommandVerify implements Command<CommandSource> {
  public static final String commandString = "verify";
  public static final String argName = "DiscordName#1234";
  private static final CommandVerify CMD = new CommandVerify();

  public static ArgumentBuilder<CommandSource, ?> register(CommandDispatcher<CommandSource> dispatcher) {
    return Commands.literal(commandString)
      .requires(cs -> cs.hasPermissionLevel(0))
      .then(
        Commands.argument(argName, StringArgumentType.greedyString())
        .executes(CMD)
      );
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

    String discordUserTag = context.getArgument(argName, String.class);

    switch(Verify.verifyUser(player, discordUserTag)) {
      case UnknownUser:
        sendMessage(context, "Invalid Discord User Tag: " + discordUserTag, true);
        break;

      case AlreadyVerified:
      case MinecraftUserVerified:
        sendMessage(context, "You're already verified! You can unverify yourself by running the command '/" + DirectionalDiscord.ModID + " " + CommandUnverify.commandString + "'", false);
        break;

      case DiscordUserVerified:
        sendMessage(context, discordUserTag + " is already verified to a different Minecraft user", true);
        break;

      case StillUnverified:
        sendMessage(context, "No verification attempt for " + discordUserTag + " exists. To create one, as " + discordUserTag + ", send a DM to the bot on Discord with the message '/verify <Your_Minecraft_Username>'", true);
        break;

      case VerificationComplete:
        sendMessage(context, "Verification successful! You're now verified as " + discordUserTag, false);
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
