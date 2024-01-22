package dk.martinersej.pint.utils;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SchematicUtil {

    public static Schematic loadSchematic(File file, com.sk89q.worldedit.world.World world) {
        ClipboardFormat format = ClipboardFormat.findByFile(file);
        try {
            ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
            Clipboard clipboard = reader.read(world.getWorldData());
            return new Schematic(clipboard);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void pasteSchematic(Schematic schematic, Location location, boolean ignoreAirBlocks) {
        schematic.paste(FastAsyncWorldEditUtil.getWEWorld(location.getWorld()), new Vector(location.getX(), location.getY(), location.getZ()), false, ignoreAirBlocks, null);
    }

    public static void createSchematic(String filePath, Location corner1, Location corner2) {
        try {
            Vector minCorner = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
            Vector maxCorner = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());
            CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(corner1.getWorld()), minCorner, maxCorner);
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            File file = new File(filePath);
            new Schematic(clipboard).save(file, ClipboardFormat.SCHEMATIC);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void setAllTo(Schematic schematic, BaseBlock baseBlock, Location location) {
        FastAsyncWorldEditUtil.runSession(location.getWorld(), session -> {
            try {
                session.replaceBlocks(schematic.getClipboard().getRegion(), null, baseBlock);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        });
    }
}
