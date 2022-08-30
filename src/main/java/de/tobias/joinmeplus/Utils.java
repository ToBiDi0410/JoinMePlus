package de.tobias.joinmeplus;

import de.tobias.joinmeplus.files.FilterConfig;
import de.tobias.mcutils.BungeeLogger;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Utils {

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static boolean isValidServer(ServerInfo inf) {
        boolean whitelistedServer = false;
        boolean blacklistedServer = false;
        boolean whitelistedMOTD = false;
        boolean blacklistedMOTD = false;

        for(String s : FilterConfig.whitelistedServers) {
            if(matchPattern(s, inf.getName())) whitelistedServer = true;
        }

        for(String s : FilterConfig.blacklistedServers) {
            if(matchPattern(s, inf.getName())) blacklistedServer = true;
        }

        for(String s : FilterConfig.whitelistedMOTDs) {
            if(matchPattern(s, inf.getMotd())) whitelistedMOTD = true;
        }

        for(String s : FilterConfig.blacklistedMOTDs) {
            if(matchPattern(s, inf.getMotd())) blacklistedMOTD = true;
        }

        BungeeLogger.debug(inf.getName() + " --> " + "SW: " + whitelistedServer + "; SB: " + blacklistedServer + "; WM: " + whitelistedMOTD + "; BM: " + blacklistedMOTD);
        if(!whitelistedServer && !whitelistedMOTD) return false;
        if(blacklistedServer || blacklistedMOTD) return false;
        return true;
    }

    //FROM: https://stackoverflow.com/questions/24337657/wildcard-matching-in-java
    public static boolean matchPattern(String pattern, String str) {
        if(pattern.equalsIgnoreCase("*")) return true;
        return str.toLowerCase().contains(pattern.toLowerCase());
    }

    public static void sendMessage(CommandSender p, String msg) {
        p.sendMessage(TextComponent.fromLegacyText(msg));
    }
}
