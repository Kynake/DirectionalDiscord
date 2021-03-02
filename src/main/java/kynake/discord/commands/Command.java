package kynake.discord.commands;

import kynake.discord.CommandHandler;
// JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public interface Command {
  public void onGuildMessage(GuildMessageReceivedEvent event, String[] args);
  public void onPrivateMessage(PrivateMessageReceivedEvent event, String[] args);

  public String getCommandString();

  // Defaults
  public default String register() {
    return getCommandString();
  }

  public default String commandTemplate() {
    return CommandHandler.prefix + getCommandString();
  }

  default void sendMessage(GuildMessageReceivedEvent event, String message) {
    event.getChannel().sendMessage(message).queue();
  }

  default void sendMessage(PrivateMessageReceivedEvent event, String message) {
    event.getChannel().sendMessage(message).queue();
  }
}