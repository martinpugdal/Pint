package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import org.bukkit.Location;

public class MapUtil {


    public MapUtil() {

    }


    public Location getZeroLocation() {
        Pint.getInstance().getGameHandler()
        return new Location(null, 0, 0, 0);
    }
}
