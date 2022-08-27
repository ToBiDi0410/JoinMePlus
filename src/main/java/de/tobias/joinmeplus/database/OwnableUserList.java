package de.tobias.joinmeplus.database;

import de.tobias.joinmeplus.main;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

public class OwnableUserList {

    public static ArrayList<OwnableUserList> cache = new ArrayList();
    public UUID owner;
    String id;
    public String name;
    public ArrayList<String> members;

    public OwnableUserList(String id) {
        this.id = id;
    }

    public OwnableUserList load() {
        for(OwnableUserList list : cache) {
            if(list.id.equalsIgnoreCase(id)) return list;
        }

        ResultSet rs = main.db.query("SELECT * FROM `lists` WHERE ID = '" + id + "'");
        try {
            while(rs.next()) {
                id = rs.getString("ID");
                owner = UUID.fromString(rs.getString("OWNER"));
                name = rs.getString("NAME");

                String userCommas = rs.getString("USERS");
                members = new ArrayList<>(Arrays.asList(userCommas.split(",")));
                members = members.stream().filter((b) -> !b.equalsIgnoreCase("") && !b.equalsIgnoreCase(" ")).collect(Collectors.toCollection(ArrayList::new));
                cache.add(this);
                return this;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<String> getMemberNames() {
        ArrayList<String> names = new ArrayList<>();
        for(String uuids : members) {
            String name = Database.uuidToName(uuids);
            if(name != null) {
                names.add(name);
            }
        }
        return names;
    }

    public boolean save() {
        if(!main.db.execute("DELETE FROM `lists` WHERE ID = '" + id + "'")) return false;
        return main.db.execute("INSERT INTO `lists` (`ID`, `NAME`, `OWNER`, `USERS`) VALUES ('" + id + "','" + name + "','" + owner.toString() + "','" + String.join(",", members) + "')");
    }
}
