package dk.martinersej.pint.game.objects;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import lombok.Getter;
import lombok.Setter;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
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

    public void setup() {
        currentGameMap = getRandomMap(players.size());
        if (currentGameMap == null) {
            Bukkit.getLogger().warning("No map found for game: " + getGameInformation().getName() + " with " + players.size() + " players");
            return;
        }
        if (this.getScoreboard() == null) {
            this.setScoreboard(Pint.getScoreboardLibrary().createSidebar());
        }
        currentGameMap.pasteSchematic();
    }

    public void start() {
        Pint.getInstance().getGameHandler().setGameRunning(true);
        registerEvents();
        scoreboard.addPlayers(players);
        onGameStart();
    }

    public void stop() {
        onGameEnd();
        unregisterEvents();
        currentGameMap.clearSchematic();
        currentGameMap = null;
        scoreboard.removePlayers(players);
        scoreboard.close();
        scoreboard = null;
        players.clear();
        Pint.getInstance().getGameHandler().setGameRunning(false);
        Pint.getInstance().getGameHandler().setCurrentGame(null);
        Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
        Pint.getInstance().getVoteHandler().startVoteTimer();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addPlayers(List<Player> players) {
        this.players.addAll(players);
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

    // a function to check if a player is in the game
    public boolean isPlayerInGame(Player player) {
        return players.contains(player);
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

    @Setter
    private Sidebar scoreboard;

    public abstract void onGameStart();

    public abstract void onGameEnd();
}
