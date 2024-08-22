package dk.martinersej.pint.map.objects;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionOperationException;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import dk.martinersej.pint.map.objects.maps.VoteMap;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.SchematicUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;

@Setter
public abstract class Map {

    private org.bukkit.util.Vector corner1;
    private org.bukkit.util.Vector corner2;
    private Location zeroLocation;
    @Getter
    private boolean pasted = false;

    public Map() {
    }

    public abstract void load();

    protected abstract String getSchematicPath();

    public boolean isPresent() {
        return corner1 != null && corner2 != null && zeroLocation != null;
    }

    public Location getCenterLocation() {
        Location corner1Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner1());
        Location corner2Location = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(getCorner2());

        return LocationUtil.getCenterLocation(corner1Location, corner2Location);
    }

    public abstract Location getSpawnLocation();

    public Schematic getSchematic() {
        File schematicFile = new File(getSchematicPath());
        return SchematicUtil.loadSchematic(schematicFile, FastAsyncWorldEditUtil.getWEWorld(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld()));
    }

    public void pasteSchematic() {
        Schematic schematic = getSchematic();

        if (schematic == null) {
            String id = "votemap";
            if (this instanceof GameMap) {
                for (GameMap value : Pint.getInstance().getMapHandler().getMaps().values()) {
                    if (value.equals(this)) {
                        id = String.valueOf(value.getMapID());
                    }
                }
            }
            Bukkit.getLogger().warning("Schematic is null for map " + id);
            return;
        }

        Location location = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation();
        if (this instanceof VoteMap) {
            int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
            location.add(0, yLevel, 0);
        } else if (this instanceof GameMap) {
            location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();

            org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(getCenterLocation(), new Location(location.getWorld(), getCorner1().getX(), getCorner1().getY(), getCorner1().getZ()));

            location.add(vectorOffset);
            location.setY(0);
        }

        pasted = true;
        SchematicUtil.pasteSchematic(schematic, location, false);
    }

    public void clearSchematic() {
        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner1());
        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getLocationFromOffset(this.getCorner2());

        Vector pos1 = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
        Vector pos2 = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());

        World world = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld();
        CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(world), pos1, pos2);


        Location location = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation();
        if (this instanceof VoteMap) {
            int yLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
            location.add(0, yLevel, 0);
        } else if (this instanceof GameMap) {
            location = Pint.getInstance().getVoteHandler().getVoteMap().getCenterLocation();

            org.bukkit.util.Vector vectorOffset = LocationUtil.getVectorOffset(getCenterLocation(), new Location(location.getWorld(), getCorner1().getX(), getCorner1().getY(), getCorner1().getZ()));

            location.add(vectorOffset);
            location.setY(0);
        }

        try {
            region.shift(new Vector(location.getX(), location.getY(), location.getZ()));
        } catch (RegionOperationException e) {
            e.printStackTrace();
        }

        pasted = false;
        SchematicUtil.clearSchematic(region, world);
    }

    public java.util.Map<BlockVector, BaseBlock> getBlocks() {
        java.util.Map<BlockVector, BaseBlock> blocks = new HashMap<>();
        Schematic schematic = getSchematic();
        if (schematic == null) {
            return blocks;
        }

        Clipboard clipboard = schematic.getClipboard();
        if (clipboard == null) {
            return blocks;
        }

        for (int x = clipboard.getMinimumPoint().getBlockX(); x <= clipboard.getMaximumPoint().getBlockX(); x++) {
            for (int y = clipboard.getMinimumPoint().getBlockY(); y <= clipboard.getMaximumPoint().getBlockY(); y++) {
                for (int z = clipboard.getMinimumPoint().getBlockZ(); z <= clipboard.getMaximumPoint().getBlockZ(); z++) {
                    BlockVector pos = BlockVector.toBlockPoint(x, y, z);
                    BaseBlock baseBlock = clipboard.getBlock(pos);
                    blocks.put(pos, baseBlock);
                }
            }
        }

        return blocks;
    }

    public java.util.Map<BlockVector, BaseBlock> getNeighbourBlocks(BlockVector blockVector) {
        java.util.Map<BlockVector, BaseBlock> blocks = new HashMap<>();
        Schematic schematic = getSchematic();
        if (schematic == null) {
            return blocks;
        }

        Clipboard clipboard = schematic.getClipboard();
        if (clipboard == null) {
            return blocks;
        }

        for (int x = blockVector.getBlockX() - 1; x <= blockVector.getBlockX() + 1; x++) {
            for (int y = blockVector.getBlockY() - 1; y <= blockVector.getBlockY() + 1; y++) {
                for (int z = blockVector.getBlockZ() - 1; z <= blockVector.getBlockZ() + 1; z++) {
                    if (x == blockVector.getBlockX() && y == blockVector.getBlockY() && z == blockVector.getBlockZ()) {
                        continue;
                    }
                    BlockVector pos = BlockVector.toBlockPoint(x, y, z);
                    BaseBlock baseBlock = clipboard.getBlock(pos);
                    blocks.put(pos, baseBlock);
                }
            }
        }

        return blocks;
    }

    public Location getZeroLocation() {
        return zeroLocation.clone();
    }

    public org.bukkit.util.Vector getCorner1() {
        return corner1.clone();
    }

    public org.bukkit.util.Vector getCorner2() {
        return corner2.clone();
    }
}
