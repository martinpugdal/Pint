package dk.martinersej.pint.game;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.game.objects.GameMap;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public abstract class Game implements Listener {

    private final List<Player> players = new ArrayList<>();
    private final List<GameMap> gameMaps = new ArrayList<>();
    private final GameMap currentGameMap = null;

    public Game() {
        loadGameMaps();
    }

    private void loadGameMaps() {
        Collection<GameMap> gameMaps = Pint.getInstance().getMapHandler().getMaps().values();
        for (GameMap map : gameMaps) {
            if (map.getGameName().equalsIgnoreCase(getGameInformation().getName())) {
                this.gameMaps.add(map);
            }
        }
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

    public void prepareGame() {
        //TODO: Implement this


        onGameStart();
    }

    public abstract void onGameStart();

    public abstract void onGameEnd();

    public abstract GameInformation getGameInformation();
}
