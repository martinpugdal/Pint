package dk.martinersej.pint.listener;

import dk.martinersej.pint.listener.listeners.GlobalChat;
import org.bukkit.plugin.java.JavaPlugin;

public class ListenerHandler {

    public ListenerHandler() {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        plugin.getServer().getPluginManager().registerEvents(new GlobalChat(), plugin);
    }
}
