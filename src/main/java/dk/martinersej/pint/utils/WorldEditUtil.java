package dk.martinersej.pint.utils;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.EditSessionFactory;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.regions.Region;
import lombok.NonNull;
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

    public static boolean isAllAir(@NonNull Region region) {
        for (BlockVector block : region) {
            if (!region.getWorld().getBlock(block).isAir()) {
                return false;
            }
        }
        return true;
    }
}