package dk.martinersej.pint.listener.listeners.global;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Chat implements Listener {
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        message = message.replaceAll("%", "%%");

        String prefix = (player.isOp() ? "§6" : "§e");
        event.setFormat(prefix + player.getName() + "§8: " + prefix + message);

    }
}
