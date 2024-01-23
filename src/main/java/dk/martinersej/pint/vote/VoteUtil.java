package dk.martinersej.pint.vote;

import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class VoteUtil {
    
    public void setToVoteGamemode(Player player) {
        player.setGameMode(GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getEquipment().clear();
        player.setHealth(20);
        player.setFoodLevel(20);

        //vote item
        player.getInventory().setItem(4, getVoteItem());
    }

    private ItemStack getVoteItem() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.COMPASS);
        itemBuilder.setName("§aVote");
        itemBuilder.setLore("§7Click to vote for a map");
        itemBuilder.setNbt("vote", "true");
        return itemBuilder.toItemStack();
    }
}
