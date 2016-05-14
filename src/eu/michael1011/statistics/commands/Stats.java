package eu.michael1011.statistics.commands;

import eu.michael1011.statistics.main.Main;
import eu.michael1011.statistics.main.SQL;
import eu.michael1011.statisticsgui.gui.ShowPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static eu.michael1011.statistics.main.Main.messages;
import static eu.michael1011.statistics.main.Main.prefix;


public class Stats implements CommandExecutor {

    static int deaths = 0, killed = 0, killsEntities = 0, killsPlayers = 0;

    static String ipAddress = "not available", lastSeen = "not available", firstJoin = "not available";
    static int timesConnected = 0, onlineTime = 0;

    static String time = "not available", tps = "not available", online = "not available", ramUsage = "not available";
    static int playersOnline = 0, ram = 0, freeRam = 0;

    private Boolean lastLine = messages.getBoolean("Commands.lastLine");

    private static Main pl;

    public Stats(Main main) {
        pl = main;
        pl.getCommand("stat").setExecutor(this);
        pl.getCommand("stat").setTabCompleter(new StatsTab());
    }

    private String getLastString(int length) {
        String last = "";

        for(int i = 7; i <= length; i++) {
            last = last+"-";
        }

        return ChatColor.translateAlternateColorCodes('&', "&7"+last);
    }


    private void sendHelp(CommandSender sender) {
        String usage = messages.getString("Commands.usage");

        sender.sendMessage("");
        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', usage));
        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.stats.helpServer")));
        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.stats.helpGUI")));
        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.stats.help")));
        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.stats.args")));


        if(lastLine) {
            sender.sendMessage(prefix+getLastString(usage.length()));
        }

    }

    private void playerNotFound(String playerName, CommandSender sender) {
        String playerNotFound = messages.getString("Commands.playerNotFound");
        playerNotFound = playerNotFound.replaceAll("%player%", playerName);

        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', playerNotFound));
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender.hasPermission("statistics.show")) {
            if(args.length == 0) {
                sendHelp(sender);

            } else if(args.length == 1) {
                if(args[0].equalsIgnoreCase("server")) {
                    GetData.getServerData();

                    String path = "Commands.stats.server.";
                    String variable = "%data%";

                    String header = messages.getString("Commands.stats.header").replaceAll("%player%", "Server");
                    String timeM = messages.getString(path + "time").replaceAll(variable, time);
                    String tpsM = messages.getString(path + "tps").replaceAll(variable, tps);
                    String onlineM = messages.getString(path + "online").replaceAll(variable, online);
                    String playersOnlineM = messages.getString(path + "playersOnline").replaceAll(variable, String.valueOf(playersOnline));
                    String ramM = messages.getString(path + "ram").replaceAll(variable, String.valueOf(ram));
                    String freeRamM = messages.getString(path + "freeRam").replaceAll(variable, String.valueOf(freeRam));
                    String ramUsageM = messages.getString(path + "ramUsage").replaceAll(variable, ramUsage);

                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', header));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', timeM));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', tpsM));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', onlineM));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', playersOnlineM));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', ramM));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', freeRamM));
                    sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', ramUsageM));

                    if (lastLine) {
                        sender.sendMessage(prefix + getLastString(header.length()));
                    }

                } else if(args[0].equalsIgnoreCase("gui")) {

                    if(pl.getServer().getPluginManager().getPlugin("StatisticsGUI") != null) {
                        // todo: open GUI, this with <player> argument
                        // todo: show a overview here

                    } else {
                        String message = messages.getString("Plugin.otherPluginMissing").replaceAll("%plugin%", "StatisticsGUI");
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', message));
                    }

                } else {
                    sendHelp(sender);
                }

            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("life")) {

                    @SuppressWarnings("deprecation")
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);

                    if (SQL.columnExists(p.getUniqueId().toString(), "stats_life")) {
                        GetData.getLifeData(p.getUniqueId().toString());

                        String path = "Commands.stats.life.";
                        String variable = "%count%";

                        int kills = killsEntities + killsPlayers;

                        String header = messages.getString("Commands.stats.header").replaceAll("%player%", args[1]);
                        String deathsM = messages.getString(path+"deaths").replaceAll(variable, Integer.toString(deaths));
                        String killedM = messages.getString(path+"killed").replaceAll(variable, Integer.toString(killed));
                        String killsM = messages.getString(path+"kills").replaceAll(variable, Integer.toString(kills));
                        String killsEntitiesM = messages.getString(path+"killsEntities").replaceAll(variable, Integer.toString(killsEntities));
                        String killsPlayersM = messages.getString(path+"killsPlayers").replaceAll(variable, Integer.toString(killsPlayers));


                        sender.sendMessage("");
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', header));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', deathsM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', killedM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', killsM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', killsEntitiesM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', killsPlayersM));

                        if(lastLine) {
                            sender.sendMessage(prefix+getLastString(header.length()));
                        }

                    } else {
                        playerNotFound(args[1], sender);
                    }

                } else if(args[0].equalsIgnoreCase("general")) {

                    @SuppressWarnings("deprecation")
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);

                    if (SQL.columnExists(p.getUniqueId().toString(), "stats")) {
                        GetData.getPlayerData(p.getUniqueId().toString());

                        String path = "Commands.stats.general.";
                        String variable = "%data%";

                        String header = messages.getString("Commands.stats.header").replaceAll("%player%", args[1]);
                        String ipAddressM = messages.getString(path + "ipAddress").replaceAll(variable, ipAddress);
                        String timesConnectedM = messages.getString(path + "timesConnected").replaceAll(variable, Integer.toString(timesConnected));
                        String onlineTimeM = messages.getString(path + "onlineTime").replaceAll(variable, Integer.toString(onlineTime));
                        String lastSeenM = messages.getString(path + "lastSeen").replaceAll(variable, lastSeen);
                        String firstJoinM = messages.getString(path + "firstJoin").replaceAll(variable, firstJoin);

                        sender.sendMessage("");
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', header));
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', ipAddressM));
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', timesConnectedM));
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', onlineTimeM));
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', lastSeenM));
                        sender.sendMessage(prefix + ChatColor.translateAlternateColorCodes('&', firstJoinM));

                        if (lastLine) {
                            sender.sendMessage(prefix + getLastString(header.length()));
                        }

                    } else {
                        playerNotFound(args[1], sender);
                    }

                } else if(args[0].equalsIgnoreCase("gui")) {
                    if(pl.getServer().getPluginManager().getPlugin("StatisticsGUI") != null) {

                        @SuppressWarnings("deprecation")
                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

                        if(sender instanceof Player) {
                            Player p = (Player) sender;

                            if(SQL.columnExists(p.getUniqueId().toString(), "stats")) {
                                ShowPlayer.showPlayer(p, target);

                            } else {
                                playerNotFound(args[1], sender);
                            }

                        } else {
                            // todo: message: only players
                        }

                    } else {
                        String message = messages.getString("Plugin.otherPluginMissing").replaceAll("%plugin%", "StatisticsGUI");
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', message));
                    }

                } else {
                    sendHelp(sender);
                }

            } else {
                sendHelp(sender);
            }

        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.noPermissions")));
        }

        return true;

    }

}
