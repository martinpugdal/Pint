package dk.martinersej.pint.listener;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerHandler {

    public ListenerHandler() {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        System.out.println(plugin);
        System.out.println((JavaPlugin) Bukkit.getPluginManager().getPlugin(getClass().getPackage().getName().split("^\\w+")[0]));
        plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(getClass().getPackage().getName().split("^\\w+")[0]);
//        plugin.getServer().getPluginManager().registerEvents(new Whitelist(), plugin);
    }
}
