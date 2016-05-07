package eu.michael1011.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;

class OnlineTime {

    private static int onlineTime = 0;

    static void trackTime(Plugin plugin) {
        plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    String id = p.getUniqueId().toString();

                    ResultSet rs = SQL.getResult("select * from stats where uuid='"+id+"'");

                    try {
                        assert rs != null;

                        if(rs.next()) {
                            onlineTime = rs.getInt(4)+1;
                        }

                    } catch(SQLException e) {
                        e.printStackTrace();
                    }

                    SQL.update("update stats set onlineTime='"+onlineTime+"' where uuid='"+id+"'");

                }

            }
        }, 1200, 1200);

    }

}
