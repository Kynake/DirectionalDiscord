package com.kynake.minecraft.directionablediscord.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
// Google
import com.google.gson.stream.JsonReader;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class Config {
  // Static Fields
  private static final Logger LOGGER = LogManager.getLogger();

  private static final String configPath = "discord-voice.jsonc";
  private static final String templatePath = "/assets/directionablediscord/dd-config-template.jsonc";

  private static Config instance = null;

  public static String getToken() {
    return instance == null? null : instance.discord_token;
  }

  public static String getServerID() {
    return instance == null? null : instance.discord_server_id;
  }

  public static String getVoiceChannelID() {
    return instance == null? null : instance.discord_voice_channel_id;
  }

  public static Map<String, String> getVerifiedUsers() {
    return instance == null? null : instance.verified_users;
  }

  // public void setVerifiedUsers(Map<String, String> verifiedUsers) {
  //   verified_users = verifiedUsers;
  // }

  public Map<String, String> getVerified_users() {
    return verified_users;
  }

  public static void acquireConfigs() throws IOException {
    File config = new File(configPath);
    if (config.isDirectory()) {
      LOGGER.error(configPath + "Exists and is a Folder!");
    } else if (config.exists()) {
      Gson gson = new GsonBuilder().setLenient().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

      try {
        instance = gson.fromJson(new FileReader(config), Config.class);
      } catch (FileNotFoundException e) {
        LOGGER.fatal("Cannot read config file!");
        e.printStackTrace();
        throw new IOException("Cannot read config file!");
      }
    } else {
      copyTemplate();
    }
  }

  public static void copyTemplate() {
    try {
      InputStream input = Config.class.getResourceAsStream(templatePath);
      Files.copy(input, Paths.get(configPath));
    } catch (IOException e) {
      LOGGER.error("Cannot create config file!");
      e.printStackTrace();
    }
  }

  public static void addVerifiedUser(String uuid, String discordID) {

  }

  // Object Fields
  public String discord_token;
  public String discord_server_id;
  public String discord_voice_channel_id;
  public Map<String, String> verified_users;

  private Config() { }
}
