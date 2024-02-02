package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.map.maps.VoteMap;
import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class VoteUtil {

    public void setToVoteGamemode(Player player) {
        player.getInventory().clear();
        player.getInventory().setHelmet(new ItemStack(Material.AIR));
        player.getInventory().setChestplate(new ItemStack(Material.AIR));
        player.getInventory().setLeggings(new ItemStack(Material.AIR));
        player.getInventory().setBoots(new ItemStack(Material.AIR));
        player.updateInventory();

        player.getActivePotionEffects().clear();
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0, true, false));

        player.setHealth(20);
        player.setFoodLevel(20);

        player.setGameMode(GameMode.ADVENTURE);

        player.setAllowFlight(true);
        player.setFlying(true);

        player.teleport(spawnLocation());

        //vote item
        player.setCompassTarget(spawnLocation());
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
        return Pint.getInstance().getVoteHandler().getVoteMap().getSpawnLocation();
    }

    public ConfigurationSection getVoteMapSection() {
        return Pint.getInstance().getMapHandler().getConfig().getConfigurationSection(MapHandler.getVoteMapSection());
    }

    public void saveMapSchematic(Location corner1, Location corner2) {
        MapHandler mapHandler = Pint.getInstance().getMapHandler();
        ConfigurationSection section = getVoteMapSection();

        String realZeroLocation = LocationUtil.locationToString(corner1);
        String pos1Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner1));
        String pos2Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner2));
        section.set("zeroLocation", realZeroLocation);
        section.set("corner1", pos1Location);
        section.set("corner2", pos2Location);
        mapHandler.save();

        String schematicPath = Pint.getInstance().getDataFolder() + "/maps/" + "votemap" + ".schematic";
        SchematicUtil.createSchematic(schematicPath, corner1, corner2);
    }

    public void removeMapSchematic() {
        ConfigurationSection section = getVoteMapSection();
        section.set("zeroLocation", null);
        section.set("corner1", null);
        section.set("corner2", null);
        Pint.getInstance().getMapHandler().save();
        deleteSpawnPoint();
    }

    public void setSpawnPoint(Location location, boolean yaw, boolean pitch) {
        ConfigurationSection section = getVoteMapSection();

        VoteMap voteMap = Pint.getInstance().getVoteHandler().getVoteMap();

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
        org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, location);

        section.set("spawnpoint.coords", LocationUtil.vectorToString(offset));
        if (yaw) section.set("spawnpoint.yaw", location.getYaw()); else section.set("spawnpoint.yaw", null);
        if (pitch) section.set("spawnpoint.pitch", location.getPitch()); else section.set("spawnpoint.pitch", null);

        Pint.getInstance().getMapHandler().save();

        voteMap.load();
    }

    public void deleteSpawnPoint() {
        ConfigurationSection section = getVoteMapSection();

        VoteMap voteMap = Pint.getInstance().getVoteHandler().getVoteMap();

        section.set("spawnpoint", null);

        Pint.getInstance().getMapHandler().save();

        voteMap.load();
    }
}
