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
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public abstract class Game implements Listener {
    @Getter
    private static int gameIDCounter = 0;
    private final List<Player> players = new ArrayList<>();
    private final List<GameMap> gameMaps = new ArrayList<>();
    private final int id;
    private final GameInformation gameInformation;
    @Setter
    private GameMap currentGameMap = null;

    public Game(GameInformation gameInformation) {
        this.id = ++gameIDCounter;
        this.gameInformation = gameInformation;
        loadGameMaps();
    }

    private void loadGameMaps() {
        Collection<GameMap> gameMaps = Pint.getInstance().getMapHandler().getMaps().values();
        for (GameMap map : gameMaps) {
            if (map.getGameID() == id) {
                this.gameMaps.add(map);
            }
        }
        Bukkit.getLogger().info("Loaded " + this.gameMaps.size() + " maps for game: " + getGameInformation().getName());
    }

    public void start() {
        currentGameMap = getRandomMap(players.size());
        if (currentGameMap == null) {
            Bukkit.getLogger().warning("No map found for game: " + getGameInformation().getName() + " with " + players.size() + " players");
            return;
        }
        currentGameMap.pasteSchematic();
        onGameStart();
        registerEvents();
    }

    public void stop() {
        onGameEnd();
        unregisterEvents();
        currentGameMap.clearSchematic();
        currentGameMap = null;
        players.clear();
        Pint.getInstance().getGameHandler().setCurrentGame(null);
        Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
        Pint.getInstance().getVoteHandler().startVoteTimer();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }


    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));
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

    public List<GameMap> getActiveMaps() {
        List<GameMap> maps = new ArrayList<>();
        for (GameMap gameMap : gameMaps) {
            if (gameMap.isActive()) {
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

    public abstract void onGameStart();

    public abstract void onGameEnd();
}
