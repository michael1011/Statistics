package eu.michael1011.listeners;

import eu.michael1011.main.Main;
import eu.michael1011.main.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Join implements Listener {

    public Join(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    private int timesConnected = 0;

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        String id = uuid.toString();

        if(!SQL.columnExists(id, "stats_life")) {
            SQL.update("insert into stats_life (uuid, deaths, killed, kills_entities, kills_players) " +
                    "values ('"+id+"', '0', '0', '0', '0')");
        }

        if(!SQL.columnExists(id, "stats")) {
            // todo: online time minutes or seconds

            // todo: check for substring also on external ip

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();

            String dateToday = dateFormat.format(date);

            String ip = p.getAddress().toString();

            if(ip.startsWith("/")) {
                ip = ip.substring(1);
            }

            SQL.update("insert into stats (uuid, ipAddress, timesConnected, onlineTime, lastSeen, firstJoin)" +
                    " values ('"+id+"', '"+ip.substring(0, ip.length() - 5)+"', '1', '0', '"+Main.messages.getString("Plugin.now")+"', '"+dateToday+"')");

        } else {
            ResultSet rs = SQL.getResult("select * from stats where uuid='"+id+"'");

            try {
                assert rs != null;

                if(rs.next()) {
                    timesConnected = rs.getInt(3)+1;
                }

            } catch(SQLException e1) {
                e1.printStackTrace();
            }

            String ip = p.getAddress().toString();

            if(ip.startsWith("/")) {
                ip = ip.substring(1);
            }

            SQL.update("update stats set timesConnected='"+timesConnected+"' where uuid='"+id+"'");
            SQL.update("update stats set ipAddress='"+ip.substring(0, ip.length() - 6)+"' where uuid='"+id+"'");
            SQL.update("update stats set lastSeen='"+Main.messages.getString("Plugin.now")+"' where uuid='"+id+"'");

        }

    }

}
