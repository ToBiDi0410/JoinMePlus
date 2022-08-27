package de.tobias.joinmeplus.files;

import de.tobias.joinmeplus.Logger;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SimpleConfig {

    File file;
    public Configuration cfg;
    public HashMap<String, Object> defaults;

    public SimpleConfig(File path) {
        this.file = path;
    }

    public void prepare() {}

    public boolean read() {
        Logger.info("Loading '" + file.getName() + "' ...");
        if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
        try {
            if(!file.exists()) file.createNewFile();
        } catch (Exception ex) {
            Logger.error("Failed to load '" + file.getName() + "': ");
            ex.printStackTrace();
            return false;
        }

        try {
            cfg = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (Exception ex) {
            Logger.error("Failed to load '" + file.getName() + "': ");
            ex.printStackTrace();
            return false;
        }

        Logger.info("Loaded '" + file.getName() + "'!");
        return true;
    }

    public void addDefault(String key, Object value) {
        defaults.put(key, value);
    }

    public void addDefaults(HashMap<String, Object> map) {
        defaults.putAll(map);
    }

    public boolean ensureDefaults() {
        for(Map.Entry<String, Object> entry : defaults.entrySet()) {
            Boolean somethingAdded = false;
            if(!cfg.contains(entry.getKey())) {
                cfg.set(entry.getKey(), entry.getValue());
                somethingAdded = true;
            }

            if(somethingAdded) return save();
            else return true;
        }
        return false;
    }

    public boolean save() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(cfg, file);
            Logger.info("Saved '" + file.getName() + "'!");
            return true;
        } catch (Exception ex) {
            Logger.error("Failed to save '" + file.getName() + "': ");
            ex.printStackTrace();
        }
        return false;
    }
}
