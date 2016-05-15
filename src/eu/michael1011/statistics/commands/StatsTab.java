package eu.michael1011.statistics.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class StatsTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        if(cmd.getName().equalsIgnoreCase("stat")) {
            int length = args.length;
            List<String> list = new ArrayList<>();

            if(length == 1) {
                list.add("general");
                list.add("life");
                list.add("server");
                list.add("gui");

            } else if(length == 2) {
                if(args[0].equalsIgnoreCase("gui")) {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }

                    list.add("server");

                } else if(!args[0].equalsIgnoreCase("server")) {
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        list.add(p.getName());
                    }
                }

            }

            return list;
        }

        return null;

    }

}
