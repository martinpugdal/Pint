package dk.martinersej.pint.listener;

import dk.martinersej.pint.listener.listeners.Whitelist;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerHandler {

    public ListenerHandler(JavaPlugin plugin) {

        plugin.getServer().getPluginManager().registerEvents(new Whitelist(), plugin);
    }
}
