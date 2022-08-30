package de.tobias.joinmeplus;

import de.tobias.joinmeplus.commands.JoinmeCMD;
import de.tobias.joinmeplus.database.Database;
import de.tobias.joinmeplus.files.DatabaseConfig;
import de.tobias.joinmeplus.files.FilterConfig;
import de.tobias.joinmeplus.files.MessagesConfig;
import de.tobias.joinmeplus.listener.LogonListener;
import de.tobias.mcutils.AutoUpdater;
import de.tobias.mcutils.BungeeLogger;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;

public class main extends Plugin {

    public static String loggerPrefix = "§7[§5§lJoinMe+§7]%ADDITION% §7";
    public static Database db;
    public static Plugin pl;


    public static DatabaseConfig dbCFG;
    public static MessagesConfig messagesCFG;
    public static FilterConfig filterCFG;

    @Override
    public void onEnable() {
        pl = this;
        dbCFG = new DatabaseConfig();
        messagesCFG = new MessagesConfig();
        filterCFG = new FilterConfig();
        BungeeLogger.info("Loading Plugin...");
        messagesCFG.doAll();

        if(new File(this.getDataFolder(), "debug.activate").exists()) BungeeLogger.shouldDebug = true;

        dbCFG.doAll();
        filterCFG.doAll();
        if(DatabaseConfig.USE_MYSQL) {
            db = new Database(DatabaseConfig.HOST, DatabaseConfig.PORT.toString(), DatabaseConfig.USERNAME, DatabaseConfig.PASSWORD, DatabaseConfig.DATABASE);
        } else {
            db = new Database(new File(this.getDataFolder(), "db.sqlite"));
        }
        if(!db.connect()) {
            BungeeLogger.error("HALTED! Database connection is required!");
            return;
        }
        db.prepareTables();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new JoinmeCMD("joinme"));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new LogonListener());
        BungeeLogger.info("§aPlugin has been loaded!");
        AutoUpdater.checkForUpdateAndUpdate();
    }


    @Override
    public void onDisable() {
        db.disconnect();
    }
}
