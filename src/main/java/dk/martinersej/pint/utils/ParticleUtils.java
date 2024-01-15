package dk.martinersej.pint.utils;

import dk.martinersej.pint.Pint;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.*;

public class ParticleUtils {

    public static void sendRedstoneParticle(Player player, Location location, Color color) {
        PacketPlayOutWorldParticles particle = new PacketPlayOutWorldParticles(
                EnumParticle.REDSTONE, true,
                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255, (float) 1, 0
        );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(particle);
    }

    public static void drawLine(Location point1, Location point2, Player player, double space, Color color) {
        World world = point1.getWorld();
        if (!point2.getWorld().equals(world)) {
            return;
        }
        point1 = point1.clone().add(0.5, 0.5, 0.5);
        point2 = point2.clone().add(0.5, 0.5, 0.5);
        double distance = point1.distance(point2);
        if (distance > 100) {
            return;
        }
        Vector p1 = point1.toVector();
        Vector p2 = point2.toVector();
        Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
        final double[] length = {0};

        new BukkitRunnable() {
            @Override
            public void run() {
                if (length[0] >= distance) {
                    cancel();
                }
                sendRedstoneParticle(player, p1.toLocation(world), color);
                p1.add(vector);
                length[0] += space;
            }
        }.runTaskTimerAsynchronously(Pint.getInstance(), 0L, 1L);
    }
}
