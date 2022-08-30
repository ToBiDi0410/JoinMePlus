package de.tobias.joinmeplus.files;

import de.tobias.joinmeplus.main;
import de.tobias.mcutils.StaticClassSerializer;

import java.io.File;

public class DatabaseConfig extends StaticClassSerializer {

    public static Boolean USE_MYSQL = false;
    public static String HOST = "192.168.178.2";
    public static Integer PORT = 3306;
    public static String USERNAME = "joinmeplus";
    public static String PASSWORD = "why_1s_this_s0_fr1ck1ng_secure";
    public static String DATABASE = "joinmeplus";

    public DatabaseConfig() {
        super(DatabaseConfig.class, new File(main.pl.getDataFolder(), "database.yml"));
    }

}
