package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteListener implements Listener {


    public VoteListener() {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(getClass().getPackage().getName().split("^\\w+")[0]);
    }


    @EventHandler
    public void onVoteItemClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null) {
                String vote = ItemBuilder.getNbt(event.getItem(), "vote");
                if (vote != null) {
                    new VoteGUI().open(event.getPlayer());
                }
            }
        }
    }
}
