package eu.michael1011.main;

import eu.michael1011.commands.Stats;
import eu.michael1011.listeners.Join;
import eu.michael1011.listeners.Kill;
import eu.michael1011.listeners.Leave;
import eu.michael1011.listeners.OnlineTime;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class Main extends JavaPlugin {

    // todo: updater, config updater

    public static YamlConfiguration config, messages;

    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&e[Statistics] ");

    public static ConsoleCommandSender console;

    @Override
    public void onEnable() {
        super.onEnable();

        createFiles();

        if(config.getBoolean("CollectMetrics")) {
            try {
                MetricsLite metrics = new MetricsLite(this);
                metrics.start();

            } catch(IOException e) {
                e.printStackTrace();
            }

        }

        startListeners();
        startCommands();

        SQL.establishMySQL();

        console = Bukkit.getConsoleSender();

        if(SQL.connection != null) {
            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("SQL.connected")));

            SQL.update("create table if not exists stats_life (uuid TEXT(100), deaths BIGINT(255), " +
                    "killed BIGINT(255), kills_entities BIGINT(255), kills_players BIGINT(255))");

            SQL.update("create table if not exists stats (uuid TEXT(100), ipAddress TEXT(100), " +
                    "timesConnected BIGINT(255), onlineTime BIGINT(255), lastSeen TEXT(100), firstJoin TEXT(100))");

        } else {
            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("SQL.failed")));
        }

        OnlineTime.trackTime(this);

        console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Plugin.enabled")));
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if(SQL.connection != null) {
            SQL.closeCon();
        }

        console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Plugin.disabled")));
    }

    private void startListeners() {
        new Join(this);
        new Leave(this);
        new Kill(this);
    }

    private void startCommands() {
        new Stats(this);
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
