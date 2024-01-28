package dk.martinersej.pint.vote.objects;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class VoteMap {

    private org.bukkit.util.Vector corner1;
    private org.bukkit.util.Vector corner2;
    private Location zeroLocation;

    public VoteMap() {
    }

    public boolean isPresent() {
        return corner1 != null && corner2 != null && zeroLocation != null;
    }

    public void load() {
        ConfigurationSection section = Pint.getInstance().getVoteHandler().getVoteUtil().getVoteMapSection();

        // check if corners are set
        if (section.getString("corner1") == null || section.getString("corner2") == null) {
            Bukkit.getLogger().warning("Corner1 or corner2 is null for map votemap");
            return;
        }
        // Load corners
        this.corner1 = LocationUtil.stringToVector(section.getString("corner1"));
        this.corner2 = LocationUtil.stringToVector(section.getString("corner2"));

        // check if zero location is set
        if (section.getString("zeroLocation") == null) {
            Bukkit.getLogger().warning("ZeroLocation is null for map votemap");
            return;
        }
        // Load zero location
        this.zeroLocation = LocationUtil.stringToLocation(section.getString("zeroLocation"));
    }

    private String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + "votemap" + ".schematic";
    }

    public void pasteSchematic() {
        File schematicFile = new File(getSchematicPath());
        Schematic schematic = SchematicUtil.loadSchematic(schematicFile, FastAsyncWorldEditUtil.getWEWorld(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld()));
        if (schematic == null) {
            Bukkit.getLogger().warning("Schematic is null for map votemap");
            return;
        }
        SchematicUtil.pasteSchematic(schematic, Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation(), false);
    }

    public void clearSchematic() {
        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.corner1);
        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.corner2);
        Vector pos1 = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
        Vector pos2 = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());
        CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(this.zeroLocation.getWorld()), pos1, pos2);
        SchematicUtil.setToAir(region, this.zeroLocation.getWorld());
    }

    public Location getCenterLocation() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(corner1);
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(corner2);
        return LocationUtil.getCenterLocation(corner1Location, corner2Location);
    }
}