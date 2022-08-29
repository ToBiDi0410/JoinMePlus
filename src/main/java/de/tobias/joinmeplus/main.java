package de.tobias.joinmeplus;

import de.tobias.joinmeplus.database.Database;
import de.tobias.joinmeplus.files.DatabaseConfig;
import de.tobias.joinmeplus.files.MessagesSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class main extends Plugin {

    public static String loggerPrefix = "§7[§5§lJoinMe+§7]%ADDITION% §7";
    public static Database db;
    public static Plugin pl;

    @Override
    public void onEnable() {
        pl = this;
        Logger.info("Loading Plugin...");
        MessagesSerializer.loadConfig();
        MessagesSerializer.saveAllFields();

        if(new File(this.getDataFolder(), "debug.activate").exists()) Logger.shouldDebug = true;

        DatabaseConfig.read();

        if(DatabaseConfig.configuration.getBoolean("USE_MYSQL") == true || DatabaseConfig.configuration.getString("USE_MYSQL").equalsIgnoreCase("true")) {
            db = new Database(DatabaseConfig.configuration.getString("HOST"), DatabaseConfig.configuration.getString("PORT"), DatabaseConfig.configuration.getString("USERNAME"), DatabaseConfig.configuration.getString("PASSWORD"), DatabaseConfig.configuration.getString("DATABASE"));
        } else {
            db = new Database(new File(this.getDataFolder(), "db.sqlite"));
        }
        db.connect();
        db.prepareTables();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new JoinmeCMD("joinme"));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new LogonListener());
        Logger.info("§aPlugin has been loaded!");
        AutoUpdater.checkForUpdateAndUpdate();
    }

    @Override
    public void onDisable() {
        db.disconnect();
    }
}
