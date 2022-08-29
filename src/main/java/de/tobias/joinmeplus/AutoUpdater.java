package de.tobias.joinmeplus;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.*;
import java.lang.module.ModuleDescriptor;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

public class AutoUpdater {

    public static String PREFIX = main.loggerPrefix.replace("%ADDITION%", "") + "§7[§aAutoUpdater§7] §7";
    public static Plugin pl = main.pl;
    public static String FILE_URL = "https://github.com/ToBiDi0410/JoinMePlus/raw/build/target/joinmeplus-1.0-jar-with-dependencies.jar";
    public static String YML_URL = "https://raw.githubusercontent.com/ToBiDi0410/JoinMePlus/build/src/main/resources/bungee.yml";
    public static File updaterDir = new File(pl.getDataFolder(), "updater");
    public static String getCurrentVersion() {
        try {
            String ymlContent = getYMLString();
            int versionStart = ymlContent.indexOf("version");
            String versionLine = ymlContent.substring(versionStart);
            String version = versionLine.replace("version: ", "").split("\n")[0];
            return version;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "NULL";
        }
    }

    public static String getYMLString() throws Exception {
        URL url = new URL(YML_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine + System.getProperty("line.separator"));
        }
        in.close();
        con.disconnect();
        return content.toString();
    }

    public static void checkForUpdateAndUpdate() {
        if(!updaterDir.exists()) updaterDir.mkdirs();

        ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§7Searching updates...");
        ModuleDescriptor.Version latest = ModuleDescriptor.Version.parse(getCurrentVersion().trim());
        ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§7Latest version is §b" + latest.toString());
        ModuleDescriptor.Version installed = ModuleDescriptor.Version.parse(pl.getDescription().getVersion());

        if(latest.compareTo(installed) == 1) {
            ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§7Performing upgrade §7(§c" + installed + " §7--> §a" + latest.toString() + "§7)...");
            if(downloadNewJAR()) {
                if(overwriteOldJAR()) {
                    ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§aPlugin has been updated!");
                } else {
                    ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§4Failed to update Plugin");
                }
            } else {
                ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§4Failed to update Plugin");
            }
        }
    }

    public static Boolean downloadNewJAR() {
        try {
            URL website = new URL(FILE_URL);
            ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§7Downloading file from '§6" + website.toString() + "§7'...");
            File newFile = new File(updaterDir, "update.jar");
            Files.copy(website.openStream(), Paths.get(newFile.toURI()), StandardCopyOption.REPLACE_EXISTING);
            ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§7Download complete!");
            return true;
        } catch (Exception ex) {
            ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§cError while downloading update file :");
            ex.printStackTrace();
            return false;
        }
    }

    public static Boolean overwriteOldJAR() {
        try {
            ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§7Overwriting old file (DO NOT ABORT!)...");
            File newJAR = new File(updaterDir, "update.jar");
            String filePath = pl.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            File oldJAR = new File(filePath);

            InputStream in = new BufferedInputStream(new FileInputStream(newJAR));
            writeBytesFromInputStreamIntoFile(in, oldJAR);

            ProxyServer.getInstance().getScheduler().schedule(pl, () -> {
                ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§4Update installed! Please restart!!!");
            }, 1, 60, TimeUnit.SECONDS);
            return true;
        } catch (Exception ex) {
            ProxyServer.getInstance().getConsole().sendMessage(PREFIX + "§cError while applying update file :");
            ex.printStackTrace();
            return false;
        }
    }

    public static boolean writeBytesFromInputStreamIntoFile(InputStream in, File f) {
        try {
            OutputStream outStream = new FileOutputStream(f);
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
            in.close();
            outStream.close();
            return true;
        } catch(Exception ex) {
            ex.printStackTrace();
            return false;
        }

    }
}
