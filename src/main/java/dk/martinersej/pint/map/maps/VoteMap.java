package dk.martinersej.pint.map.maps;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.Map;
import dk.martinersej.pint.utils.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class VoteMap extends Map {

    private Vector spawnPoint;
    private float yaw;
    private float pitch;

    public VoteMap() {
        super();
    }

    @Override
    public void load() {
        ConfigurationSection section = Pint.getInstance().getVoteHandler().getVoteUtil().getVoteMapSection();

        // check if corners are set
        if (section.getString("corner1") == null || section.getString("corner2") == null) {
            Bukkit.getLogger().warning("Corner1 or corner2 is null for map votemap");
            return;
        }
        // Load corners
        setCorner1(LocationUtil.stringToVector(section.getString("corner1")));
        setCorner2(LocationUtil.stringToVector(section.getString("corner2")));

        // check if zeroLocation is set
        if (section.getString("zeroLocation") == null) {
            Bukkit.getLogger().warning("ZeroLocation is null for map votemap");
            return;
        }
        // Load zeroLocation
        setZeroLocation(LocationUtil.stringToLocation(section.getString("zeroLocation")));

        // Load spawnpoint
        ConfigurationSection spawnPointSection = section.getConfigurationSection("spawnpoint");
        String spawnPointString = null;
        if (spawnPointSection != null && spawnPointSection.getString("coords") != null) {
            spawnPointString = spawnPointSection.getString("coords");
        }

        //int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
        if (spawnPointString == null) {
            Location centerLocation = getCenterLocation();
            this.spawnPoint = new Vector(centerLocation.getX() + 0.5, centerLocation.getY() + 1, centerLocation.getZ() + 0.5);
        } else {
            this.spawnPoint = LocationUtil.stringToVector(spawnPointString);
            //spawnPoint.setY(spawnPoint.getY() + yLevel);
        }

        // Load yaw and pitch
        this.yaw = (float) section.getDouble("spawnpoint.yaw", 0f);
        this.pitch = (float) section.getDouble("spawnpoint.pitch", 0f);
    }

    protected String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + "votemap" + ".schematic";
    }

    public Location getSpawnLocation() {
        int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
//        if (spawnPoint == null) {
//            return getCenterLocation().add(0, 1, 0);
////            return Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation().clone().add(0.5, yLevel + 1, 0.5);
//        }
        Location spawnLocation = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(spawnPoint);
        spawnLocation.setYaw(yaw);
        spawnLocation.setPitch(pitch);
        spawnLocation.add(0, yLevel, 0);
        return spawnLocation;
    }
}