package dk.martinersej.pint.listener.listeners.global;

import dk.martinersej.pint.Pint;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class Hunger implements Listener {

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!Pint.getInstance().getGameHandler().isPlayerInGame(player)) {
                event.setCancelled(true);
            }
        }
    }
}
