package eu.michael1011.statistics.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class TPSTracker implements Runnable {

    public static int TICK_COUNT= 0;
    public static long[] TICKS= new long[600];
    public static long LAST_TICK= 0L;

    public static double getTPS() {
        return getTPS(100);
    }

    public static void startTracking(Plugin plugin) {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new TPSTracker(), 100L, 1L);
    }

    private static double getTPS(int ticks) {
        if (TICK_COUNT< ticks) {
            return 20.0D;
        }

        int target = (TICK_COUNT- 1 - ticks) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return ticks / (elapsed / 1000.0D);
    }

    public static long getElapsed(int tickID) {
        if (TICK_COUNT- tickID >= TICKS.length) {
        }

        long time = TICKS[(tickID % TICKS.length)];
        return System.currentTimeMillis() - time;
    }

    public void run() {
        TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();

        TICK_COUNT+= 1;
    }

}