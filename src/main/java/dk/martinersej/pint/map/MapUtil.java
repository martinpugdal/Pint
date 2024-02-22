package dk.martinersej.pint.map;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.map.objects.maps.GameMap;
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

    public Location calculateSpawnLocationWithVoteMap(SpawnPoint spawnPoint, GameMap gameMap) {
        Location location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();

        org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(gameMap.getCenterLocation(), new Location(serverWorld.getWorld(), gameMap.getCorner1().getX(), gameMap.getCorner1().getY(), gameMap.getCorner1().getZ()));
        location.add(vectorOffset);
        location.setY(0);

        location.setPitch(spawnPoint.getPitch());
        location.setYaw(spawnPoint.getYaw());
        return location.add(spawnPoint.getVector());
    }

    public Region calculateRegionWithVoteMap(Region region, GameMap gameMap) {
        Location location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();

        org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(gameMap.getCenterLocation(), new Location(serverWorld.getWorld(), gameMap.getCorner1().getX(), gameMap.getCorner1().getY(), gameMap.getCorner1().getZ()));
        location.add(vectorOffset);
        location.setY(0);

        region = region.clone();
        try {
            Vector locVector = location.toVector();
            region.shift(new com.sk89q.worldedit.Vector(locVector.getX(), locVector.getY(), locVector.getZ()));
        } catch (RegionOperationException e) {
            e.printStackTrace();
        }

        return region;
    }

    public void updateHighestYLevel() {
        Collection<GameMap> gameMaps = mapHandler.getMaps().values();
        for (GameMap gameMap : gameMaps) {
            int yLevel = gameMap.getHighestYLevel();
            if (yLevel > highestYLevel) {
                if (Pint.getInstance().getVoteHandler().getVoteMap().isPresent()) {
                    Pint.getInstance().getVoteHandler().getVoteMap().clearSchematic();
                }
                highestYLevel = yLevel;
                if (Pint.getInstance().getVoteHandler().getVoteMap().isPresent()) {
                    Pint.getInstance().getVoteHandler().getVoteMap().pasteSchematic();
                }
            }
        }
    }
}
