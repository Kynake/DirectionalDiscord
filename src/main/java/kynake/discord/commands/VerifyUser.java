package kynake.discord.commands;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import kynake.discord.CommandHandler;
// JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public class VerifyUser implements Command {
  public static final Logger LOGGER = LogManager.getLogger();

  private static final String commandString = "verify";

  public String getCommandString() {
    return commandString;
  }

  @Override
  public void onGuildMessage(GuildMessageReceivedEvent event, String[] args) {
    event.getChannel().sendMessage(
      "**This command should be sent as a Direct Message.**\nTo verify your Discord user against a Minecraft user, send a DM to this bot in the format:\n`"
      + CommandHandler.prefix + getCommandString() + " <Minecraft_Username>`, and follow the instructions given."
    ).queue();
  }

  public void onPrivateMessage(PrivateMessageReceivedEvent event, String[] args) {
    LOGGER.debug(commandString + " received private message from " + event.getAuthor().getName());
    LOGGER.debug(event);
    LOGGER.debug(args);
  };
}
