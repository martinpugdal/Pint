package dk.martinersej.pint.game.games.simonsays;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.simonsays.game.CraftStick;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.game.games.simonsays.objects.SimonPlayer;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class SimonSaysGame extends Game {

    private final List<SimonGame> games = new ArrayList<>();
    private final List<SimonGame> playedGames = new ArrayList<>();
    private final TreeSet<SimonPlayer> simonPlayers = new TreeSet<>(Comparator.comparing(SimonPlayer::getPoints).reversed());
    private final int gameAmount = 5;

    @Setter
    private SimonGame currentGame;
    private BukkitRunnable startGameTask;

    public SimonSaysGame() {
        super(new GameInformation("Simon Says", "§5", "Gør hvad Simon siger", new ItemBuilder().skull().setSkullOwner("MartinErSej").build()));
    }

    @Override
    public void addWinListeners() {
//        addWinListener(players -> {
//            if (players.size() == 1) {
//                win(players);
//            }
//        });
        addWinListener(players -> {
            if (playedGames.size() >= gameAmount) {
                win(players);
            }
        });
        addWinListener(players -> {
            if (games.isEmpty()) {
                win(players);
            }
        });
    }

    @Override
    public void win(Set<Player> players) {
        win();
        stop();
    }

    private void win() {
        SimonPlayer first = simonPlayers.first();
        SimonPlayer second = null;
        SimonPlayer third = null;
        if (simonPlayers.size() > 2) {
            second = simonPlayers.higher(first);
            third = simonPlayers.higher(second);
        }

        Bukkit.broadcastMessage("§6Førsteplads: " + first.getPlayer().getName() + " med " + first.getPoints() + " point");
        if (second != null) {
            Bukkit.broadcastMessage("§6Andenplads: " + simonPlayers.higher(first).getPlayer().getName() + " med " + second.getPoints() + " point");
        }
        if (third != null) {
            Bukkit.broadcastMessage("§6Tredjeplads: " + simonPlayers.higher(second).getPlayer().getName() + " med " + third.getPoints() + " point");
        }
    }

    @Override
    public void setup() {
        setupGames();
        super.setup();
    }

    private void setupGames() {
        games.clear();
        playedGames.clear();
        simonPlayers.clear();

        // add games
        this.getGames().add(new CraftStick(this));
    }

    private SimonGame getRandomGame() {
        if (games.isEmpty()) {
            return null;
        }
        SimonGame game = games.get((int) (Math.random() * games.size()));
        games.remove(game);
        playedGames.add(game);
        return game;
    }

    public void finishedPlayer(Player player) {
        if (currentGame == null) return;
        if (currentGame.getFinishedPlayers().contains(player)) return;
        currentGame.getFinishedPlayers().add(player);
        for (Player p : getPlayers()) {
            p.sendMessage("§a" + player.getName() + " klarede opgaven");
        }
    }

    public void failedPlayer(Player player) {
        if (currentGame == null) return;
        if (currentGame.getFinishedPlayers().contains(player)) return;
        for (Player p : getPlayers()) {
            p.sendMessage("§c" + player.getName() + " klarede ikke opgaven");
        }
    }

    @Override
    public void onGameStart() {
        SimonGame game = getRandomGame();
        currentGame = game;

        setPlayersToGameGamemode();
        for (Player player : getPlayers()) {
            player.teleport(getSpawnLocation());
            player.sendMessage("§aVi starter om 5 sekunder, vær klar!");
            simonPlayers.add(new SimonPlayer(player));
        }
        startGameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (game != null) {
                    game.start();
                }
            }
        };
        startGameTask.runTaskLater(Pint.getInstance(), 20 * 5); // 5 seconds
    }

    @Override
    public void onGameEnd() {
        if (startGameTask != null) {
            startGameTask.cancel();
        }
        startGameTask = null;
        currentGame = null;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        simonPlayers.removeIf(simonPlayer -> simonPlayer.getPlayer().equals(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageToVoid(EntityDamageEvent event) {
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            event.getEntity().teleport(getSpawnLocation());
            event.getEntity().setFallDistance(0);
        }
    }


    public Location getSpawnLocation() {
        if (getCurrentGameMap().getSpawnPoints().isEmpty()) {
            return getCurrentGameMap().getSpawnLocation();
        }
        return getCurrentGameMap().getSpawnPoints().get((int) (Math.random() * getCurrentGameMap().getSpawnPoints().size())).getLocation(getCurrentGameMap());
    }

    public void nextGame() {
        SimonGame previousGame = playedGames.get(playedGames.size() - 1);
        for (SimonPlayer simonPlayer : simonPlayers) {
            if (previousGame.getFinishedPlayers().contains(simonPlayer.getPlayer())) {
                int placement = previousGame.getFinishedPlayers().indexOf(simonPlayer.getPlayer());
                simonPlayer.addPoints(previousGame.getScoringType().getPoints(placement));
            }
        }
        callWinListeners(getPlayers());
        SimonGame nextGame = getRandomGame();
        currentGame = nextGame;
        for (Player player : getPlayers()) {
            player.sendMessage("§aVi starter om 5 sekunder, vær klar!");
        }
        startGameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (nextGame != null)
                    nextGame.start();
            }
        };
        startGameTask();
    }

    private void startGameTask() {
        startGameTask.runTaskLater(Pint.getInstance(), 20 * 5); // 5 seconds
    }
}
