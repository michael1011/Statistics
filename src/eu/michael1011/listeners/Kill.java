package eu.michael1011.listeners;

import eu.michael1011.main.Main;
import eu.michael1011.main.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        String killedTr = killedID.toString().replaceAll("-", "");

        ResultSet rsKilled = SQL.getResult("select * from stats where uuid='"+killedTr+"'");

        try {
            assert rsKilled != null;

            if(rsKilled.next()) {
                int oldDeaths = rsKilled.getInt(3)+1;
                SQL.update("update stats set deaths='"+oldDeaths+"' where uuid='"+killedTr+"'");
            }

        } catch(SQLException e1) {
            e1.printStackTrace();
        }

        if(e.getEntity().getKiller() != null) {
            // todo: test this

            Player killer = e.getEntity().getKiller();
            UUID killerID = killer.getUniqueId();
            String killerTr = killerID.toString().replaceAll("-", "");

            ResultSet rsKiller = SQL.getResult("select * from stats where uuid='"+killerTr+"'");

            try {
                int oldKilled = rsKilled.getInt(4)+1;
                SQL.update("update stats set killed='"+oldKilled+"' where uuid='"+killedTr+"'");

                assert rsKiller != null;

                int oldKillsPlayer = rsKiller.getInt(6);
                SQL.update("update stats set kills_players='"+oldKillsPlayer+"' where uuid='"+killerTr+"'");

            } catch(SQLException e1) {
                e1.printStackTrace();
            }

        }

    }


}
