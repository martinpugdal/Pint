package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

@Getter
@Setter
public abstract class Map {

    private org.bukkit.util.Vector corner1;
    private org.bukkit.util.Vector corner2;
    private Location zeroLocation;

    public Map() {

    }

    public abstract void load();

    public boolean isPresent() {
        return corner1 != null && corner2 != null && zeroLocation != null;
    }

    public Location getCenterLocation() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());
        return LocationUtil.getCenterLocation(corner1Location, corner2Location);
    }
}
