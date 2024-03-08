package dk.martinersej.pint.utils;

import dk.martinersej.pint.Pint;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PacketUtil {

    public static void sendTitle(Player player, String title, String subTitle) {
        sendTitle(player, title, subTitle, 10, 70, 20);
    }

    public static void sendTitle(Player player, String title, String subTitle, int fadeInTicks, int durationTicks, int fadeOutTicks) {
        IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");
        IChatBaseComponent subtitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subTitle + "\"}");

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent, fadeInTicks, durationTicks, fadeOutTicks);
        PacketPlayOutTitle subtitlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleComponent, fadeInTicks, durationTicks, fadeOutTicks);

        PlayerConnection playerConnection = ((CraftPlayer) player).getHandle().playerConnection;
        playerConnection.sendPacket(subtitlePacket);
        playerConnection.sendPacket(titlePacket);

        PacketPlayOutTitle length = new PacketPlayOutTitle(fadeInTicks, durationTicks, fadeOutTicks);
        playerConnection.sendPacket(length);

    }

    public static void sendActionBar(Player player, String message) {
        IChatBaseComponent chatComponent = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message + "\"}");
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chatComponent, (byte) 2);

        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        nmsPlayer.playerConnection.sendPacket(packetPlayOutChat);
    }

    public static void sendRedstoneParticle(Player player, Location location, Color color) {
        PacketPlayOutWorldParticles particle = new PacketPlayOutWorldParticles(
            EnumParticle.REDSTONE, true,
            (float) location.getX(), (float) location.getY(), (float) location.getZ(),
            (float) color.getRed() / 255, (float) color.getGreen() / 255, (float) color.getBlue() / 255,
            (float) 1, 0, 1
        );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(particle);
    }

    public static void sendTintScreen(Player player, WorldBorderColor color, Location centerLocation, int tintDuration) {
        WorldBorder worldBorder = new WorldBorder();
        worldBorder.world = ((CraftWorld) centerLocation.getWorld()).getHandle();
        worldBorder.setCenter(centerLocation.getBlockX() + 0.5, centerLocation.getBlockZ() + 0.5);

        worldBorder.setDamageBuffer(0);
        worldBorder.setDamageAmount(0);
        worldBorder.setWarningTime(0);

        switch (color) {
            case RED:
                worldBorder.transitionSizeBetween(Integer.MAX_VALUE, Integer.MAX_VALUE - 1.0D, 20000000L);
                worldBorder.setWarningDistance(Integer.MAX_VALUE);
                break;
            case YELLOW:
                worldBorder.transitionSizeBetween(Integer.MAX_VALUE, Integer.MAX_VALUE - 0.1D, 20000000L);
                worldBorder.setWarningDistance(Integer.MAX_VALUE);
                break;
            case GREEN:
                worldBorder.transitionSizeBetween(Integer.MAX_VALUE, Integer.MAX_VALUE + 1.0D, 20000000L);
                worldBorder.setWarningDistance(Integer.MAX_VALUE);
                break;
            case WHITE:
                worldBorder.transitionSizeBetween(Integer.MAX_VALUE, Integer.MAX_VALUE + 0.1D, 20000000L);
                worldBorder.setWarningDistance(Integer.MAX_VALUE);
                break;
            case OFF:
                worldBorder.setSize(Integer.MAX_VALUE);
                break;
        }

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldBorder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));

        if (tintDuration == 0 || color == WorldBorderColor.OFF) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                sendTintScreen(player, WorldBorderColor.OFF, centerLocation, 0);
            }
        }.runTaskLater(Pint.getInstance(), tintDuration);
    }

    public enum WorldBorderColor {
        RED, YELLOW, GREEN, WHITE, OFF
    }
}
