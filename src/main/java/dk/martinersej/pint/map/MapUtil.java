package dk.martinersej.pint.map;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
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

    public World getGameWorld() {
        return serverWorld.getWorld();
    }

    public Location getCurrentGameMapCorner1() {
        Location location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();
        location.setY(0);

        GameMap currentMap = Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap();

        org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(currentMap.getCenterLocation(), new Location(serverWorld.getWorld(), currentMap.getCorner1().getX(), currentMap.getCorner1().getY(), currentMap.getCorner1().getZ()));
        location.add(vectorOffset);

        return location.clone();
    }

    public Location getCurrentGameMapCorner2() {
        Location location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();
        location.setY(0);

        GameMap currentMap = Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap();

        org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(currentMap.getCenterLocation(), new Location(serverWorld.getWorld(), currentMap.getCorner2().getX(), currentMap.getCorner2().getY(), currentMap.getCorner2().getZ()));
        location.add(vectorOffset);

        return location.clone();
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
