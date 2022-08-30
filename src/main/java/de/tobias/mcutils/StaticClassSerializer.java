package de.tobias.mcutils;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public class StaticClassSerializer {

    Class toSerialize;
    public Configuration configuration;
    public File configFile;

    public StaticClassSerializer(Class c, File f) {
        toSerialize = c;
        configFile = f;
    }

    public boolean loadConfig() {
        BungeeLogger.debug("[" + configFile.getName() + "] Loading data...");
        if(!configFile.getParentFile().exists()) configFile.getParentFile().mkdirs();
        try {
            if(!configFile.exists()) configFile.createNewFile();
        } catch (Exception ex) {
            BungeeLogger.error("[" + configFile.getName() + "] Failed to create file!");
            ex.printStackTrace();
            return false;
        }

        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
            Field[] attributes = toSerialize.getDeclaredFields();

            for(String key : configuration.getKeys()) {
                for(Field field : attributes) {
                    if(field.getName().equalsIgnoreCase(key)) {
                        if(field.getType() == String.class) {
                            field.set(null, configuration.getString(key));
                        } else if (field.getType() == Integer.class) {
                            field.set(null, configuration.getInt(key));
                        } else if (field.getType() == Boolean.class) {
                            field.set(null, configuration.getBoolean(key));
                        } else if (field.getType() == ArrayList.class) {
                            field.set(null, new ArrayList<>(configuration.getList(key)));
                        } else {
                            BungeeLogger.warn("[" + configFile.getName() + "] Field cannot be loaded: " + field.getType().toString() + " (UNKNOWN TYPE)");
                        }
                    }
                }
            }
        } catch (Exception ex) {
            BungeeLogger.error("[" + configFile.getName() + "] Failed to load value from config!");
            ex.printStackTrace();
            return false;
        }

        BungeeLogger.info("["  + configFile.getName() + "]" + " File loaded!");
        return true;
    }

    public void saveAllFields() {
        Field[] attributes = toSerialize.getDeclaredFields();
        for(Field field : attributes) {
            if(Modifier.isPublic(field.getModifiers())) {
                if(field.getType() == String.class || field.getType() == Integer.class || field.getType() == Boolean.class || field.getType() == ArrayList.class) {
                    try {
                        configuration.set(field.getName(), field.get(null));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    BungeeLogger.debug("["  + configFile.getName() + "] Ignoring Field: " + field.getName() + " (" + field.getType() + ")");
                }
            } else {
                BungeeLogger.debug("["  + configFile.getName() + "] Ignoring Field: " + field.getName() + " (" + (Modifier.isPublic(field.getModifiers()) ? "PUBLIC" : (Modifier.isProtected(field.getModifiers()) ? "PROTECTED" : "PRIVATE")) + ")");
            }
        }
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, configFile);
        } catch (Exception ex) {
            BungeeLogger.error("["  + configFile.getName() + "] Failed to save file!");
            ex.printStackTrace();
        }
    }

    public void doAll() {
        loadConfig();
        saveAllFields();
    }
}
