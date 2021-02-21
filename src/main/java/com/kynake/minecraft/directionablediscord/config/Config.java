package com.kynake.minecraft.directionablediscord.config;

// Google
import com.google.gson.stream.JsonReader;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Config {
  public static class VerifiedUser {
    public String NAME;
    public String MINECRAFT_UUID;
    public String DISCORD_ID;
  }

  private static final String configPath = "discord-voice.jsonc";
  private static final String templatePath = "/assets/directionablediscord/dd-config-template.jsonc";

  private static final Logger LOGGER = LogManager.getLogger();

  // Config values
  public static boolean isConfigured = false;
  public static String DISCORD_TOKEN;
  public static String DISCORD_GUILD_ID;
  public static String DISCORD_VOICE_CHANNEL_ID;
  public static ArrayList<VerifiedUser> VERIFIED_USERS;

  public static void acquireConfigs() {
    File config = new File(configPath);
    if(config.exists()) {
      if(config.isDirectory()) {
        LOGGER.error(configPath + "Exists and is a Folder!");
        isConfigured = false;
      } else {
        acquireFromFile(config);
      }
    } else {
      createFromTemplate();
    }
  }

  public static void addVerifiedUser(VerifiedUser user) {

  }

  private static void acquireFromFile(File configFile) {
    try {
      InputStream stream = new FileInputStream(configFile);
      populateAttributes(stream);
      isConfigured = true;
    } catch(FileNotFoundException e) { // Should never happen
      e.printStackTrace();
      isConfigured = false;
    }
  }

  private static void createFromTemplate() {
    try {
      InputStream inputStream = Config.class.getResourceAsStream(templatePath);
      FileOutputStream newConfig = new FileOutputStream(configPath, false);
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        newConfig.write(buffer, 0, bytesRead);
      }

      inputStream.close();
      newConfig.close();

    } catch(IOException e) {
      LOGGER.fatal("Cannot create/write config file!");
      e.printStackTrace();

    } finally {
      isConfigured = false;
    }

  }

  private static void populateAttributes(InputStream jsonStream) {
    JsonReader reader = new JsonReader(new InputStreamReader(jsonStream));
    reader.setLenient(true); // Allow comments in the JSON file
    try {
      reader.beginObject();
      while(reader.hasNext()) { // Root object
        switch(reader.nextName()) {
          case "discord_token":
            DISCORD_TOKEN = reader.nextString();
            break;

          case "discord_server_id":
            DISCORD_GUILD_ID = reader.nextString();
            break;

          case "discord_voice_channel_id":
            DISCORD_VOICE_CHANNEL_ID = reader.nextString();
            break;

          case "verified_users":
            VERIFIED_USERS = new ArrayList<>();

            reader.beginArray();
            while(reader.hasNext()) { // User array
              reader.beginObject();
              VerifiedUser user = new VerifiedUser();

              while(reader.hasNext()) { // User object
                switch(reader.nextName()) {
                  case "name":
                    user.NAME = reader.nextString();
                    break;

                  case "uuid":
                    user.MINECRAFT_UUID = reader.nextString();
                    break;

                  case "discord_id":
                    user.DISCORD_ID = reader.nextString();
                    break;
                }
              }

              VERIFIED_USERS.add(user);
              reader.endObject();
            }
            reader.endArray();
            break;
        }
      }
    } catch(IOException e) {
      LOGGER.error("Error reading config file!");
      isConfigured = false;
    }

  }
}
