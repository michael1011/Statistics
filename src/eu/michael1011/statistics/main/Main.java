package eu.michael1011.statistics.main;

import eu.michael1011.statistics.commands.Stats;
import eu.michael1011.statistics.listeners.Join;
import eu.michael1011.statistics.listeners.Kill;
import eu.michael1011.statistics.listeners.Leave;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class Main extends JavaPlugin {

    public static YamlConfiguration messages;
    static YamlConfiguration config;

    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&e[Statistics] ");

    private static ConsoleCommandSender console;

    @Override
    public void onEnable() {
        createFiles();

        if(config.getBoolean("CollectMetrics")) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();

            } catch(IOException e) {
                e.printStackTrace();
            }

        }

        if(config.getBoolean("Updater.enable")) {
            update();
        }

        startListeners();
        startCommands();

        SQL.establishMySQL();

        console = Bukkit.getConsoleSender();

        if(SQL.connection != null) {
            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("SQL.connected")));

            SQL.update("create table if not exists stats (uuid TEXT(100), ipAddress TEXT(100), " +
                    "timesConnected BIGINT(255), onlineTime BIGINT(255), lastSeen TEXT(100), firstJoin TEXT(100))");

            SQL.update("create table if not exists stats_life (uuid TEXT(100), deaths BIGINT(255), " +
                    "killed BIGINT(255), kills_entities BIGINT(255), kills_players BIGINT(255))");

            SQL.update("create table if not exists stats_server (time TEXT(100), tps TEXT(100), online TEXT(100), " +
                    "playersOnline BIGINT(100), ram TEXT(100), freeRam TEXT(100), ramUsage TEXT(100))");

            TPSTracker.startTracking(this);
            OnlineTime.trackTime(this);
            ServerStats.trackServer(this);

            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Plugin.enabled")));

        } else {
            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("SQL.failed")));
        }

    }

    @Override
    public void onDisable() {
        if(SQL.connection != null) {
            SQL.closeCon();
        }

        Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Plugin.disabled")));
    }

    private void startListeners() {
        new Join(this);
        new Leave(this);
        new Kill(this);
    }

    private void startCommands() {
        new Stats(this);
    }

    private void update() {
        Updater updater = new Updater(this, 100073, this.getFile(), Updater.UpdateType.DEFAULT, false);

        if(!config.getBoolean("Updater.autoDownload")) {
            updater = new Updater(this, 100073, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        }

        Updater.UpdateResult result = updater.getResult();

        switch(result) {
            case SUCCESS:
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Updater.updateFinished")
                .replaceAll("%version%", updater.getLatestName())));
                break;
            case NO_UPDATE:
                console.sendMessage("works");
                break;
            case DISABLED:
                break;
            case FAIL_DOWNLOAD:
                break;
            case FAIL_DBO:
                break;
            case FAIL_NOVERSION:
                break;
            case FAIL_BADID:
                break;
            case FAIL_APIKEY:
                break;
            case UPDATE_AVAILABLE:
                Bukkit.getConsoleSender().sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', config.getString("Updater.updateAvailable")
                        .replaceAll("%version%", updater.getLatestName()).replaceAll("%downloadLink%", updater.getLatestFileLink())));
                break;
        }

    }

    private void createFiles() {
        File configF = new File(getDataFolder(), "config.yml");
        File messagesF = new File(getDataFolder(), "messages.yml");

        if(!configF.exists()) {
            Boolean mkdirs = configF.getParentFile().mkdirs();
            copy(getResource("config.yml"), configF);
        }

        if(!messagesF.exists()) {
            Boolean mkdirs = messagesF.getParentFile().mkdirs();
            copy(getResource("messages.yml"), messagesF);
        }

        config = new YamlConfiguration();
        messages = new YamlConfiguration();

        try {
            config.load(configF);
            messages.load(messagesF);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(Integer.valueOf(config.getString("ConfigVersion")) == 1) {
            config.set("Updater.enable", true);
            config.set("Updater.autoDownload", true);
            config.set("Updater.updateFinished", "&7The plugin was updated successful to version &e%version%&7! Reload or restart the server to enable it.");
            config.set("Updater.updateAvailable", "&7A new version (&e%version%&7) a available! &7Download it here: &e%downloadLink%");
            config.set("ConfigVersion", 2);

            try {
                config.save(configF);
            } catch(IOException e) {
                console.sendMessage(prefix+"&4Failed to update config files. Please delete them.");
            }

        }

    }

    private void copy(InputStream in, File file) {
        try {
            OutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;

            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            out.close();
            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
