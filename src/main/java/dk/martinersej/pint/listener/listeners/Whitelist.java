package dk.martinersej.pint.listener.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class Whitelist implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (!player.isWhitelisted() && !player.isOp()) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted on this server.");
        }
    }
}