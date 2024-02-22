package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;
import dk.martinersej.pint.map.objects.maps.GameMap;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
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
        if (spawnPointSection == null) {
            return 1;
        }
        int lastID = 0;
        for (String spawnPointID : spawnPointSection.getKeys(false)) {
            int id = Integer.parseInt(spawnPointID);
            if (id - 1 != lastID) {
                return id;
            } else {
                lastID = id;
            }
        }
        return lastID + 1;
    }

    private int getNewRegionID(int mapID) {
        ConfigurationSection regionSection = mapUtil.getMapSection(mapID).getConfigurationSection("regions");
        if (regionSection == null) {
            return 1;
        }
        int lastID = 0;
        for (String regionID : regionSection.getKeys(false)) {
            int id = Integer.parseInt(regionID);
            if (id - 1 != lastID) {
                return id;
            } else {
                lastID = id;
            }
        }
        return lastID + 1;
    }

    public GameMap getMap(int id) {
        return maps.getOrDefault(id, null);
    }

    public boolean mapIsPresent(int id) {
        return maps.get(id) != null;
    }

    public int createMap(Game game, Location corner1, Location corner2) {
        int mapID = getNewMapID();
        getConfig().createSection("maps." + mapID);
        ConfigurationSection section = mapUtil.getMapSection(mapID);

        section.set("gameID", game.getId());
        section.set("gameName", game.getGameInformation().getName());
        section.createSection("spawnpoints");
        save();

        maps.put(mapID, new GameMap(mapID));

        saveMapSchematic(mapID, corner1, corner2);

        return mapID;
    }

    public void deleteMap(int mapID) {
        getConfig().set("maps." + mapID, null);
        save();

        String schematicPath = Pint.getInstance().getDataFolder() + "/maps/" + mapID + ".schematic";
        SchematicUtil.deleteSchematic(schematicPath);

        GameMap deletedMap = maps.remove(mapID);
        for (Game game : Pint.getInstance().getGameHandler().getGames()) {
            game.getGameMaps().remove(deletedMap);
        }
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
            if (!active) {
                Game game = Pint.getInstance().getGameHandler().getGame(maps.get(mapID).getGameID());
                checkEnoughMapsAndShuffleVotePool(game);
            }
        }
    }

    private void checkEnoughMapsAndShuffleVotePool(Game game) {
        if (game.getActiveMaps().isEmpty()) {
            // no maps are active for the game,
            // so remove the game from the pool
            // and shuffle the vote pool if the game is in the vote pool
            Pint.getInstance().getGameHandler().getGamePool().removeGame(game);
            for (Game voteGame : Pint.getInstance().getGameHandler().getGamePool().getVoteGames()) {
                if (voteGame != null && voteGame.equals(game)) {
                    Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
                    break;
                }
            }
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

    public int addSpawnPoint(int mapID, Location location, boolean yaw, boolean pitch) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        int spawnPointID = getNewSpawnPointID(mapID);

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
        org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, location);
        section.set("spawnpoints." + spawnPointID + ".coords", LocationUtil.vectorToString(offset));

        if (yaw) section.set("spawnpoints." + spawnPointID + ".yaw", location.getYaw());
        else section.set("spawnpoints." + spawnPointID + ".yaw", 0);

        if (pitch) section.set("spawnpoints." + spawnPointID + ".pitch", location.getPitch());
        else section.set("spawnpoints." + spawnPointID + ".pitch", 0);

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

    public int addRegion(int mapID, Location corner1, Location corner2) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        int regionID = getNewRegionID(mapID);

        Location realZeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
        org.bukkit.util.Vector offset1 = LocationUtil.getVectorOffset(realZeroLocation, corner1);
        org.bukkit.util.Vector offset2 = LocationUtil.getVectorOffset(realZeroLocation, corner2);

        Bukkit.getLogger().info("offset1: " + offset1);
        Bukkit.getLogger().info("offset2: " + offset2);

        section.set("regions." + regionID + ".corner1", LocationUtil.vectorToString(offset1));
        section.set("regions." + regionID + ".corner2", LocationUtil.vectorToString(offset2));

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }

        return regionID;
    }

    public void deleteRegion(int mapID, int regionID) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        section.set("regions." + regionID, null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }

    public void clearRegions(int mapID) {
        ConfigurationSection section = mapUtil.getMapSection(mapID);
        section.set("regions", null);

        save();

        if (maps.containsKey(mapID)) {
            maps.get(mapID).load();
        }
    }
}
