package dk.martinersej.pint.warp;

import dk.martinersej.pint.utils.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class WarpUtil {

    private final WarpHandler warpHandler;

    public WarpUtil(WarpHandler warpHandler) {
        this.warpHandler = warpHandler;
    }

    public void setWarp(String warpID, Location location) {
        String locationString = LocationUtil.locationToString(location);
        warpHandler.getConfig().set(WarpHandler.getWarpSection() + "." + warpID, locationString);
        warpHandler.save();
    }

    public void deleteWarp(String warpID) {
        warpHandler.getConfig().set(WarpHandler.getWarpSection() + "." + warpID, null);
        warpHandler.save();
    }

    public boolean doesWarpExist(String warpID) {
        return warpHandler.getConfig().contains(WarpHandler.getWarpSection() + "." + warpID);
    }

    public Location getWarpLocation(String warpID) {
        if (!warpHandler.getConfig().contains(WarpHandler.getWarpSection() + "." + warpID))
            return null;
        return LocationUtil.stringToLocation(warpHandler.getConfig().getString(WarpHandler.getWarpSection() + "." + warpID));
    }

    public List<String> getAllWarps() {
        ConfigurationSection section = warpHandler.getConfig().getConfigurationSection(WarpHandler.getWarpSection());
        if (section == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(section.getKeys(false));
    }
}
