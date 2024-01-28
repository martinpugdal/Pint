package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VoteUtil {

    public void setToVoteGamemode(Player player) {
        player.teleport(spawnLocation());
//        player.setGameMode(GameMode.ADVENTURE);
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
        player.setFlying(true);

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

    public Location spawnLocation() {
//        int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
//        if (Pint.getInstance().getGameHandler().getCurrentGame() == null || Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap() == null) {
//            return Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation().clone().add(0.5, yLevel + 1, 0.5);
//        }
//        Location centerLocation = Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap().getCenterLocation().clone();
//        centerLocation.setY(yLevel + 1);
//        return centerLocation;
        int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
        return Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation().clone().add(0.5, yLevel + 1, 0.5);
    }

    public ConfigurationSection getVoteMapSection() {
        return Pint.getInstance().getMapHandler().getConfig().getConfigurationSection("votemap");
    }

    public void saveMapSchematic(Location corner1, Location corner2) {
        MapHandler mapHandler = Pint.getInstance().getMapHandler();
        ConfigurationSection section = mapHandler.getConfig().getConfigurationSection("votemap");

        String realZeroLocation = LocationUtil.locationToString(corner1);
        String pos1Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner1));
        String pos2Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner2));
        section.set("zeroLocation", realZeroLocation);
        section.set("corner1", pos1Location);
        section.set("corner2", pos2Location);
        mapHandler.save();
        Pint.getInstance().getVoteHandler().loadVoteMap();

        String schematicPath = Pint.getInstance().getDataFolder() + "/maps/" + "votemap" + ".schematic";
        SchematicUtil.createSchematic(schematicPath, corner1, corner2);
    }
}
