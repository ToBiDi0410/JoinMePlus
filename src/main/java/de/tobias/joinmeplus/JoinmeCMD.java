package de.tobias.joinmeplus;

import de.tobias.joinmeplus.database.*;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JoinmeCMD extends Command implements TabExecutor {

    public JoinmeCMD(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender cmd, String[] args) {
        if (!(cmd instanceof ProxiedPlayer)) {
            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NOCONSOLE);
            return;
        }

        if (!cmd.hasPermission("joinme.command")) {
            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION);
            return;
        }

        ProxiedPlayer p = (ProxiedPlayer) cmd;

        UserSettings settings = new UserSettings(p.getUniqueId());
        settings = settings.load();
        if(settings == null) {
            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_ERROR_CFG);
            return;
        }


        if(args.length == 0) {
            if(!settings.listEnabled) {
                if (!cmd.hasPermission("joinme.invite.global")) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION.replace("%PERM%", "joinme.invite.global"));
                    return;
                }
            } else {
                if (!cmd.hasPermission("joinme.invite.list")) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION.replace("%PERM%", "joinme.invite.list"));
                    return;
                }
            }

            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_JOINME_GENERATING);
            try {
                BufferedImage img = ImageIO.read(new URL("https://crafatar.com/avatars/" + p.getUniqueId().toString() + "?size=8"));
                int width = img.getWidth();
                int height = img.getHeight();

                ArrayList<ProxiedPlayer> targets = new ArrayList<>();
                if(settings.listEnabled) {
                    OwnableUserList currentList = settings.getCurrentList();
                    for(String memberUUID : currentList.members) {
                        ProxiedPlayer member = ProxyServer.getInstance().getPlayer(memberUUID);
                        if(member != null) targets.add(member);
                    }
                    targets.add(p);
                } else {
                    targets = new ArrayList<>(ProxyServer.getInstance().getPlayers());
                }
                targets = targets.stream().filter((pp) -> {
                    UserSettings targetSettings = new UserSettings(pp.getUniqueId());
                    targetSettings = targetSettings.load();
                    if (targetSettings == null) return false;
                    return targetSettings.notificationsEnabled;
                }).collect(Collectors.toCollection(ArrayList::new));

                for(int y=0; y < height; y++) {
                    ArrayList<ChatColor> colors = new ArrayList<>();
                    for (int x = 0; x < width; x++) {
                        Color color = new Color(img.getRGB(x, y));
                        ChatColor cc = ChatColor.of(color);
                        colors.add(cc);
                    }

                    net.md_5.bungee.api.chat.TextComponent tcg = new net.md_5.bungee.api.chat.TextComponent(Messages.JOINME_LINEPREFIX);
                    for(ChatColor segmentColor : colors) {
                        net.md_5.bungee.api.chat.TextComponent tc = new TextComponent(Messages.JOINME_CHARACTER);
                        tc.setColor(segmentColor);
                        tcg.addExtra(tc);
                    }

                    if(y == 3) tcg.addExtra(Messages.JOINME_LINE1.replace("%NAME%", p.getDisplayName()));
                    if(y == 4) {
                        String joinMeID = UUID.randomUUID().toString();
                        JoinMeStore.joinmeServers.put(joinMeID, p.getServer());
                        tcg.addExtra(Messages.JOINME_LINE2.replace("%SERVER_NAME%", p.getServer().getInfo().getName()).replace("%SERVER_MOTD%", p.getServer().getInfo().getMotd()));
                        TextComponent clickable = new TextComponent(" " + Messages.JOINME_CLICK);
                        clickable.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/joinme accept " + joinMeID));
                        tcg.addExtra(clickable);
                    }

                    for(ProxiedPlayer target : targets) if(target != null && target.isConnected()) target.sendMessage(tcg);
                }

                Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_JOINME_SENT.replace("%NUM%", "" + (targets.size() - 1)));
            } catch (Exception ex) {
                Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_ERROR_JOINMEGEN);
                Logger.error("Failed to generate JoinME:");
                ex.printStackTrace();
            }
        } else if(args.length == 1) {
            if(args[0].equalsIgnoreCase("toggleNotifications")) {
                settings.notificationsEnabled = !settings.notificationsEnabled;
                settings.save();

                if(settings.notificationsEnabled) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NOTIFICATIONS_ENABLED);
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NOTIFICATIONS_DISABLED);
                }
            } else if(args[0].equalsIgnoreCase("enableList")) {
                if(!settings.listEnabled) {
                    settings.listEnabled = true;
                    settings.save();
                }
                Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_LIST_ENABLED);
            } else if(args[0].equalsIgnoreCase("disableList")) {
                if(settings.listEnabled) {
                    settings.listEnabled = false;
                    settings.save();
                }
                Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_LIST_DISABLED);
            } else if(args[0].equalsIgnoreCase("listClear")) {
                if (!cmd.hasPermission("joinme.lists.manage")) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION.replace("%PERM%", "joinme.lists.manage"));
                    return;
                }

                if(settings.listEnabled) {
                    OwnableUserList currentList = settings.getCurrentList();
                    currentList.members = new ArrayList<>();
                    currentList.save();
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_LIST_CLEARED);
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_REQUIRE_LIST_ENABLED);
                }
            } else if(args[0].equalsIgnoreCase("listInfo")) {
                if(settings.listEnabled) {
                    OwnableUserList currentList = settings.getCurrentList();
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CHAT_SEPERATOR_LINE);
                    Utils.sendMessage(cmd, Messages.chatPrefix + "Name: §a" + currentList.name);
                    Utils.sendMessage(cmd, Messages.chatPrefix + "Owner: §b" + currentList.owner);
                    Utils.sendMessage(cmd, Messages.chatPrefix + "Users: §6" + currentList.members.size());
                    Utils.sendMessage(cmd, Messages.chatPrefix + "Usernames: §8See with /joinme listUsers");
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CHAT_SEPERATOR_LINE);
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_REQUIRE_LIST_ENABLED);
                }
            } else if(args[0].equalsIgnoreCase("listUsers")) {
                if(settings.listEnabled) {
                    OwnableUserList currentList = settings.getCurrentList();
                    ArrayList<String> memberNames = currentList.getMemberNames();
                    if(memberNames.size() > 0) {
                        Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_ALL_USERS_ON_LIST.replace("%USERS%", String.join(",", memberNames)));
                    } else {
                        Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_LIST_EMPTY);
                    }
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_REQUIRE_LIST_ENABLED);
                }
            } else {
                Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_INVALID_SYNTAX);
            }
        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("listAddUser")) {
                if (!cmd.hasPermission("joinme.lists.manage")) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION.replace("%PERM%", "joinme.lists.manage"));
                    return;
                }

                if(settings.listEnabled) {
                    String userName = args[1];
                    UUID uuid;
                    if(ProxyServer.getInstance().getPlayer(userName) != null) uuid = ProxyServer.getInstance().getPlayer(userName).getUniqueId();
                    else uuid = Database.nameToUUID(userName);

                    if(uuid != null && uuid.toString().contains("-")) {
                        OwnableUserList currentList = settings.getCurrentList();
                        if(!currentList.members.contains(uuid.toString())) {
                            currentList.members.add(uuid.toString());
                            currentList.save();
                            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_USER_ADDED.replace("%NAME%", userName));
                        } else {
                            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_USER_ON_LIST);
                        }
                    } else {
                        Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_USER_NOT_FOUND);
                    }
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_REQUIRE_LIST_ENABLED);
                }
            } else if(args[0].equalsIgnoreCase("listRemoveUser")) {
                if (!cmd.hasPermission("joinme.lists.manage")) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION.replace("%PERM%", "joinme.lists.manage"));
                    return;
                }

                if(settings.listEnabled) {
                    String userName = args[1];
                    UUID uuid;
                    if(ProxyServer.getInstance().getPlayer(userName) != null) uuid = ProxyServer.getInstance().getPlayer(userName).getUniqueId();
                    else uuid = Database.nameToUUID(userName);

                    if(uuid != null && uuid.toString().contains("-")) {
                        OwnableUserList currentList = settings.getCurrentList();
                        if(currentList.members.contains(uuid.toString())) {
                            currentList.members.remove(uuid.toString());
                            currentList.save();
                            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_USER_REMOVED.replace("%NAME%", userName));
                        } else {
                            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_USER_NOT_ON_LIST);
                        }
                    } else {
                        Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_USER_NOT_FOUND);
                    }
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_REQUIRE_LIST_ENABLED);
                }
            } else if (args[0].equalsIgnoreCase("accept")) {
                if (!cmd.hasPermission("joinme.accept")) {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_NO_PERMISSION.replace("%PERM%", "joinme.accept"));
                    return;
                }

                if(JoinMeStore.joinmeServers.containsKey(args[1])) {
                    Server srv = JoinMeStore.joinmeServers.get(args[1]);
                    if(p.getServer() != srv) {
                        Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_REDIRECTING_TO_SERVER.replace("%SERVER%", srv.getInfo().getName()));
                        p.connect(srv.getInfo());
                    } else {
                        Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_ALREADY_ON_SERVER);
                    }
                } else {
                    Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_JOINME_NOT_FOUND);
                }
            } else {
                Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_INVALID_SYNTAX);
            }
        } else {
            Utils.sendMessage(cmd, Messages.chatPrefix + Messages.CMD_INVALID_SYNTAX);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender cmd, String[] args) {
        List<String> proposals = new ArrayList<>();

        if(args.length == 1) {
            proposals.add("disableList");
            proposals.add("enableList");
            proposals.add("listInfo");
            proposals.add("listUsers");
            proposals.add("listRemoveUser");
            proposals.add("listAddUser");
            proposals.add("listClear");
            proposals = proposals.stream().filter((b) -> b.startsWith(args[0])).collect(Collectors.toList());
        }

        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("listRemoveUser")) {
                for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) proposals.add(p.getName());
            }

            if(args[0].equalsIgnoreCase("listAddUser")) {
                for(ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) proposals.add(p.getName());
            }

            proposals = proposals.stream().filter((b) -> b.startsWith(args[1])).collect(Collectors.toList());
        }

        return proposals;
    }
}
