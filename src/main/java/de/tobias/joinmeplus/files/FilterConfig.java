package de.tobias.joinmeplus.files;

import de.tobias.joinmeplus.main;
import de.tobias.mcutils.StaticClassSerializer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilterConfig extends StaticClassSerializer {

    public static String IGNORE = "Currently only matches if Name/MOTD contains string. NO wildcards!";
    public static ArrayList<String> whitelistedServers = new ArrayList<>(List.of("*"));
    public static ArrayList<String> blacklistedServers = new ArrayList<>(List.of("lobby"));;

    public static ArrayList<String> blacklistedMOTDs = new ArrayList<>(List.of("*ingame*", "restart"));;
    public static ArrayList<String> whitelistedMOTDs = new ArrayList<>(List.of("*"));

    public FilterConfig() {
        super(FilterConfig.class, new File(main.pl.getDataFolder(), "filter.yml"));
    }
}
