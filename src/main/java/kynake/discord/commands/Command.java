package kynake.discord.commands;

// JDA
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

public interface Command {
  public default String register() {
    return getCommandString();
  };

  public void onGuildMessage(GuildMessageReceivedEvent event, String[] args);
  public void onPrivateMessage(PrivateMessageReceivedEvent event, String[] args);

  public String getCommandString();
}