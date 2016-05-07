package eu.michael1011.commands;

import eu.michael1011.main.Main;
import eu.michael1011.main.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.ResultSet;
import java.sql.SQLException;

import static eu.michael1011.main.Main.messages;
import static eu.michael1011.main.Main.prefix;

public class Stats implements CommandExecutor {

    private int deaths = 0, killed = 0, kills_entities = 0, kills_players = 0;

    private String ipAddress = "not available", lastSeen = "not available", firstJoin = "not available";
    private int timesConnected = 0, onlineTime = 0;

    private Boolean lastLine = messages.getBoolean("Commands.lastLine");


    public Stats(Main main) {
        main.getCommand("stat").setExecutor(this);
        main.getCommand("stat").setTabCompleter(new StatsTab());
    }

    private void getPlayerData(String id) {
        ResultSet rs = SQL.getResult("select * from stats where uuid='"+id+"'");

        try {
            assert rs != null;

            if(rs.next()) {
                ipAddress = rs.getString(2);
                timesConnected = rs.getInt(3);
                onlineTime = rs.getInt(4);
                lastSeen = rs.getString(5);
                firstJoin = rs.getString(6);
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

    }

    private void getLifeData(String id) {
        ResultSet rs = SQL.getResult("select * from stats_life where uuid='"+id+"'");

        try {
            assert rs != null;

            if(rs.next()) {
                deaths = rs.getInt(2);
                killed = rs.getInt(3);
                kills_entities = rs.getInt(4);
                kills_players = rs.getInt(5);
            }

         } catch(SQLException e) {
            e.printStackTrace();
        }

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
                sendHelp(sender);

            } else if(args.length == 2) {
                if(args[0].equalsIgnoreCase("life")) {

                    @SuppressWarnings("deprecation")
                    OfflinePlayer p = Bukkit.getOfflinePlayer(args[1]);

                    if (SQL.columnExists(p.getUniqueId().toString(), "stats_life")) {
                        getLifeData(p.getUniqueId().toString());

                        String path = "Commands.stats.life.";
                        String variable = "%count%";

                        int kills = kills_entities + kills_players;

                        String header = messages.getString("Commands.stats.header").replaceAll("%player%", args[1]);
                        String deathsM = messages.getString(path+"deaths").replaceAll(variable, Integer.toString(deaths));
                        String killedM = messages.getString(path+"killed").replaceAll(variable, Integer.toString(killed));
                        String killsM = messages.getString(path+"kills").replaceAll(variable, Integer.toString(kills));
                        String killsEntitiesM = messages.getString(path+"killsEntities").replaceAll(variable, Integer.toString(kills_entities));
                        String killsPlayersM = messages.getString(path+"killsPlayers").replaceAll(variable, Integer.toString(kills_players));


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

                    if(SQL.columnExists(p.getUniqueId().toString(), "stats")) {
                        getPlayerData(p.getUniqueId().toString());

                        String path = "Commands.stats.general.";
                        String variable = "%data%";

                        String header = messages.getString("Commands.stats.header").replaceAll("%player%", args[1]);
                        String ipAddressM = messages.getString(path+"ipAddress").replaceAll(variable, ipAddress);
                        String timesConnectedM = messages.getString(path+"timesConnected").replaceAll(variable, Integer.toString(timesConnected));
                        String onlineTimeM = messages.getString(path+"onlineTime").replaceAll(variable, Integer.toString(onlineTime));
                        String lastSeenM = messages.getString(path+"lastSeen").replaceAll(variable, lastSeen);
                        String firstJoinM = messages.getString(path+"firstJoin").replaceAll(variable, firstJoin);

                        sender.sendMessage("");
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', header));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', ipAddressM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', timesConnectedM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', onlineTimeM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', lastSeenM));
                        sender.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', firstJoinM));

                        if(lastLine) {
                            sender.sendMessage(prefix+getLastString(header.length()));
                        }

                    } else {
                        playerNotFound(args[1], sender);
                    }

                } else {
                    // todo: show overview

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
