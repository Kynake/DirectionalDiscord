package kynake.discord.commands;

// Internal
import kynake.minecraft.directionaldiscord.modules.verification.Unverify;

// JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UnverifyUser implements Command {
  private static final Logger LOGGER = LogManager.getLogger();

  private static final String commandString = "unverify";
  public String getCommandString() {
    return commandString;
  }


  @Override
  public void onGuildMessage(GuildMessageReceivedEvent event, String[] args) {
    sendMessage(event,
      "**This command should be sent as a Direct Message.**\nTo unverify your Discord user from a Minecraft user, send a DM to this bot in the format:\n`" + commandTemplate() + "`"
    );
  }

  @Override
  public void onPrivateMessage(PrivateMessageReceivedEvent event, String[] args) {
    if(args.length > 0) {
      sendMessage(event, "**This command doesn't accept arguments.**\nTo use it, simply type `" + commandTemplate() + "`");
      return;
    }

    if(Unverify.unverifyDiscordUser(event.getAuthor().getId())) {
      // Was able to unverify this user
      sendMessage(event, "**Done!**\nYou are now unverified.");
    } else {
      // This user wasn't verified
      sendMessage(event, "**You are not verified to a Minecraft user.**");
    }
  }
}
