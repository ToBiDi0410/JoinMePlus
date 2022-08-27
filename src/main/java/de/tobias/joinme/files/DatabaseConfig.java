package de.tobias.joinme.files;

import de.tobias.joinme.Logger;
import de.tobias.joinme.main;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DatabaseConfig {

    public static Configuration configuration;
    public static File configFile = new File(main.pl.getDataFolder(), "mysql.yml");
    public static HashMap<String, String> defaultValues = new HashMap<>();

    public static boolean read() {
        defaultValues.put("USE_MYSQL", "false");
        defaultValues.put("HOST", "192.168.178.2");
        defaultValues.put("PORT", "3306");
        defaultValues.put("USERNAME", "joinmeplus");
        defaultValues.put("PASSWORD", "why_1s_this_s0_fr1ck1ng_secure");
        defaultValues.put("DATABASE", "joinmeplus");

        Logger.info("Loading mysql.yml ...");
        if(!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
        try {
            if(!configFile.exists()) configFile.createNewFile();
        } catch (Exception ex) {
            Logger.error("Failed to load mysql.yml: ");
            ex.printStackTrace();
            return false;
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            for(Map.Entry<String, String> defaultField : defaultValues.entrySet()) {
                if(!configuration.getKeys().contains(defaultField.getKey())) {
                    configuration.set(defaultField.getKey(), defaultField.getValue());
                }
            }
            save();
        } catch (Exception ex) {
            Logger.error("Failed to load value from mysql.yml: ");
            ex.printStackTrace();
            return false;
        }

        Logger.info("Loaded mysql.yml!");
        return true;
    }

    public static void save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (Exception ex) {
            Logger.error("Failed to save mysql.yml: ");
            ex.printStackTrace();
        }
    }
}
