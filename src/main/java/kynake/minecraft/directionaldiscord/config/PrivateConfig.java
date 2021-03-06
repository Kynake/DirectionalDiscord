package kynake.minecraft.directionaldiscord.config;

// Google
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// Apache
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Java
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class PrivateConfig {
  // Static Fields
  private static final Logger LOGGER = LogManager.getLogger();

  private static final String configPath = "directional-discord-private-config.json";
  private static final String templatePath = "/assets/directionaldiscord/dd-config-template.json";

  private static PrivateConfig instance = null;

  public static String getToken() {
    return isConfigured()? instance.discord_token : null;
  }

  public static String getServerID() {
    return isConfigured()? instance.discord_server_id : null;
  }

  public static String getVoiceChannelID() {
    return isConfigured()? instance.discord_voice_channel_id : null;
  }

  /**
   *
   * @return A Map<String, String> of Discord User ID -> Minecraft UUID
   */
  public static Map<String, String> getVerifiedUsers() {
    return isConfigured()? instance.verified_users : null;
  }

  /**
   * Loads the the mod's configuration variables from the
   * discord-voice.jsonc config file,
   * or creates a template of it, if it doesn't already exist
   *
   * @throws IOException
   */
  public static void acquireConfigs() throws IOException {
    File config = new File(configPath);
    if (config.isDirectory()) {
      LOGGER.error(configPath + "Exists and is a Folder!");
    } else if (config.exists()) {
      Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();

      try {
        instance = gson.fromJson(new FileReader(config), PrivateConfig.class);
      } catch (FileNotFoundException e) {
        LOGGER.fatal("Cannot read config file!");
        e.printStackTrace();
        throw new IOException("Cannot read config file!");
      }
    } else {
      instance = null;
      copyTemplate();
    }
  }

  public static boolean isConfigured() {
    return instance != null;
  }

  public static void Unconfigure() {
    instance = null;
  }

  /**
   * Adds a new User to the list of verified users,
   * and attempts to sync those changes to the config file.
   *
   * @param discordID The UserID of the correspondig user on Discord
   * @param uuid The UUID of the Minecraft user to be added
   */
  public static void addVerifiedUser(String discordID, String uuid) {
    if(instance == null) {
      LOGGER.error("Cannot add verified user, config was not initialized!");
      return;
    }

    instance.verified_users.put(discordID, uuid);
    syncVerifiedUserConfigFile();
  }

  /**
   * Removes an existing User from the list of verified users
   *
   * @param discordID The UserID of the user on Discord to remove
   */
  public static void removeVerifiedUser(String discordID) {
    if(instance == null) {
      LOGGER.error("Cannot remove verified user, config was not initialized!");
      return;
    }

    instance.verified_users.remove(discordID);
    syncVerifiedUserConfigFile();
  }

  private static void copyTemplate() {
    try {
      InputStream input = PrivateConfig.class.getResourceAsStream(templatePath);
      Files.copy(input, Paths.get(configPath));
    } catch (IOException e) {
      LOGGER.error("Cannot create config file!");
      e.printStackTrace();
    }
  }

  private static void syncVerifiedUserConfigFile() {
    Gson gson = new GsonBuilder().setPrettyPrinting().setFieldNamingPolicy(FieldNamingPolicy.IDENTITY).create();
    String jsonOutput = gson.toJson(instance);

    try {
      InputStream input = new ByteArrayInputStream(jsonOutput.getBytes());
      Files.copy(input, Paths.get(configPath), StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      LOGGER.fatal("Cannot write config file!");
      e.printStackTrace();
    }

    // long[] offsets = findVerifiedUsersByteOffsets();
    // if(offsets == null) {
    //   return;
    // }

    // String userList = buildVerifiedUsersString();
    // spliceToFile(userList, offsets[0], offsets[1]);
  }

  // TODO: use a better file type (like .toml) to create a nice, detailed config with comments
  // For now just use a simple json file

  // private static long[] findVerifiedUsersByteOffsets() { // Find the start and ending bytes of the verified user's object in the config file
  //   File config = new File(configPath);
  //   if (!config.exists() || config.isDirectory()) {
  //     LOGGER.error("Could not sync config file to verified Users!");
  //     return null;
  //   }

  //   long startingByte = -1, endingByte = -1;

  //   try(JsonReader reader = new JsonReader(new FileReader(config))) {
  //     // To get the byte offsets from the Json object in the file
  //     // we need access to the 'pos' variable that JsonReader uses to keep track of where it is on the InputStream
  //     Field privatePosField = JsonReader.class.getDeclaredField("pos");
  //     privatePosField.setAccessible(true);

  //     reader.setLenient(true);
  //     reader.beginObject();
  //     while(reader.hasNext()) {
  //       if(!reader.nextName().equals("verified_users")) {
  //         reader.skipValue();
  //         continue;
  //       }

  //       // Verified Users Object
  //       reader.beginObject(); // Right after opening '{'
  //       startingByte = privatePosField.getInt(reader) - 1;
  //       while(reader.hasNext()) {
  //         reader.skipValue();
  //       }
  //       reader.endObject();
  //       endingByte = privatePosField.getInt(reader);
  //     }
  //   } catch(IOException e) {
  //     LOGGER.error("Error reading json from file");
  //     e.printStackTrace();
  //     return null;
  //   } catch(NoSuchFieldException | IllegalAccessException e) {
  //     LOGGER.error("Error accessing JsonReader 'pos' field");
  //     e.printStackTrace();
  //     return null;
  //   }

  //   long[] res = {startingByte, endingByte};
  //   return res;
  // }

  // private static String buildVerifiedUsersString() {
  //   // Rough estimate of how big the final string will be
  //   int builderSize = 66 * instance.verified_users.size() + 7;
  //   StringBuilder sb = new StringBuilder(builderSize);

  //   boolean isFirst = true;
  //   sb.append("{\n");
  //   for(Map.Entry<String, String> verifiedUser : instance.verified_users.entrySet()) {
  //     if(isFirst) {
  //       isFirst = false;
  //     } else {
  //       sb.append(",\n");
  //     }

  //     sb.append("    \"" + verifiedUser.getKey() + "\": \"" + verifiedUser.getValue() + "\"");
  //   }
  //   sb.append("\n  }");

  //   return sb.toString();
  // }

  // private static void spliceToFile(String data, long byteStartOffset, long byteEndOffset) { // Replace Verified user's object string in the config file without changing the rest of it
  //   File config = new File(configPath);
  //   if (!config.exists() || config.isDirectory()) {
  //     LOGGER.error("Could not open config file for rewriting!");
  //     return;
  //   }

  //   DataInputStream fis;
  //   FileOutputStream fos;
  //   try {
  //     fis = new DataInputStream(new BufferedInputStream(new FileInputStream(config)));
  //     List<Byte> bytes = new ArrayList<Byte>(fis.available());

  //     for(long i = 0; i < byteStartOffset; i++) {
  //       bytes.add(fis.readByte());
  //     }

  //     for(Byte stringByte : data.getBytes()) {
  //       bytes.add(stringByte);
  //     }

  //     fis.skip(byteEndOffset - byteStartOffset);

  //     try {
  //       while(true) {
  //         bytes.add(fis.readByte());
  //       }
  //     } catch(EOFException e) {}
  //     fis.close();

  //     fos = new FileOutputStream(config, false);
  //     for (Byte writeByte : bytes) {
  //       fos.write(writeByte);
  //     }
  //     fos.close();

  //   } catch(IOException e) {
  //     e.printStackTrace();
  //   }
  // }

  // Object Fields
  private String discord_token;
  private String discord_server_id;
  private String discord_voice_channel_id;

  // Map of Discord User ID -> Minecraft UUID
  private Map<String, String> verified_users;

  private PrivateConfig() { }
}
