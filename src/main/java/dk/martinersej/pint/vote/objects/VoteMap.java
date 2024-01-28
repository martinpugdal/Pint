package dk.martinersej.pint.vote.objects;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.Map;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class VoteMap extends Map {

//    private org.bukkit.util.Vector corner1;
//    private org.bukkit.util.Vector corner2;
//    private Location zeroLocation;

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

        // check if zero location is set
        if (section.getString("zeroLocation") == null) {
            Bukkit.getLogger().warning("ZeroLocation is null for map votemap");
            return;
        }
        // Load zero location
        setZeroLocation(LocationUtil.stringToLocation(section.getString("zeroLocation")));
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
        int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
        Location location = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation().clone().add(0, yLevel, 0);
        SchematicUtil.pasteSchematic(schematic, location, false);
    }

    public void clearSchematic() {
        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner1());
        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner2());

        Vector pos1 = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
        Vector pos2 = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());

        World world = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld();
        CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(world), pos1, pos2);
        Schematic schematic = new Schematic(region);

        int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
        Location location = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation().clone().add(0, yLevel, 0);

        SchematicUtil.pasteSchematic(schematic, location, true);
    }

    public Location getCenterLocation() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());
        return LocationUtil.getCenterLocation(corner1Location, corner2Location);
    }
}