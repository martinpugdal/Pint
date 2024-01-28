package dk.martinersej.pint.utils.gui;

import org.bukkit.plugin.java.JavaPlugin;

public class GuiListeners {

    public GuiListeners() {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        plugin.getServer().getPluginManager().registerEvents(new OnGuiClick(), plugin);
    }
}
