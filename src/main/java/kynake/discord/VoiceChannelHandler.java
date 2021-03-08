package kynake.discord;

// JDA
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

// Java
import java.util.ArrayList;
import java.util.List;

public class VoiceChannelHandler extends ListenerAdapter {
  private List<String> deafenedUsers = new ArrayList<>();

  // Events
  @Override
  public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
    userJoinedVoiceChannel((GuildVoiceUpdateEvent) event);
  }


  @Override
  public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
    userLeftVoiceChannel((GuildVoiceUpdateEvent) event);
  }

  @Override
  public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
    userLeftVoiceChannel((GuildVoiceUpdateEvent) event);
    userJoinedVoiceChannel((GuildVoiceUpdateEvent) event);
  }

  private void userJoinedVoiceChannel(GuildVoiceUpdateEvent event) {
    if(event.getEntity().getIdLong() == ListeningBot.getSelfID()) {
      botJoinVoiceChannel(event);
      return;
    }

    Member user = event.getEntity();
    if(event.getChannelJoined().getIdLong() == ListeningBot.getVoiceChannel().getIdLong()) {
      user.deafen(true).queue();
    } else if(deafenedUsers.contains(user.getId())) {
      // User rejoined guild after leaving while deafened by the bot
      user.deafen(false).queue();
    }


    deafenedUsers.remove(user.getId());
  }

  private void userLeftVoiceChannel(GuildVoiceUpdateEvent event) {
    if(event.getChannelLeft().getIdLong() == ListeningBot.getVoiceChannel().getIdLong()) {
      try {
        event.getEntity().deafen(false).queue();
      } catch(IllegalStateException e) {
        // User left the guild, rather than move channels

        deafenedUsers.add(event.getEntity().getId());
      }
    }
  }

  private void botJoinVoiceChannel(GuildVoiceUpdateEvent event) {
    event.getEntity().mute(true).queue();
    event.getEntity().deafen(false).queue();
  }
}
