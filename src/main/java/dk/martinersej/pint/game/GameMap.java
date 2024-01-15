package dk.martinersej.pint.game;

import lombok.Getter;
import org.bukkit.util.Vector;

import java.util.List;

@Getter
public class GameMap {

    private final String id;

    private boolean active;
    private Vector corner1;
    private Vector corner2;
    private List<Vector> spawnPoints;

    public GameMap(String id) {
        this.id = id;
    }
}
