package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.map.objects.maps.VoteMap;
import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import dk.martinersej.pint.vote.interaction.VoteComponent;
import lombok.Getter;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

@Getter
public class VoteUtil {

    @Getter
    private final Sidebar voteScoreboard;
    private final VoteComponent voteComponent;
    private final Team voteTeam;

    public VoteUtil() {
        voteScoreboard = Pint.getScoreboardLibrary().createSidebar();
        voteComponent = new VoteComponent(voteScoreboard);
        Bukkit.getScoreboardManager().getMainScoreboard().getTeam("vote").unregister();

        voteTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam("vote");
        voteTeam.setPrefix("§e");
    }

    public void setToPlainVoteGamemode(Player player) {
        Bukkit.getScheduler().runTask(Pint.getInstance(), () -> {
            player.setGameMode(GameMode.ADVENTURE);
            player.setAllowFlight(false);
            player.setFlying(false);
        });
    }

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

        setToPlainVoteGamemode(player);

        player.teleport(spawnLocation());

        //vote item
        player.setCompassTarget(spawnLocation());
        player.getInventory().setItem(4, getVoteItem());
        //join item
        player.getInventory().setItem(8, getJoinItem());

        voteTeam.addPlayer(player);
        voteScoreboard.addPlayer(player);
    }

    private ItemStack getVoteItem() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.NETHER_STAR);
        itemBuilder.setName("§aVote");
        itemBuilder.setLore("§7Click to vote for a map");
        itemBuilder.setNbt("vote", "true");
        return itemBuilder.build();
    }

    private ItemStack getJoinItem() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.INK_SACK);
        itemBuilder.setDurability((short) 10);
        itemBuilder.setName("§aJoin");
        itemBuilder.setLore("§7Click to join the game");
        itemBuilder.setNbt("join", "true");
        return itemBuilder.build();
    }

    public void updateJoinItem(Player player) {
        ItemStack itemStack = null;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == Material.INK_SACK) {
                if (ItemBuilder.getNbt(item, "join") != null) {
                    itemStack = item;
                }
            }
        }
        updateJoinItem(itemStack, Pint.getInstance().getVoteHandler().getAllVoters().contains(player));
    }

    public void updateJoinItem(ItemStack itemStack) {
        if (itemStack == null) return;
        updateJoinItem(itemStack, itemStack.getData().getData() == 10);
    }

    private void updateJoinItem(ItemStack itemStack, boolean joined) {
        if (itemStack == null) return;
        ItemBuilder itemBuilder = new ItemBuilder(itemStack);
        itemBuilder.setName(joined ? "§cLeave" : "§aJoin");
        itemBuilder.setLore(joined ? "§7Click to leave the game" : "§7Click to join the game");
        itemBuilder.setDurability(joined ? (short) 8 : (short) 10);
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
        if (yaw) section.set("spawnpoint.yaw", location.getYaw());
        else section.set("spawnpoint.yaw", null);
        if (pitch) section.set("spawnpoint.pitch", location.getPitch());
        else section.set("spawnpoint.pitch", null);

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
