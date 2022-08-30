package de.tobias.joinmeplus.database;

import de.tobias.mcutils.BungeeLogger;
import de.tobias.joinmeplus.Utils;
import de.tobias.joinmeplus.main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.UUID;

public class Database {

    File dbFile;
    Boolean isMySQL = false;

    String MYSQL_HOST;
    String MYSQL_PORT;
    String MYSQL_USERNAME;
    String MYSQL_PASSWORD;
    String MYSQL_DB;

    Connection conn = null;

    public Database(String host, String port, String username, String password, String database) {
        isMySQL = true;
        MYSQL_HOST = host;
        MYSQL_PORT = port;
        MYSQL_USERNAME = username;
        MYSQL_PASSWORD = password;
        MYSQL_DB = database;
    }

    public Database(File f) {
        isMySQL = false;
        this.dbFile = f;
    }

    public boolean connect() {
        BungeeLogger.info("Connecting to database...");
        try {
            if(!isMySQL) {
                if(!dbFile.getParentFile().exists()) {
                    dbFile.getParentFile().mkdirs();
                    BungeeLogger.warn("Created empty Plugin directory!");
                }
                Class.forName("org.sqlite.JDBC");
                String url = "jdbc:sqlite:" + dbFile.getPath();
                conn = DriverManager.getConnection(url);
                BungeeLogger.info("Now connected to SQLite database at: " + dbFile.getAbsolutePath());
                return true;
            } else {
                Class.forName("com.mysql.cj.jdbc.Driver");
                conn = DriverManager.getConnection("jdbc:mysql://" + MYSQL_HOST + "/" + MYSQL_DB + "?" + "user=" + MYSQL_USERNAME + "&password=" + MYSQL_PASSWORD);
                BungeeLogger.info("Now connected to MySQL at: " + MYSQL_HOST);
                return true;
            }
        } catch (Exception ex) {
            BungeeLogger.error("Failed to connect to database: ");
            ex.printStackTrace();
            return false;
        }

    }

    public void disconnect() {
        try {
            if(conn != null && !conn.isClosed()) {
                BungeeLogger.info("Disconnecting from database...");
                conn.close();
                BungeeLogger.info("Database is now closed");
            }
        } catch (Exception ex) {
            BungeeLogger.error("Failed to disconnect from database: ");
            ex.printStackTrace();
        }
    }

    public void prepareTables() {
        execute("CREATE TABLE IF NOT EXISTS `userSettings` (`UUID` TEXT NOT NULL , `listEnabled` BOOLEAN NOT NULL , `notifications` BOOLEAN NOT NULL , `selectedList` TEXT NOT NULL )");
        execute("CREATE TABLE IF NOT EXISTS `lists` (`ID` TEXT NOT NULL, `NAME` TEXT NOT NULL, `OWNER` TEXT NOT NULL , `USERS` TEXT NOT NULL )");
        execute("CREATE TABLE IF NOT EXISTS `userlog` (`UUID` TEXT NOT NULL, `NAME` TEXT NOT NULL, `JOINED` BIGINT NOT NULL )");
        BungeeLogger.info("Database tables are now ready");
    }

    public String format(String sql) {
        if(isMySQL) {
            sql = sql.replaceAll("`userSettings`", "`" + MYSQL_DB + "`.`userSettings`");
            sql = sql.replaceAll("`lists`", "`" + MYSQL_DB + "`.`lists`");
            sql = sql.replaceAll("`userlog`", "`" + MYSQL_DB + "`.`userlog`");
        }
        return sql;
    }
    public boolean execute(String sql) {
        sql = format(sql);
        BungeeLogger.debug(sql);
        try {
            PreparedStatement exec = conn.prepareStatement(sql);
            exec.execute();
            return true;
        } catch (Exception ex) {
            BungeeLogger.error("Failed database execute: ");
            ex.printStackTrace();
            return false;
        }
    }

    public ResultSet query(String sql) {
        sql = format(sql);
        BungeeLogger.debug(sql);
        try {
            PreparedStatement exec = conn.prepareStatement(sql);
            return exec.executeQuery();
        } catch (Exception ex) {
            BungeeLogger.error("Failed database query: ");
            ex.printStackTrace();
            return null;
        }
    }

    public static HashMap<UUID, String> uuidNameCache = new HashMap<>();
    public static void registerUser(ProxiedPlayer p) {
        if(uuidNameCache.containsKey(p.getUniqueId()) && uuidNameCache.get(p.getUniqueId()).equalsIgnoreCase(p.getName().toLowerCase())) return;
        try {
            ResultSet rs = main.db.query("SELECT * FROM `userlog` where `UUID` = '" + p.getUniqueId() + "'");
            if(rs.isClosed()) {
                main.db.execute("INSERT INTO `userlog` (`UUID`, `NAME`, `JOINED`) VALUES ('" + p.getUniqueId().toString() + "','" + p.getName() + "'," + System.currentTimeMillis() + ") ");
            } else {
                rs.next();
                if(!rs.getString("NAME").equalsIgnoreCase(p.getName())) {
                    main.db.execute("DELETE FROM `userlog` where `UUID` = '" + p.getUniqueId() + "'");
                    registerUser(p);
                }
            }
        } catch (Exception ex) {
            BungeeLogger.error("Failed to log player:");
            ex.printStackTrace();
        }
    }

    public static String uuidToName(String uuid) {
        if(uuidNameCache.containsKey(UUID.fromString(uuid))) {
            return uuidNameCache.get(UUID.fromString(uuid));
        }

        try {
            ResultSet rs = main.db.query("SELECT * FROM `userlog` where `UUID` = '" + uuid + "'");
            if(rs.isClosed()) return null;
            while(rs.next()) {
                String res = rs.getString("NAME");
                uuidNameCache.put(UUID.fromString(uuid), res.toLowerCase());
                return res;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    public static UUID nameToUUID(String name) {
        if(uuidNameCache.containsValue(name.toLowerCase())) {
            return Utils.getKeyByValue(uuidNameCache, name.toLowerCase());
        }

        try {
            ResultSet rs = main.db.query("SELECT * FROM `userlog` where LOWER(`NAME`) = '" + name.toLowerCase() + "'");
            if(rs.isClosed()) return null;
            while(rs.next()) {
                UUID res = UUID.fromString(rs.getString("NAME"));
                if(res != null) uuidNameCache.put(res, name.toLowerCase());
                return res;
            }
            return null;
        } catch (Exception ex) {
            return null;
        }
    }
}
