package dk.martinersej.pint.listener.listeners.global;

import dk.martinersej.pint.simplecommand.WhitelistCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Connection implements Listener {

    @EventHandler
    public void onConnection(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        if (WhitelistCommand.WIHTELIST_MODE && (!player.isOp() && !player.isWhitelisted())) {
            event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, "§cDu er ikke whitelisted på denne server\n§e§oVi åbner snart op for alle");
            Bukkit.broadcastMessage("§8[§c-§8] §7" + player.getName() + " §cblev smidt af serveren (Ikke whitelisted)");
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage("§8[§a+§8] §7" + player.getName());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage("§8[§c-§8] §7" + player.getName());
    }
}
