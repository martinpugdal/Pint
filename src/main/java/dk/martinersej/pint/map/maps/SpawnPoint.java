package dk.martinersej.pint.map.maps;

import lombok.Getter;
import org.bukkit.util.Vector;

@Getter
public class SpawnPoint {

    private final Vector vector;
    private final float yaw;
    private final float pitch;

    public SpawnPoint(Vector vector, float yaw, float pitch) {
        this.vector = vector;
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
