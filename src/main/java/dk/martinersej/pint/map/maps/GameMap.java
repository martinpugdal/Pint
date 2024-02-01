package dk.martinersej.pint.map.maps;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.Map;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMap extends Map {

    private final int id;

    private String gameName;
    private boolean active;

    private List<Vector> spawnPoints;
    private List<YawPitch> yawPitches;

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
            Bukkit.getLogger().warning("Corner1 or corner2 is null for map with id " + id);
            return;
        }
        // Load corners
        setCorner1(LocationUtil.stringToVector(section.getString("corner1")));
        setCorner2(LocationUtil.stringToVector(section.getString("corner2")));

        // check if zero location is set
        if (section.getString("zeroLocation") == null) {
            Bukkit.getLogger().warning("ZeroLocation is null for map with id " + id);
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
        this.yawPitches = new ArrayList<>();
        ConfigurationSection spawnPointsSection = section.getConfigurationSection("spawnpoints");
        if (spawnPointsSection != null) {
            for (String key : spawnPointsSection.getKeys(false)) {
                spawnPoints.add(LocationUtil.stringToVector(section.getString("spawnpoints." + key + ".coords")));
                yawPitches.add(new YawPitch(
                        (float) section.getDouble("spawnpoints." + key + ".yaw", 0f),
                        (float) section.getDouble("spawnpoints." + key + ".pitch", 0f)
                ));
            }
        }
    }

    protected String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + id + ".schematic";
    }

    public int getHighestYLevel() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());
        return Math.max(corner2Location.getBlockX(), corner1Location.getBlockX());
    }

    public Location getSpawnPoint(int index) {
        Location spawnPointLocation = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffsetWithVoteMap(spawnPoints.get(index), this);
        spawnPointLocation.setYaw(yawPitches.get(index).getYaw());
        spawnPointLocation.setPitch(yawPitches.get(index).getPitch());
        return spawnPointLocation;
    }
}
