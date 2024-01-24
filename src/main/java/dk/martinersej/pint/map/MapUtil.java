package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

@Getter
public class MapUtil {

    private int highestYLevel;

    public MapUtil() {
        updateHighestYLevel();
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
        Location location = Pint.getInstance().getGameHandler().getServerWorld().getZeroLocation();
        location.add(offset);
        return location;
    }

    public void updateHighestYLevel() {
        List<GameMap> gameMaps = (List<GameMap>) Pint.getInstance().getMapHandler().getMaps().values();
        highestYLevel = Integer.MIN_VALUE;
        for (GameMap gameMap : gameMaps) {
            if (gameMap.getHighestYLevel() > highestYLevel) {
                highestYLevel = gameMap.getHighestYLevel();
            }
        }
    }
}
