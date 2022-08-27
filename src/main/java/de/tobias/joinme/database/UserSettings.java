package de.tobias.joinme.database;

import de.tobias.joinme.main;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

public class UserSettings {

    public static ArrayList<UserSettings> cache = new ArrayList();
    UUID uuid = null;
    public Boolean listEnabled = false;
    public String selectedList = null;
    public Boolean notificationsEnabled = true;

    public UserSettings(UUID uuid) {
        this.uuid = uuid;
    }

    public UserSettings load() {
        for(UserSettings settings : cache) {
            if(settings.uuid.toString().equalsIgnoreCase(uuid.toString())) return settings;
        }

        ResultSet rs = main.db.query("SELECT * FROM `userSettings` WHERE `UUID` = '" + uuid + "'");
        try {
            while(rs.next()) {
                listEnabled = rs.getBoolean("listEnabled");
                notificationsEnabled = rs.getBoolean("notifications");
                selectedList = rs.getString("selectedList");
                cache.add(this);
                return this;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public OwnableUserList getCurrentList() {
        OwnableUserList list = new OwnableUserList(selectedList);
        list = list.load();
        return list;
    }

    public Boolean setCurrentList(OwnableUserList list) {
        list.save();
        selectedList = list.id;
        return save();
    }

    public boolean save() {
        if(!main.db.execute("DELETE FROM `userSettings` WHERE UUID = '" + uuid + "'")) return false;
        return main.db.execute("INSERT INTO `userSettings` (`UUID`, `listEnabled`, `notifications`, `selectedList`) VALUES ('" + uuid + "'," + listEnabled + "," + notificationsEnabled + ",'" + selectedList + "')");
    }
}
