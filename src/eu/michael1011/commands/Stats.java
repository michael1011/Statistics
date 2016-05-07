package eu.michael1011.commands;

import eu.michael1011.listeners.Join;
import eu.michael1011.main.Main;
import eu.michael1011.main.SQL;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Stats implements CommandExecutor {

    private HashMap<String, String> data;

    private static YamlConfiguration messages = Main.messages;

    public Stats(Main main) {
        main.getCommand("stat").setExecutor(this);
    }

    private void getPlayerData(String player) {
        UUID id = Bukkit.getPlayer(player).getUniqueId();
        String trimmedID = id.toString().replaceAll("-", "");

        if (Join.columnExists(trimmedID)) {
            ResultSet rs = SQL.getResult("select * from "+trimmedID);

            try {
                assert rs != null;

                if(rs.next()) {
                    data.put("name", rs.getString(1));
                    data.put("deaths", Integer.toString(rs.getInt(2)));
                    data.put("killed", Integer.toString(rs.getInt(3)));
                    data.put("kills_entities", Integer.toString(rs.getInt(4)));
                    data.put("kills_players", Integer.toString(rs.getInt(5)));
                }

            } catch(SQLException e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(args.length == 0) {
            sender.sendMessage("");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.usage")));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', messages.getString("Commands.stats.help")));

        } else if(args.length == 1) {
            Player p = Bukkit.getPlayer(args[0]);
            String trimmedID = p.getUniqueId().toString().replaceAll("-", "");

            getPlayerData(trimmedID);



        }

        return true;
    }

}
