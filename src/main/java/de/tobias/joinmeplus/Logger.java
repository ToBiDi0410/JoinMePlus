package de.tobias.joinmeplus;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public class Logger {

    public static String loggerPrefix = main.loggerPrefix;
    public static Boolean shouldDebug = false;

    public static void info(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(loggerPrefix.replace("%ADDITION%", " §a[INFO]") + msg));
    }

    public static void warn(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(loggerPrefix.replace("%ADDITION%", " §6[WARN]") + msg));
    }

    public static void error(String msg) {
        ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(loggerPrefix.replace("%ADDITION%", " §c[ERROR]") + msg));
    }

    public static void debug(String msg) {
        if(shouldDebug) {
            ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(loggerPrefix.replace("%ADDITION%", " §5[DEBUG]") + msg));
        }
    }
}
