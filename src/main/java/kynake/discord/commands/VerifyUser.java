package kynake.discord.commands;

// Internal
import kynake.discord.CommandHandler;
import kynake.minecraft.directionaldiscord.modules.verification.Verify;

// JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VerifyUser implements Command {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final String commandString = "verify";

  public String getCommandString() {
    return commandString;
  }

  @Override
  public String commandTemplate() {
    return CommandHandler.prefix + getCommandString() + " <Minecraft_Username>";
  }


  public void onGuildMessage(GuildMessageReceivedEvent event, String[] args) {
   sendMessage(event,
      "**This command should be sent as a Direct Message.**\nTo verify your Discord user against a Minecraft user, send a DM to this bot in the format:\n`"
      + commandTemplate() + "`, and follow the instructions given."
    );
  }

  public void onPrivateMessage(PrivateMessageReceivedEvent event, String[] args) {
    if(args.length == 0) {
      sendMessage(event,
        "**This command requires and argument.**\nPlease provide your Minecraft username as follows:\n`" + commandTemplate() + "`"
      );
      return;
    }

    String user = args[0];
    switch(Verify.addUserForVerification(event.getAuthor(), user)) {
      case UnknownUser:
      sendMessage(event,
        "**" + user + " was not found.**\nMake sure you're logged in to the Minecraft server before attempting to verify your Discord user."
      );
      break;

      case AlreadyVerified:
        sendMessage(event,                                                                                                 // TODO: these commands dont exist yet
          "**You're all set!**\nThis Discord User is already verified to " + user + ".\nYou can unverify this user by either typing `/unverify` here or `/directionaldiscord unverify` in the Minecraft server chat."
        );
        break;

      case DiscordUserVerified:
        sendMessage(event,                                                                                             // TODO: these commands dont exist yet
          "**You're already verified to a different Minecraft user.**\nYou can unverify yourself by either typing `/unverify` here or `/directionaldiscord unverify` in the Minecraft server chat."
        );
        break;

      case MinecraftUserVerified:
        sendMessage(event,                                                                                                                            // TODO: this command doesn't exist yet
          "**" + user + " is already verified to a different Discord user.**\nIf you're logged in as " + user + " you can unverify yourself by typing `/directionaldiscord unverify` in the Minecraft server chat."
        );
        break;

      case StillUnverified:
        sendMessage(event,                                                                                // TODO: this command doesn't exist yet
          "**Verification request sent.**\nTo complete verification, as " + user + " in the Minecraft server, type `/directionablediscord verify" + event.getAuthor().getAsTag() + "`"
        );
        break;
    }

  };
}
