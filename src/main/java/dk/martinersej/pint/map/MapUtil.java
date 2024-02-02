package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.maps.GameMap;
import dk.martinersej.pint.map.maps.SpawnPoint;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.util.Collection;

@Getter
public class MapUtil {

    private final MapHandler mapHandler;
    private final ServerWorld serverWorld;
    private int highestYLevel = 0;

    public MapUtil(MapHandler mapHandler) {
        this.serverWorld = new ServerWorld();
        this.mapHandler = mapHandler;
    }

    public ConfigurationSection getMapSection() {
        return mapHandler.getConfig().getConfigurationSection("maps");
    }

    public ConfigurationSection getMapSection(int mapID) {
        return mapHandler.getConfig().getConfigurationSection("maps." + mapID);
    }

    public GameMap getCurrentMap() {
        return Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap();
    }

    public Location getCurrentCorner1() {
        return getLocationFromOffset(getCurrentMap().getCorner1().clone());
    }

    public Location getCurrentCorner2() {
        return getLocationFromOffset(getCurrentMap().getCorner2().clone());
    }

    public Location getLocationFromOffset(Vector offset) {
        Location location = serverWorld.getZeroLocation();
        return location.add(offset);
    }

    public Location getLocationFromOffsetWithVoteMap(SpawnPoint spawnPoint, GameMap gameMap) {
        Location location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();

        org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(gameMap.getCenterLocation(), new Location(location.getWorld(), gameMap.getCorner1().getX(), gameMap.getCorner1().getY(), gameMap.getCorner1().getZ()));
        location.add(vectorOffset);
        location.setY(0);

        location.setPitch(spawnPoint.getPitch());
        location.setYaw(spawnPoint.getYaw());
        return location.add(spawnPoint.getVector());
    }

    public void updateHighestYLevel() {
        Collection<GameMap> gameMaps = mapHandler.getMaps().values();
        for (GameMap gameMap : gameMaps) {
            int yLevel = gameMap.getHighestYLevel();
            if (yLevel > highestYLevel) {
                highestYLevel = yLevel;
            }
        }
    }
}
