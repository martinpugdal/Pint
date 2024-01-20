package dk.martinersej.pint.utils;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import dk.martinersej.pint.Pint;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.function.Consumer;

public class WorldEditUtil {

    public static void runSession(World inWorld, Consumer<EditSession> consumer) {
        EditSessionFactory sessionFactory = WorldEdit.getInstance().getEditSessionFactory();
        EditSession session = sessionFactory.getEditSession(getWEWorld(inWorld), Integer.MAX_VALUE);
        try {
            consumer.accept(session);
        } finally {
            session.flushQueue();
        }
    }

    public static com.sk89q.worldedit.world.World getWEWorld(World world) {
        return BukkitUtil.getLocalWorld(world);
    }

    public static Clipboard loadSchematic(File file, com.sk89q.worldedit.world.World world) {
        ClipboardFormat format = ClipboardFormat.findByFile(file);
        try {
            ClipboardReader reader = format.getReader(Files.newInputStream(file.toPath()));
            return reader.read(world.getWorldData());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void pasteSchematic(Clipboard clipboard, Location location) {
        ClipboardHolder holder = new ClipboardHolder(clipboard, clipboard.getRegion().getWorld().getWorldData());
        runSession(location.getWorld(), session -> {
            try {
                Operations.completeLegacy(
                        holder.createPaste(session, holder.getWorldData())
                                .to(new Vector(location.getX(), location.getY(), location.getZ()))
                                .ignoreAirBlocks(false)
                                .build()
                );
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setAllTo(Clipboard clipboard, BaseBlock block) {
        runSession(Pint.getInstance().getGameHandler().getServerWorld().getWorld(), session -> {
            try {
                session.setBlocks(clipboard.getRegion(), block);
            } catch (MaxChangedBlocksException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void createSchematic(String filePath, Location corner1, Location corner2) {
        try {
            Vector minCorner = new Vector(corner1.getX(), corner1.getY(), corner1.getZ());
            Vector maxCorner = new Vector(corner2.getX(), corner2.getY(), corner2.getZ());
            CuboidRegion region = new CuboidRegion(WorldEditUtil.getWEWorld(corner1.getWorld()), minCorner, maxCorner);
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

            ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(Files.newOutputStream(new File(filePath).toPath()));
            writer.write(clipboard, region.getWorld().getWorldData());

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}