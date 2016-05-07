package eu.michael1011.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StatsTab implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String s, String[] args) {

        // todo: test this

        if(cmd.getName().equalsIgnoreCase("stat")) {
            int length = args.length;
            List<String> list = new ArrayList<>();

            if(length == 1) {
                list.add("general");
                list.add("life");

            } else if(length == 2) {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    list.add(p.getName());
                }
            }

            return list;
        }

        return null;

    }

}
