package dk.martinersej.pint.game.objects;

import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.List;

@Getter
public class GameMap {

    private final String id;

    private Vector corner1;
    private Vector corner2;
    private List<Vector> spawnPoints;

    public GameMap(String id) {
        this.id = id;
    }
}
