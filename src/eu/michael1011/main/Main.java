package eu.michael1011.main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Main extends JavaPlugin {

    public static YamlConfiguration config, messages;

    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&5Stats: ");

    public static ConsoleCommandSender console;

    @Override
    public void onEnable() {
        super.onEnable();

        createFiles();
        SQL.establishMySQL();

        console = Bukkit.getConsoleSender();

        if(SQL.connection != null) {
            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("SQL.connected")));
        } else {
            console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("SQL.failed")));
        }

        console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Plugin.enabled")));
    }

    @Override
    public void onDisable() {
        super.onDisable();

        console.sendMessage(prefix+ChatColor.translateAlternateColorCodes('&', messages.getString("Plugin.disabled")));
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
