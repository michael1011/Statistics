package eu.michael1011.listeners;

import eu.michael1011.main.Main;
import eu.michael1011.main.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.UUID;

public class Join implements Listener {

    public Join(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        String trimmedID = id.toString().replaceAll("-", "");

        if(!columnExists(trimmedID)) {
            SQL.update("insert into stats (uuid, name, deaths, killed, kills_entities, kills_players) " +
                    "values ('"+trimmedID+"', '"+p.getDisplayName()+"', '0', '0', '0', '0')");
        }

    }

    public static Boolean columnExists(String uuid) {
        try {
            ResultSet rs = SQL.getResult("select * from stats where uuid='"+uuid+"'");

            assert rs != null;

            if(rs.next()) {
                return rs.getString(1) != null;
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

}
