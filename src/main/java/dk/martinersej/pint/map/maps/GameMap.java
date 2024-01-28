package dk.martinersej.pint.map.maps;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.regions.CuboidRegion;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.Map;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMap extends Map {

    private final int id;

    private String gameName;
    private boolean active;

    //    private Vector corner1;
//    private Vector corner2;
    private List<Vector> spawnPoints;
//    private Location zeroLocation;

    private int minPlayers;
    private int maxPlayers;

    public GameMap(int id) {
        super();
        this.id = id;
    }

    @Override
    public void load() {
        ConfigurationSection section = Pint.getInstance().getMapHandler().getMapUtil().getMapSection(id);

        // Load game name
        this.gameName = section.getString("gameName");

        // Load active
        this.active = section.getBoolean("active", false);

        // check if corners are set
        if (section.getString("corner1") == null || section.getString("corner2") == null) {
            Bukkit.getLogger().warning("Corner1 or corner2 is null for map votemap");
            return;
        }
        // Load corners
        setCorner1(LocationUtil.stringToVector(section.getString("corner1")));
        setCorner2(LocationUtil.stringToVector(section.getString("corner2")));

        // check if zero location is set
        if (section.getString("zeroLocation") == null) {
            Bukkit.getLogger().warning("ZeroLocation is null for map votemap");
            return;
        }
        // Load zero location
        setZeroLocation(LocationUtil.stringToLocation(section.getString("zeroLocation")));

        // Load min players
        this.minPlayers = section.getInt("minPlayers", 0);

        // Load max players
        this.maxPlayers = section.getInt("maxPlayers", 16);

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
        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner1());
        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner2());

        com.sk89q.worldedit.Vector pos1 = new com.sk89q.worldedit.Vector(corner1.getX(), corner1.getY(), corner1.getZ());
        com.sk89q.worldedit.Vector pos2 = new com.sk89q.worldedit.Vector(corner2.getX(), corner2.getY(), corner2.getZ());

        World world = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld();
        CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(world), pos1, pos2);
        Schematic schematic = new Schematic(region);

        Location location = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation().clone();

        SchematicUtil.pasteSchematic(schematic, location, true);
    }

//    public void clearSchematic() {
//        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner1());
//        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner2());
//        com.sk89q.worldedit.Vector pos1 = new com.sk89q.worldedit.Vector(corner1.getX(), corner1.getY(), corner1.getZ());
//        com.sk89q.worldedit.Vector pos2 = new com.sk89q.worldedit.Vector(corner2.getX(), corner2.getY(), corner2.getZ());
//        CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(this.getZeroLocation().getWorld()), pos1, pos2);
//        SchematicUtil.setToAir(region, this.getZeroLocation().getWorld());
//    }

    public int getHighestYLevel() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());
        return Math.max(corner2Location.getBlockX(), corner1Location.getBlockX());
    }

//    public Location getCenterLocation() {
//        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
//        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());
//        Location center = LocationUtil.getCenterLocation(corner1Location, corner2Location);
//        return center.add(0.5, 0, 0.5);
//    }
}
