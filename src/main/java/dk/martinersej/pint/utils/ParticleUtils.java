package dk.martinersej.pint.utils;

import dk.martinersej.pint.Pint;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.awt.*;

import static java.awt.Color.*;

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
            Bukkit.getLogger().warning("Distance is too long to draw line");
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

    public static void drawCircle(Location center, double radius, Player player, double space, Color color) {
        double increment = 1 / radius;
        for (double theta = 0; theta < 2 * Math.PI; theta += increment) {
            double x = radius * Math.cos(theta);
            double z = radius * Math.sin(theta);
            sendRedstoneParticle(player, center.clone().add(x, 0, z), color);
        }
    }

    private static Color getNextColorInRow(Color color) {
        if (color.equals(RED)) {
            return ORANGE;
        } else if (color.equals(ORANGE)) {
            return YELLOW;
        } else if (color.equals(YELLOW)) {
            return GREEN;
        } else if (color.equals(GREEN)) {
            return BLUE;
        } else if (color.equals(BLUE)) {
            return CYAN;
        } else if (color.equals(CYAN)) {
            return MAGENTA;
        } else if (color.equals(MAGENTA)) {
            return PINK;
        } else if (color.equals(PINK)) {
            return BLACK;
        } else if (color.equals(BLACK)) {
            return WHITE;
        } else if (color.equals(WHITE)) {
            return GRAY;
        } else if (color.equals(GRAY)) {
            return LIGHT_GRAY;
        } else if (color.equals(LIGHT_GRAY)) {
            return DARK_GRAY;
        } else if (color.equals(DARK_GRAY)) {
            return RED;
        } else {
            return RED;
        }
    }


    public static void drawRegionCuboid(Location corner1, Location corner2, Player player) {
        World world = corner1.getWorld();
        if (!corner2.getWorld().equals(world)) {
            return;
        }
        Bukkit.getLogger().info("Drawing region cuboid");
        corner1 = corner1.clone().add(0.5, 0.5, 0.5);
        corner2 = corner2.clone().add(0.5, 0.5, 0.5);

        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        Bukkit.getLogger().info("minX: " + minX + " minY: " + minY + " minZ: " + minZ);
        Bukkit.getLogger().info("maxX: " + maxX + " maxY: " + maxY + " maxZ: " + maxZ);

        int particleCount = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1) * 20*5; // 10 particles per block

        if (particleCount > 10000) {
            // Limit the number of particles to avoid lag
            Bukkit.getLogger().warning("Too many particles to draw region cuboid");
            return;
        }

        new BukkitRunnable() {

            int counter = 0;
            Color color = RED;

            @Override
            public void run() {
                if (counter >= particleCount) {
                    cancel();
                }
                // don't spawn particles inside the region, so we can see the outline
                for (int x = minX; x <= maxX + 1; x++) {
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, x, minY, minZ), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, x, minY, maxZ + 1), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, x, maxY + 1, minZ), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, x, maxY + 1, maxZ + 1), color);
                }
                for (int y = minY; y <= maxY + 1; y++) {
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, minX, y, minZ), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, maxX + 1, y, minZ), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, minX, y, maxZ + 1), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, maxX + 1, y, maxZ + 1), color);
                }
                for (int z = minZ; z <= maxZ + 1; z++) {
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, minX, minY, z), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, maxX + 1, minY, z), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, minX, maxY + 1, z), color);
                    color = getNextColorInRow(color);
                    sendRedstoneParticle(player, new Location(world, maxX + 1, maxY + 1, z), color);
                }
                counter += 12;
            }
        }.runTaskTimerAsynchronously(Pint.getInstance(), 0L, 1L);

    }

}
