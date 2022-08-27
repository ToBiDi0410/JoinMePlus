package de.tobias.joinme.files;
import de.tobias.joinme.Logger;
import de.tobias.joinme.database.Messages;
import de.tobias.joinme.main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;

public class MessagesSerializer {

    public static Configuration configuration;
    public static File configFile = new File(main.pl.getDataFolder(), "messages.yml");

    public static void loadConfig() {
        Logger.info("Loading messages.yml ...");
        if(!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
        try {
            if(!configFile.exists()) configFile.createNewFile();
        } catch (Exception ex) {
            Logger.error("Failed to load messages.yml: ");
            ex.printStackTrace();
            return;
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            Field[] attributes = Messages.class.getDeclaredFields();

            for(String key : configuration.getKeys()) {
                for(Field field : attributes) {
                    if(field.getName().equalsIgnoreCase(key)) {
                        field.set(null, configuration.getString(key));
                    }
                }
            }
        } catch (Exception ex) {
            Logger.error("Failed to load value from messages.yml: ");
            ex.printStackTrace();
            return;
        }

        Logger.info("Loaded messages.yml!");
    }

    public static void saveAllFields() {
        Field[] attributes = Messages.class.getDeclaredFields();
        for(Field field : attributes) {
            if(field.getType() == String.class) {
                try {
                    configuration.set(field.getName(), field.get(null));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (Exception ex) {
            Logger.error("Failed to save messages.yml: ");
            ex.printStackTrace();
        }
    }
}
