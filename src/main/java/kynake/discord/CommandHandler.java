package kynake.discord;

// Internal
import kynake.discord.commands.*;

// JDA
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.HashMap;

public class CommandHandler extends ListenerAdapter {
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String prefix = "!";

  private Map<String, Command> commands;

  public Map<String, Command> getCommands() {
    return this.commands;
  }

  public CommandHandler() {
    commands = new HashMap<String, Command>();

    // Register Commands

    LOGGER.info("Created Commands: " + commands.keySet().stream().map(command -> prefix + command).collect(Collectors.joining(", ")));

    // Register self as event handler
    ListeningBot.jda.addEventListener(this);
  }

  private void createCommand(Class<? extends Command> commandClass) {
    try {
      Command commandObject = commandClass.newInstance();
      if(commands.putIfAbsent(commandObject.register(), commandObject) != null) {
        LOGGER.info(commandObject.getCommandString() + " has already been registered!");
        return;
      }

    } catch(IllegalAccessException | InstantiationException | ExceptionInInitializerError | SecurityException e) {
      LOGGER.info("Could not instantiate command " + commandClass.getName());
    }
  }

  // Events
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    String message = event.getMessage().getContentDisplay();

    // Only parse messages that start with the prefix
    if(message.length() == 0 || !message.startsWith(prefix)) {
      return;
    }

    String[] args = message.split("\\s+");
    Command command = commands.get(args[0].substring(prefix.length()).toLowerCase());
    if(command != null) {
      command.onGuildMessage(event, Arrays.copyOfRange(args, 1, args.length));
    }
  }

  public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
    String message = event.getMessage().getContentDisplay();

    // Only parse messages that start with the prefix
    if(message.length() == 0 || !message.startsWith(prefix)) {
      return;
    }

    String[] args = message.split("\\s+");
    Command command = commands.get(args[0].substring(prefix.length()).toLowerCase());
    if(command != null) {
      command.onPrivateMessage(event, Arrays.copyOfRange(args, 1, args.length));
    }

  }
}