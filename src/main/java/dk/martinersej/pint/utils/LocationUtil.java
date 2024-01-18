package dk.martinersej.pint.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class LocationUtil {

    public static String locationToString(Location loc) {
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }

    public static Location stringToLocation(String string) {
        String[] parts = string.split(";");
        if (parts.length != 6) return new Location(Bukkit.getWorlds().get(0), 0, 0, 0);

        World world = Bukkit.getWorld(parts[0]);
        float x = Float.parseFloat(parts[1]);
        float y = Float.parseFloat(parts[2]);
        float z = Float.parseFloat(parts[3]);
        float yaw = Float.parseFloat(parts[4]);
        float pitch = Float.parseFloat(parts[5]);

        return new Location(world, x, y, z, yaw, pitch);
    }

    public static String vectorToString(Vector vector) {
        return vector.getX() + ";" + vector.getY() + ";" + vector.getZ();
    }

    public static Vector stringToVector(String string) {
        String[] parts = string.split(";");
        if (parts.length != 3) return new Vector(0, 0, 0);

        float x = Float.parseFloat(parts[0]);
        float y = Float.parseFloat(parts[1]);
        float z = Float.parseFloat(parts[2]);

        return new Vector(x, y, z);
    }

    public static Vector getVectorOffset(Location origin, Location point) {
        double x = point.getX() - origin.getX();
        double y = point.getY() - origin.getY();
        double z = point.getZ() - origin.getZ();
        return new Vector(x, y, z);
    }
}
