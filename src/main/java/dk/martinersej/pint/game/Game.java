package dk.martinersej.pint.game;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.game.objects.GameMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class Game implements Listener {

    @Setter
    private boolean isRunning = false;
    private final List<Player> players = new ArrayList<>();
    @Setter
    private GameMap gameMap;

    public Game(boolean addToGamePool) {
        if (addToGamePool) {
            Pint.getInstance().getGameHandler().addGameToPool(this);
        }
    }

    public Game() {
        this(true);
    }

    public void start() {
        registerEvents();
    }

    public void stop() {
        unregisterEvents();
        players.clear();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }


    private void registerEvents() {
        registerEvents();
    }

    private void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public abstract void onGameStart();

    public abstract void onGameEnd();

    public abstract GameInformation getGameInformation();
}
