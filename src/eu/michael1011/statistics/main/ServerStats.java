package eu.michael1011.statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerStats {

    private static int interval = (Main.config.getInt("Interval.ServerStats")*20)*60;

    @SuppressWarnings("deprecation")
    static void trackServer(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                collectStats();
            }
        }, 1200, interval);
    }

    static void listenRefresh(Plugin plugin) {
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                ResultSet rs = SQL.getResult("select * from stats_update");

                assert rs != null;

                try {
                    if(rs.next()) {
                        if(rs.getString(1).equals("yes")) {
                            collectStats();

                            SQL.update("update stats_update set refresh='no'");
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }, 200, 200);
    }

    public static void collectStats() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();

        String tps = String.valueOf(TPSTracker.getTPS());

        String online = "offline";

        if(testNet("google.at")) {
            online = "online";
        }

        int playersOnline = 0;

        for(Player p : Bukkit.getOnlinePlayers()) {
            playersOnline++;
        }

        Runtime rt = Runtime.getRuntime();

        int mb = 1048576;
        long totalRam = rt.maxMemory()/ mb;

        long freeRam = rt.freeMemory()/ mb;

        long usedRam = totalRam-freeRam;

        SQL.update("insert into stats_server (time, tps, online, playersOnline, ram, freeRam, ramUsage) values" +
                "('"+dateFormat.format(date)+"', '"+tps+"', '"+online+"', '"+playersOnline+"', '"+String.valueOf(totalRam)+"', " +
                "'"+String.valueOf(freeRam)+"', '"+String.valueOf((usedRam*100)/totalRam)+"%"+"')");
    }

    private static boolean testNet(String site) {
        Socket sock = new Socket();
        InetSocketAddress address = new InetSocketAddress(site,80);
        try {
            sock.connect(address,3000);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
