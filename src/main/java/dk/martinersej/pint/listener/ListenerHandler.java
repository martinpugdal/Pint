package dk.martinersej.pint.listener;

import dk.martinersej.pint.listener.listeners.global.Chat;
import dk.martinersej.pint.listener.listeners.global.Connection;
import dk.martinersej.pint.listener.listeners.global.PvP;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerHandler {

    public ListenerHandler() {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());

        plugin.getServer().getPluginManager().registerEvents(new Chat(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new Connection(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PvP(), plugin);
    }
}
