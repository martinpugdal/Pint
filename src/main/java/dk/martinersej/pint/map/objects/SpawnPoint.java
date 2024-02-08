package dk.martinersej.pint.map.objects;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import lombok.Getter;
import org.bukkit.Location;
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

    public Location getLocation(GameMap gameMap) {
        return Pint.getInstance().getMapHandler().getMapUtil().calculateSpawnLocationWithVoteMap(this, gameMap);
    }
}
