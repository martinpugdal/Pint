package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MapHandler extends YamlManagerTypeImpl {

    @Getter
    private final Map<Integer, GameMap> maps = new HashMap<>();
    @Getter
    private final MapUtil mapUtil;
    private int idMapCounter = 0;

    public MapHandler() {
        super(Pint.getInstance(), "maps.yml");
//        load();
        if (getConfig().getConfigurationSection("maps") == null) {
            getConfig().createSection("maps");
            save();
        }
        this.mapUtil = new MapUtil();
        loadMaps();

    }

    private void loadMaps() {
        ConfigurationSection section = getConfig().getConfigurationSection("maps");

        for (String mapID : section.getKeys(false)) {
            int id = Integer.parseInt(mapID);
            maps.put(id, new GameMap(id));
        }
    }

    private int getNewMapID() {
        if (idMapCounter == 0) {
            for (String mapID : getConfig().getConfigurationSection("maps").getKeys(false)) {
                int id = Integer.parseInt(mapID);
                if (id > idMapCounter) {
                    idMapCounter = id;
                }
            }
        }
        return ++idMapCounter;
    }

    private int getNewSpawnPointID(int mapID) {
        int idCounter = getMap(mapID).getSpawnPoints().size();
        for (String spawnPointID : getConfig().getConfigurationSection("spawnpoints").getKeys(false)) {
            int id = Integer.parseInt(spawnPointID);
            if (id > idCounter) {
                idCounter = id;
            }
        }
        return ++idCounter;
    }

    public GameMap getMap(int id) {
        return maps.getOrDefault(id, null);
    }

    public boolean mapIsPresent(int id) {
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
        SchematicUtil.createSchematic(schematicPath, corner1, corner2);

        if (maps.containsKey(id)) {
            mapUtil.updateHighestYLevel();
            maps.get(id).load();
        }
    }

    public void setActive(int mapID, boolean active) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("active", active);
        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void setMinPlayers(int mapID, int minPlayers) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("minPlayers", minPlayers);
        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void setMaxPlayers(int mapID, int maxPlayers) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("maxPlayers", maxPlayers);
        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public int addSpawnPoint(int mapID, Location location) {
        ConfigurationSection section = getMapSection(mapID);
        int spawnPointID = getNewSpawnPointID(mapID);

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
        org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, location);
        section.set("spawnpoints." + spawnPointID, LocationUtil.vectorToString(offset));

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
        return spawnPointID;
    }

    public void deleteSpawnPoint(int mapID, int pointID) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("spawnpoints." + pointID, null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void clearSpawnPoints(int mapID) {
        ConfigurationSection section = getMapSection(mapID);
        section.set("spawnpoints", null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }
}
