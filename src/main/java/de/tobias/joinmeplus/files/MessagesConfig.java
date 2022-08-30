package de.tobias.joinmeplus.files;

import de.tobias.joinmeplus.main;
import de.tobias.mcutils.StaticClassSerializer;

import java.io.File;

public class MessagesConfig extends StaticClassSerializer {

    public static String CMD_NOCONSOLE = "§cThis command is only available for players";
    public static String CMD_NO_PERMISSION = "§cYou are not allowed to do that (%PERM%)";
    public static String CMD_INVALID_SYNTAX = "§cUnsupported Syntax (are you sure everything is spelled correctly?)";
    public static String CMD_REQUIRE_LIST_ENABLED = "§7You have to enable lists before that";
    public static String CMD_USER_NOT_FOUND = "§cThis user has never played on this server before";
    public static String CMD_USER_NOT_ON_LIST = "§cThis user is not on the current list";
    public static String CMD_USER_ON_LIST = "§cThis user is already on the current list";

    public static String CMD_LIST_EMPTY = "§7Your current list is §cempty";
    public static String CMD_ERROR_CFG = "§cThere was an error accessing your configuration";
    public static String CMD_ERROR_JOINMEGEN = "§cThere was an error generating or transmitting your JoinME";
    public static String CMD_ALREADY_ON_SERVER =  "§cYou are already on the target server";
    public static String CMD_JOINME_NOT_FOUND = "§cThis JoinMe has expired or does not exist";
    public static String CMD_JOINME_GENERATING = "§7Generating your JoinMe...";
    public static String CMD_JOINME_SENT = "§7You JoinMe has been sent to §a%NUM% §7players";
    public static String CMD_NOTIFICATIONS_DISABLED = "§7You will not receive JoinMEs anymore";
    public static String CMD_NOTIFICATIONS_ENABLED = "§7You will now receive JoinME";
    public static String CMD_LIST_ENABLED = "§7List-Invite is now §aenabled";
    public static String CMD_LIST_DISABLED = "§7List-Invite is now §cdisabled";
    public static String CMD_LIST_CLEARED = "§7You list has been cleared";

    public static String CMD_USER_ADDED = "§b%NAME% §7has been §aadded §7to the current list";
    public static String CMD_USER_REMOVED = "§b%NAME% §7has been §cremoved §7from the current list";
    public static String CMD_REDIRECTING_TO_SERVER = "§7Redirecting you to target server: §6%SERVER%§7...";
    public static String CMD_ALL_USERS_ON_LIST = "§7All users on your list:\n%USERS%";

    public static String JOINME_LINE1 = " §b%NAME% §7invites you all for playing";
    public static String JOINME_LINE2 = " §7Server: §6%SERVER_NAME%";
    public static String JOINME_LINEPREFIX = "§8▎ ";
    public static String JOINME_CHARACTER = "█";
    public static String JOINME_CLICK = "§c*CLICK*";
    public static String CMD_JOINME_NOT_ALLOWED = "§cJoinMe is not allowed on this server";

    public static String chatPrefix = "§5§lJoinMe+ §7| ";
    public static String CHAT_SEPERATOR_LINE = "§m--------------§r§7 [LIST] §m--------------";

    public MessagesConfig() {
        super(MessagesConfig.class, new File(main.pl.getDataFolder(), "messages.yml"));
    }
}
