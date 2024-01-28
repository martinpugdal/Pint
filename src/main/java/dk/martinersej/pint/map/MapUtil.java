package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.nio.Buffer;
import java.util.Collection;
import java.util.List;

@Getter
public class MapUtil {

    private final MapHandler mapHandler;
    private final ServerWorld serverWorld;
    private int highestYLevel = 0;

    public MapUtil(MapHandler mapHandler) {
        this.serverWorld = new ServerWorld();
        this.mapHandler = mapHandler;
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
