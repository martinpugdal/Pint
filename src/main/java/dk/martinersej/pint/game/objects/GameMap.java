package dk.martinersej.pint.game.objects;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMap {

    private final int id;

    private String gameName;
    private boolean active;

    private Vector corner1;
    private Vector corner2;
    private List<Vector> spawnPoints;
    private Location zeroLocation;

    private int minPlayers;
    private int maxPlayers;

    public GameMap(int id) {
        this.id = id;
    }

    public void load() {
        ConfigurationSection section = Pint.getInstance().getMapHandler().getMapUtil().getMapSection(id);

        // Load game name
        this.gameName = section.getString("gameName");

        // Load active
        this.active = section.getBoolean("active", false);

        // Load corners
        this.corner1 = LocationUtil.stringToVector(section.getString("corner1"));
        this.corner2 = LocationUtil.stringToVector(section.getString("corner2"));

        // Load min and max players
        this.minPlayers = section.getInt("minPlayers", 0);
        this.maxPlayers = section.getInt("maxPlayers", 16);

        // Load zero location
        this.zeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));

        // Load spawnpoints
        this.spawnPoints = new ArrayList<>();
        for (String key : section.getConfigurationSection("spawnpoints").getKeys(false)) {
            spawnPoints.add(LocationUtil.stringToVector(section.getString("spawnpoints." + key)));
        }
    }

    private String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + id + ".schematic";
    }

    public void pasteSchematic() {
        File schematicFile = new File(getSchematicPath());
        Schematic schematic = SchematicUtil.loadSchematic(schematicFile, FastAsyncWorldEditUtil.getWEWorld(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld()));
        if (schematic == null) {
            Bukkit.getLogger().warning("Schematic is null for map " + id);
            return;
        }
        SchematicUtil.pasteSchematic(schematic, getCenterLocation(), false);
    }

    public void clearSchematic() {
        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.corner1);
        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.corner2);
        com.sk89q.worldedit.Vector pos1 = new com.sk89q.worldedit.Vector(corner1.getX(), corner1.getY(), corner1.getZ());
        com.sk89q.worldedit.Vector pos2 = new com.sk89q.worldedit.Vector(corner2.getX(), corner2.getY(), corner2.getZ());
        CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(this.zeroLocation.getWorld()), pos1, pos2);
        SchematicUtil.setToAir(region, this.zeroLocation.getWorld());
    }

    public int getHighestYLevel() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(corner1);
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(corner2);
        return Math.max(corner2Location.getBlockX(), corner1Location.getBlockX());
    }

    public Location getCenterLocation() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(corner1);
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(corner2);
        Location center = LocationUtil.getCenterLocation(corner1Location, corner2Location);
        return center.add(0.5, 0, 0.5);
    }
}
