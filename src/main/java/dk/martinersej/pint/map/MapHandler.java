package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.WorldEditUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MapHandler extends YamlManagerTypeImpl {

    private final Map<String, GameMap> maps = new HashMap<>();

    public MapHandler() {
        super(Pint.getInstance(), "maps.yml");
        load();
    }

    public void loadMaps() {
        for (String mapName : getConfig().getKeys(false)) {

        }
    }

    public boolean mapExists(String id) {
        return maps.get(id) != null;
    }

    private ConfigurationSection getMapSection(String mapID) {
        return getConfig().getConfigurationSection("maps." + mapID);
    }

    public void createMap(String mapID) {

        getConfig().createSection("maps." + mapID);
        ConfigurationSection section = getConfig().getConfigurationSection("maps." + mapID);

        section.createSection("spawnpoints");

        save();
        maps.put(mapID, new GameMap(mapID));
    }

    public void saveMapSchematic(String id, Location corner1, Location corner2) {
        ConfigurationSection section = getMapSection(id);

        String realZeroLocation = LocationUtil.locationToString(corner1);
        String pos1Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner1));
        String pos2Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner2));
        section.set("zeroLocation", realZeroLocation);
        section.set("corner1", pos1Location);
        section.set("corner2", pos2Location);
        save();

        String schematicPath = Pint.getInstance().getDataFolder() + "/maps/" + id + ".schematic";
        WorldEditUtil.createSchematic(schematicPath, corner1, corner2);
    }

    public void addSpawnPoint(String mapID, String pointID, Location location) {
        ConfigurationSection section = getMapSection(mapID);

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("realZeroLocation"));
        org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, location);
        section.set("spawnpoints." + pointID, LocationUtil.vectorToString(offset));

        save();
    }

    public void deleteSpawnPoint(String mapID, String pointID) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("spawnpoints." + pointID, null);

        save();
    }
}
