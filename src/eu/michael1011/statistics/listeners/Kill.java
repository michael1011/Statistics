package eu.michael1011.statistics.listeners;

import eu.michael1011.statistics.main.Main;
import eu.michael1011.statistics.main.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class Kill implements Listener {

    public Kill(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void PlayerKill(PlayerDeathEvent e) {
        Player killed = e.getEntity();
        UUID killedID = killed.getUniqueId();
        String killedTr = killedID.toString();

        ResultSet rsKilled = SQL.getResult("select * from stats_life where uuid='"+killedTr+"'");

        try {
            assert rsKilled != null;

            if(rsKilled.next()) {
                int oldDeaths = rsKilled.getInt(2)+1;
                SQL.update("update stats_life set deaths='"+oldDeaths+"' where uuid='"+killedTr+"'");
            }

        } catch(SQLException e1) {
            e1.printStackTrace();
        }

        if(e.getEntity().getKiller() != null) {
            Player killer = e.getEntity().getKiller();
            UUID killerID = killer.getUniqueId();
            String killerTr = killerID.toString();

            ResultSet rsKiller = SQL.getResult("select * from stats_life where uuid='"+killerTr+"'");

            try {
                int oldKilled = rsKilled.getInt(3)+1;
                SQL.update("update stats_life set killed='"+oldKilled+"' where uuid='"+killedTr+"'");

                assert rsKiller != null;

                if(rsKiller.next()) {
                    int oldKillsPlayer = rsKiller.getInt(5)+1;
                    SQL.update("update stats_life set kills_players='"+oldKillsPlayer+"' where uuid='"+killerTr+"'");
                }

            } catch(SQLException e1) {
                e1.printStackTrace();
            }

        }

    }


    @EventHandler
    public void mobKill(EntityDeathEvent e) {
        if(!(e.getEntity() instanceof Player)) {
            Player p = e.getEntity().getKiller();

            if(p != null) {
                String id = p.getUniqueId().toString();

                ResultSet rs = SQL.getResult("select * from stats_life where uuid='"+id+"'");

                try {
                    assert rs != null;

                    if(rs.next()) {
                        int mobKill = rs.getInt(4)+1;

                        SQL.update("update stats_life set kills_entities='"+mobKill+"' where uuid='"+id+"'");
                    }

                } catch(SQLException e1) {
                    e1.printStackTrace();
                }

            }
        }
    }


}
