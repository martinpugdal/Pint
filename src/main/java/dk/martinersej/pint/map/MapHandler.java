package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;
import dk.martinersej.pint.map.maps.GameMap;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class MapHandler extends YamlManagerTypeImpl {

    @Getter
    private final static String mapSection = "maps";
    @Getter
    private final static String voteMapSection = "votemaps";
    @Getter
    private final Map<Integer, GameMap> maps = new HashMap<>();
    @Getter
    private final MapUtil mapUtil;
    private int idMapCounter = 0;

    public MapHandler() {
        super(Pint.getInstance(), "maps.yml");
        if (getConfig().getConfigurationSection(mapSection) == null) {
            getConfig().createSection(mapSection);
            save();
        }
        if (getConfig().getConfigurationSection(voteMapSection) == null) {
            getConfig().createSection(voteMapSection);
            save();
        }
        this.mapUtil = new MapUtil(this);
    }

    public void loadMaps() {
        ConfigurationSection section = mapUtil.getMapSection();
        for (String mapID : section.getKeys(false)) {
            int id = Integer.parseInt(mapID);
            GameMap map = new GameMap(id);
            maps.put(id, map);
            map.load();
        }
        mapUtil.updateHighestYLevel();
    }

    private int getNewMapID() {
        if (idMapCounter == 0) {
            for (String mapID : getConfig().getConfigurationSection(mapSection).getKeys(false)) {
                int id = Integer.parseInt(mapID);
                if (id > idMapCounter) {
                    idMapCounter = id;
                }
            }
        }
        return ++idMapCounter;
    }

    private int getNewSpawnPointID(int mapID) {
        ConfigurationSection spawnPointSection = mapUtil.getMapSection(mapID).getConfigurationSection("spawnpoints");
        /*
         * If the spawnpoints section is null, then the first spawnpointID is 1
         */
        if (spawnPointSection == null) {
            return 1;
        }
        /*
         * Find the first spawnpointID that is not taken by a spawnpoint in the map
         * If the spawnpoints are 1, 2, 3, 5, 6, 7, 8, 9, 10, 11, 12
         * Then the first spawnpointID that is not taken is 4
         * ----------------------------------
         * Else if the spawnpoints are 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
         * Then the first spawnpointID that is not taken is 11
         */
        int lastID = 0;
        for (String spawnPointID : spawnPointSection.getKeys(false)) {
            int id = Integer.parseInt(spawnPointID);
            if (id - 1 != lastID) {
                return id;
            } else {
                lastID = id;
            }
        }
        /*
         * return the lastID + 1
         * If the spawnpoints are 1, 2, 3, 4, 5, 6, 7, 8, 9, 10
         * Then the lastID is 10
         * So the next spawnpointID is 11
         */
        return lastID + 1;
    }

    public GameMap getMap(int id) {
        return maps.getOrDefault(id, null);
    }

    public boolean mapIsPresent(int id) {
        return maps.get(id) != null;
    }

    public int createMap(String gameName, Location corner1, Location corner2) {
        int mapID = getNewMapID();
        getConfig().createSection("maps." + mapID);
        ConfigurationSection section = mapUtil.getMapSection(mapID);

        section.set("gameName", gameName);
        section.createSection("spawnpoints");
        save();

        maps.put(mapID, new GameMap(mapID));

        saveMapSchematic(mapID, corner1, corner2);

        return mapID;
    }

    public void saveMapSchematic(int id, Location corner1, Location corner2) {
        ConfigurationSection section = mapUtil.getMapSection(id);

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
            maps.get(id).load();
            mapUtil.updateHighestYLevel();
        }
    }

    public void setActive(int mapID, boolean active) {
        ConfigurationSection section = getMapUtil().getMapSection(mapID);
        section.set("active", active);
        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void setMinPlayers(int mapID, int minPlayers) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        section.set("minPlayers", minPlayers);
        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void setMaxPlayers(int mapID, int maxPlayers) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        section.set("maxPlayers", maxPlayers);
        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public int addSpawnPoint(int mapID, Location location) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        int spawnPointID = getNewSpawnPointID(mapID);

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
        org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, location);
        section.set("spawnpoints." + spawnPointID + ".coords", LocationUtil.vectorToString(offset));
        section.set("spawnpoints." + spawnPointID + ".yaw", location.getYaw());
        section.set("spawnpoints." + spawnPointID + ".pitch", location.getPitch());

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
        return spawnPointID;
    }

    public void deleteSpawnPoint(int mapID, int pointID) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        section.set("spawnpoints." + pointID, null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void clearSpawnPoints(int mapID) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        section.set("spawnpoints", null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }
}
