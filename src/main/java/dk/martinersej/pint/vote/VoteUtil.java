package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VoteUtil {

    public void setToVoteGamemode(Player player) {
        voteSpawn(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.getActivePotionEffects().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setAllowFlight(true);

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

    public void voteSpawn(Player player) {
        int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
        Location centerLocation = Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap().getCenterLocation().clone();
        centerLocation.setY(yLevel + 1);
        player.teleport(centerLocation);
    }
}
