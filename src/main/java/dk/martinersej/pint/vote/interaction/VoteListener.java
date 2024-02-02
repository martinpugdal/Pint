package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class VoteListener implements Listener {

    public VoteListener() {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVoteItemClick(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (event.getItem() != null && event.getItem().getType().equals(Material.COMPASS)) {
                String vote = ItemBuilder.getNbt(event.getItem(), "vote");
                if (vote != null) {
                    new VoteGUI(event.getPlayer().getUniqueId()).open(event.getPlayer());
                }
            }
        }
    }

    @EventHandler
    public void onVoteItemDrop(PlayerDropItemEvent event) {
        if (event.getItemDrop().getType().equals(EntityType.DROPPED_ITEM)) {
            ItemStack itemStack = event.getItemDrop().getItemStack();
            if (itemStack.getType().equals(Material.COMPASS)) {
                String vote = ItemBuilder.getNbt(itemStack, "vote");
                if (vote != null) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(event.getPlayer());
            }
        }.runTaskLater(Pint.getInstance(), 1L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Pint.getInstance().getVoteHandler().setVote(event.getPlayer().getUniqueId(), null);
    }
}
