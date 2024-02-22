package dk.martinersej.pint.utils;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.World;

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

    public static void pasteSchematic(Schematic schematic, Location location, boolean pasteAir) {
        schematic.paste(
            FastAsyncWorldEditUtil.getWEWorld(location.getWorld()),
            new Vector(location.getX(), location.getY(), location.getZ()),
            false,
            pasteAir,
            null
        );
    }

    public static void clearSchematic(Region region, World world) {
        FastAsyncWorldEditUtil.runSession(world, session -> {
            try {
                session.setBlocks(region, new BaseBlock(0));
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        });
    }

    public static void setSchematic(Region region, World world, BaseBlock block) {
        FastAsyncWorldEditUtil.runSession(world, session -> {
            try {
                session.setBlocks(region, block);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        });
    }

    public static void createSchematic(String filePath, Location corner1, Location corner2) {
        try {
            File file = new File(filePath);

            Vector minCorner = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
            Vector maxCorner = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());

            CuboidRegion region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(corner1.getWorld()), minCorner, maxCorner);

            Schematic schematic = new Schematic(region);
            schematic.save(file, ClipboardFormat.SCHEMATIC);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void deleteSchematic(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
