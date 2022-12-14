package de.tobias.joinmeplus.listener;

import de.tobias.joinmeplus.Utils;
import de.tobias.joinmeplus.database.Database;
import de.tobias.joinmeplus.files.MessagesConfig;
import de.tobias.joinmeplus.database.OwnableUserList;
import de.tobias.joinmeplus.database.UserSettings;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.ArrayList;
import java.util.UUID;

public class LogonListener implements Listener {

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer p = event.getPlayer();
        Database.registerUser(p);

        UserSettings settings = new UserSettings(p.getUniqueId());
        if(settings.load() == null) {
            Utils.sendMessage(p, MessagesConfig.chatPrefix + "Preparing your Database...");
            settings.listEnabled = false;
            settings.selectedList = UUID.randomUUID().toString();
            settings.notificationsEnabled = true;
            if(!settings.save()) {
                Utils.sendMessage(p, MessagesConfig.chatPrefix + "§cSomething went wrong (FAILED_SETTINGS_SAVE)");
                return;
            }

            OwnableUserList list = new OwnableUserList(settings.selectedList);
            list.owner = p.getUniqueId();
            list.name = "default";
            list.members = new ArrayList<>();
            if(!list.save()) {
                Utils.sendMessage(p, MessagesConfig.chatPrefix + "§cSomething went wrong (FAILED_LIST_SAVE)");
                return;
            }

            Utils.sendMessage(p, MessagesConfig.chatPrefix + "Your Database is now ready!");
        }
    }
}
