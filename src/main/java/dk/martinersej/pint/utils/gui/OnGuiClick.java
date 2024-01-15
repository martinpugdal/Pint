package dk.martinersej.pint.utils.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnGuiClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof BaseGui) {
            BaseGui baseGui = (BaseGui) event.getInventory().getHolder();
            if (baseGui.cooldownEnabled()) {
                baseGui.cooldownCheck(event);
            }
            baseGui.onInventoryClick(event);
        }
    }
}