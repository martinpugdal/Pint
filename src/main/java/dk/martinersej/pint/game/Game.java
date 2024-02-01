package dk.martinersej.pint.game;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.map.maps.GameMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
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
    private final GameInformation gameInformation;
    @Setter
    private GameMap currentGameMap = null;

    public Game(GameInformation gameInformation) {
        this.gameInformation = gameInformation;
        loadGameMaps();
    }

    private void loadGameMaps() {
        Collection<GameMap> gameMaps = Pint.getInstance().getMapHandler().getMaps().values();
        for (GameMap map : gameMaps) {
            if (map.getGameName().equalsIgnoreCase(getGameInformation().getName())) {
                this.gameMaps.add(map);
            }
        }
        Bukkit.getLogger().info("Loaded " + this.gameMaps.size() + " maps for game: " + getGameInformation().getName());
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

    private List<GameMap> getAppropriateMaps(int playersCount) {
        List<GameMap> maps = new ArrayList<>();
        for (GameMap gameMap : gameMaps) {
            if (gameMap.getMinPlayers() <= playersCount && gameMap.getMaxPlayers() >= playersCount && gameMap.isActive()) {
                maps.add(gameMap);
            }
        }
        return maps;
    }

    public GameMap getRandomMap(int playersCount) {
        List<GameMap> maps = getAppropriateMaps(playersCount);
        if (maps.isEmpty()) {
            Bukkit.getLogger().warning("No maps found for game: " + getGameInformation().getName() + " with " + playersCount + " players");
            return null;
        }
        return maps.get((int) (Math.random() * maps.size()));
    }

    public void prepareGame() {
        //TODO: Implement this


        onGameStart();
    }

    public abstract void onGameStart();

    public abstract void onGameEnd();
}
