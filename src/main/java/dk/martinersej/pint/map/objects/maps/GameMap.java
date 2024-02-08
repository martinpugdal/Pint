package dk.martinersej.pint.map.objects.maps;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.Map;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMap extends Map {

    private final int mapID;

//    private String gameName;
    private int gameID;

    private boolean active;

    private List<SpawnPoint> spawnPoints;
    private List<Region> regions;

    private int minPlayers;
    private int maxPlayers;

    public GameMap(int id) {
        super();
        this.mapID = id;
    }

    @Override
    public void load() {
        ConfigurationSection section = Pint.getInstance().getMapHandler().getMapUtil().getMapSection(mapID);

        // Load game id
        this.gameID = section.getInt("gameID");

        // Load active
        this.active = section.getBoolean("active", false);

        // check if corners are set
        if (section.getString("corner1") == null || section.getString("corner2") == null) {
            Bukkit.getLogger().warning("Corner1 or corner2 is null for map with id " + mapID);
            return;
        }
        // Load corners
        setCorner1(LocationUtil.stringToVector(section.getString("corner1")));
        setCorner2(LocationUtil.stringToVector(section.getString("corner2")));

        // check if zero locations are set
        if (section.getString("zeroLocation") == null) {
            Bukkit.getLogger().warning("ZeroLocation is null for map with id " + mapID);
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
        ConfigurationSection spawnPointsSection = section.getConfigurationSection("spawnpoints");
        if (spawnPointsSection != null) {
            for (String key : spawnPointsSection.getKeys(false)) {
                spawnPoints.add(new SpawnPoint(
                        LocationUtil.stringToVector(spawnPointsSection.getString(key + ".coords")),
                        (float) spawnPointsSection.getDouble(key + ".yaw", 0f),
                        (float) spawnPointsSection.getDouble(key + ".pitch", 0f)
                ));
            }
        }

        // Load regions
        this.regions = new ArrayList<>();
        ConfigurationSection regionsSection = section.getConfigurationSection("regions");
        if (regionsSection != null) {
            for (String key : regionsSection.getKeys(false)) {
                // convert bukkit vector to worldedit vector
                org.bukkit.util.Vector corner1 = LocationUtil.stringToVector(regionsSection.getString(key + ".corner1"));
                Vector pos1 = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
                org.bukkit.util.Vector corner2 = LocationUtil.stringToVector(regionsSection.getString(key + ".corner2"));
                Vector pos2 = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());

                regions.add(
                        new CuboidRegion(
                                pos1,
                                pos2
                        )
                );
            }
        }
    }


    protected String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + mapID + ".schematic";
    }

    public int getHighestYLevel() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());
        return Math.max(corner2Location.getBlockX(), corner1Location.getBlockX());
    }

    public Location getSpawnPoint(int index) {
        if (spawnPoints.size() <= index) {
            return null;
        }
        return Pint.getInstance().getMapHandler().getMapUtil().calculateSpawnLocationWithVoteMap(spawnPoints.get(index), this);
    }

    public Region getRegion(int index) {
        if (regions.size() <= index) {
            return null;
        }
        return Pint.getInstance().getMapHandler().getMapUtil().calculateRegionWithVoteMap(regions.get(index), this);
    }

    @Override
    public String toString() {
        return mapID + " - " + gameID;
    }
}
