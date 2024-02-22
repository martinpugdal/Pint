package dk.martinersej.pint.game.objects;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import lombok.Getter;
import lombok.Setter;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Consumer;

@Getter
public abstract class Game implements Listener, WinnerListener {
    @Getter
    private static int gameIDCounter = 0;
    private final Set<Player> players = new HashSet<>();
    private final Set<GameMap> gameMaps = new HashSet<>();
    private final int id;
    private final GameInformation gameInformation;
    private final Set<Consumer<Set<Player>>> winnerListeners = new HashSet<>();
    @Setter
    private GameMap currentGameMap = null;
    @Setter
    private Sidebar scoreboard;

    public Game(GameInformation gameInformation) {
        this.id = ++gameIDCounter;
        this.gameInformation = gameInformation;
        loadGameMaps();
    }

    public abstract void addWinListeners();

    @Override
    public void addWinListener(Consumer<Set<Player>> onWin) {
        winnerListeners.add(onWin);
    }

    public void callWinListeners(Set<Player> players) {
        //if (winnerListeners.isEmpty()) return;
        for (Consumer<Set<Player>> listener : new HashSet<>(winnerListeners)) {
            if (winnerListeners.isEmpty()) return;
            listener.accept(players);
        }
    }

    public void win(Set<Player> players) {
        String playerString = players.stream().map(Player::getName).reduce((s1, s2) -> s1 + ", " + s2).orElse("");
        if (playerString.isEmpty())
            Bukkit.broadcastMessage("§cIngen vandt!");
        else {
            String vinder = players.size() == 1 ? "§6Vinder" : "§6Vindere";
            Bukkit.broadcastMessage(vinder + "§8: §e" + playerString + "!");
            Bukkit.broadcastMessage("§aDe vandt " + gameInformation.getDisplayName() + "§a!");
        }
        stop();
        // Gør noget med vinderne her
        // Statistikker, belønninger osv.
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

    public GameMap setupGameMap(int playersCount) {
        currentGameMap = getRandomMap(playersCount);
        if (currentGameMap == null) {
            Bukkit.getLogger().warning("No map found for game: " + getGameInformation().getName() + " with " + playersCount + " players");
            Pint.getInstance().getGameHandler().setCurrentGame(null);
            Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
            Pint.getInstance().getVoteHandler().startVoteTimer();
            return null;
        }
        return currentGameMap;
    }

    private boolean isCurrentGameMapValid() {
        return currentGameMap != null && currentGameMap.getMinPlayers() <= players.size() && currentGameMap.getMaxPlayers() >= players.size();
    }

    public void setupDefaultScoreboard() {
        if (this.getScoreboard() == null) {
            this.setScoreboard(Pint.getScoreboardLibrary().createSidebar());
        }
    }

    public void setup() {
        if (setupGameMap(Pint.getInstance().getVoteHandler().getAllVoters().size()) == null) return;
        currentGameMap.pasteSchematic();
        setupDefaultScoreboard();
    }

    public void start() {
        if (!isCurrentGameMapValid()) {
            GameMap oldMap = currentGameMap;
            if (setupGameMap(players.size()) == null) return;
            oldMap.clearSchematic();
        }
        currentGameMap.pasteSchematic();
        Pint.getInstance().getGameHandler().setGameRunning(true);
        registerEvents();
        scoreboard.addPlayers(players);
        addWinListeners();
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
        setPlayersToVoteGamemode();

        players.clear();

        Pint.getInstance().getGameHandler().setGameRunning(false);
        Pint.getInstance().getGameHandler().setCurrentGame(null);
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
        // check if winlistener contains a way to end the game early
        callWinListeners(players);
    }

    protected void registerEvents() {
        Bukkit.getPluginManager().registerEvents(this, JavaPlugin.getProvidingPlugin(getClass()));
    }

    private void unregisterEvents() {
        winnerListeners.clear();
        HandlerList.unregisterAll(this);
    }

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

    public void setPlayerToGameGamemode(Player player) {
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setHealth(20);
        player.setFoodLevel(20);
        player.setSaturation(20);
    }

    public void setPlayersToGameGamemode() {
        for (Player player : players) {
            setPlayerToGameGamemode(player);
        }
    }

    public void setPlayersToVoteGamemode() {
        for (Player player : players) {
            Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            removePlayer(event.getPlayer());
        }
    }

    public abstract void onGameStart();

    public abstract void onGameEnd();
}
