package eu.michael1011.statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

class ServerStats {

    private static int interval = (Main.config.getInt("Interval.ServerStats")*20)*60;

    private static int mb = 1048576;

    @SuppressWarnings("deprecation")
    static void trackServer(Plugin plugin) {
        plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {

            @Override
            public void run() {
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

                long totalRam = rt.maxMemory()/mb;

                long freeRam = rt.freeMemory()/mb;

                long usedRam = totalRam-freeRam;

                SQL.update("insert into stats_server (time, tps, online, playersOnline, ram, freeRam, ramUsage) values" +
                        "('"+dateFormat.format(date)+"', '"+tps+"', '"+online+"', '"+playersOnline+"', '"+String.valueOf(totalRam)+"', " +
                        "'"+String.valueOf(freeRam)+"', '"+String.valueOf((usedRam*100)/totalRam)+"%"+"')");

            }
        }, interval, interval);
    }

    private static boolean testNet(String site) {
        Socket sock = new Socket();
        InetSocketAddress addr = new InetSocketAddress(site,80);
        try {
            sock.connect(addr,3000);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            try {sock.close();}
            catch (IOException e) {}
        }
    }

}
