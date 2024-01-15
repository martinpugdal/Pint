package dk.martinersej.pint.game;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

public class GameQueue {

    private final Set<Player> queue = Collections.newSetFromMap(new WeakHashMap<>());

    public GameQueue() {
    }

}
