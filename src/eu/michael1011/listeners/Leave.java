package eu.michael1011.listeners;

import eu.michael1011.main.Main;
import eu.michael1011.main.SQL;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Leave implements Listener {

    public Leave(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void leave(PlayerQuitEvent e) {
        Player p = e.getPlayer();

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String dateToday = dateFormat.format(date);

        SQL.update("update stats set lastSeen='"+dateToday+"' where uuid='"+p.getUniqueId()+"'");
    }

}
