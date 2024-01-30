package dk.martinersej.pint.map.maps;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.Map;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

@Getter
public class VoteMap extends Map {

    private Vector spawnPoint;

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

        // Load spawnpoint
        String spawnPointString = section.getString("spawnpoint");
        if (spawnPointString == null) {
            Location realZeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
            org.bukkit.util.Vector offset = LocationUtil.getVectorOffset(realZeroLocation, getCenterLocation());
            section.set("spawnpoint", LocationUtil.vectorToString(offset));
        } else {
            this.spawnPoint = LocationUtil.stringToVector(spawnPointString);
        }
    }

    protected String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + "votemap" + ".schematic";
    }

    public Location getSpawnLocation() {
        return Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(spawnPoint);
    }
}