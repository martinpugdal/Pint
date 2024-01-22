package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MapHandler extends YamlManagerTypeImpl {

    @Getter
    private final Map<Integer, GameMap> maps = new HashMap<>();
    private int idCounter = 0;

    public MapHandler() {
        super(Pint.getInstance(), "maps.yml");
        load();
        loadMaps();
    }

    public void loadMaps() {
        ConfigurationSection section = getConfig().getConfigurationSection("maps");

        for (String mapID : section.getKeys(false)) {
            maps.put(Integer.valueOf(mapID), new GameMap(mapID));
        }
    }

    private int getNewMapID() {
        if (idCounter == 0) {
            for (String mapID : getConfig().getConfigurationSection("maps").getKeys(false)) {
                int id = Integer.parseInt(mapID);
                if (id > idCounter) {
                    idCounter = id;
                }
            }
        }
        return ++idCounter;
    }

    public GameMap getMap(int id) {
        return maps.getOrDefault(id, null);
    }

    public boolean mapExists(int id) {
        return maps.get(id) != null;
    }

    private ConfigurationSection getMapSection(int mapID) {
        return getConfig().getConfigurationSection("maps." + mapID);
    }

    public int createMap() {
        int mapID = getNewMapID();
        getConfig().createSection("maps." + mapID);
        ConfigurationSection section = getConfig().getConfigurationSection("maps." + mapID);

        section.createSection("spawnpoints");

        save();
        maps.put(mapID, new GameMap(mapID));

        return mapID;
    }

    public void saveMapSchematic(int id, Location corner1, Location corner2) {
        ConfigurationSection section = getMapSection(id);

        String realZeroLocation = LocationUtil.locationToString(corner1);
        String pos1Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner1));
        String pos2Location = LocationUtil.vectorToString(LocationUtil.getVectorOffset(corner1, corner2));
        section.set("zeroLocation", realZeroLocation);
        section.set("corner1", pos1Location);
        section.set("corner2", pos2Location);
        save();

        String schematicPath = Pint.getInstance().getDataFolder() + "/maps/" + id + ".schematic";
        FastAsyncWorldEditUtil.createSchematic(schematicPath, corner1, corner2);

        if (maps.containsKey(id)) {
            maps.get(id).load();
        }
    }

    public void addSpawnPoint(int mapID, String pointID, Location location) {
        ConfigurationSection section = getMapSection(mapID);

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("realZeroLocation"));
        org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, location);
        section.set("spawnpoints." + pointID, LocationUtil.vectorToString(offset));

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void deleteSpawnPoint(int mapID, String pointID) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("spawnpoints." + pointID, null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }
}
