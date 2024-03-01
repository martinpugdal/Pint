package dk.martinersej.pint.game.games.simonsays;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.simonsays.games.participation.*;
import dk.martinersej.pint.game.games.simonsays.games.placement.*;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.game.games.simonsays.objects.SimonPlayer;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.utils.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
        super(new GameInformation("Simon Says", "§5", "Gør hvad Simon siger",
            new ItemBuilder().skull().
                build()
        ));
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
        SimonPlayer first = simonPlayers.pollFirst();
        SimonPlayer second = simonPlayers.pollFirst();
        SimonPlayer third = simonPlayers.pollFirst();


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
        simonPlayers.clear();
        setupGames();
        super.setup();
    }

    private void setupGames() {
        games.clear();
        playedGames.clear();

        // participation
        games.add(new CraftOneStickGame(this));
        games.add(new CrouchGame(this));
        games.add(new DropItemGame(this));
        games.add(new EatItemGame(this));
        games.add(new HitAPlayerGame(this));
        games.add(new JumpGame(this));

        // placement
        games.add(new BreakOreGame(this));
        games.add(new LookDirectionGame(this));
        games.add(new Rotate360Game(this));
        games.add(new SitInABoatGame(this));
        games.add(new TypeInChatGame(this));
        games.add(new TypeTheNumberGame(this));
    }

    private SimonGame getRandomGame() {
        if (games.isEmpty()) {
            return null;
        }
        SimonGame game = games.get((int) (Math.random() * games.size()));
        games.remove(game);
        return game;
    }

    public void say() {
        for (Player player : getPlayers()) {
            player.sendMessage("§5§lSimon siger: §r" + currentGame.sayText());
        }
    }

    private void stopGameIfAllPlayersDone() {
        int finishedAndFailed = currentGame.getFinishedPlayers().size() + currentGame.getFailedPlayers().size();
        if (finishedAndFailed == getPlayers().size()) {
            currentGame.stop();
        }
    }

    public void finishedTask(Player player) {
        if (currentGame == null) return;
        if (currentGame.getFinishedPlayers().contains(player)) return;
        if (currentGame.getFailedPlayers().contains(player)) return;
        currentGame.getFinishedPlayers().add(player);
        for (Player p : getPlayers()) {
            p.sendMessage("§a" + player.getName() + " klarede opgaven");
        }

        stopGameIfAllPlayersDone();
    }

    public void failedPlayer(Player player) {
        if (currentGame == null) return;
        if (currentGame.getFinishedPlayers().contains(player)) return;
        if (currentGame.getFailedPlayers().contains(player)) return;
        currentGame.getFailedPlayers().add(player);
        for (Player p : getPlayers()) {
            p.sendMessage("§c" + player.getName() + " klarede ikke opgaven");
        }

        stopGameIfAllPlayersDone();
    }

    public void clearInventory(Player player) {
        if (player.getItemOnCursor() != null && player.getItemOnCursor().getType() != Material.AIR) {
            player.getItemOnCursor().setType(Material.AIR);
        }
        player.getOpenInventory().getTopInventory().clear();
        player.getInventory().clear();
    }


    @Override
    public void onGameStart() {
        SimonGame game = getRandomGame();
        currentGame = game;

        setPlayersToGameGamemode();
        for (Player player : getPlayers()) {
            player.teleport(getSpawnLocation());
            player.sendMessage("§aVi starter om 5 sekunder, vær klar!");
            simonPlayers.add(new SimonPlayer(player.getUniqueId()));
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
        if (currentGame != null) {
            currentGame.stop();
        }
        currentGame = null;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageToVoid(EntityDamageEvent event) {
        Player player = (Player) event.getEntity();
        if (player == null) return;
        if (!isPlayerInGame(player)) return;
        if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
            event.setCancelled(true);
            event.getEntity().teleport(getSpawnLocation());
            event.getEntity().setFallDistance(0);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageByPlayer(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player attacker = (Player) event.getDamager();
            Player victim = (Player) event.getEntity();
            if (isPlayerInGame(attacker) || isPlayerInGame(victim)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // just in case, we have a game that allows breaking blocks
    public void onBlockBreak(BlockBreakEvent event) {
        if (isPlayerInGame(event.getPlayer()) && !event.isCancelled()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST) // just in case, we have a game that allows placing blocks
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isPlayerInGame(event.getPlayer()) && !event.isCancelled()) {
            event.setCancelled(true);
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
                int placement = previousGame.getFinishedPlayers().indexOf(simonPlayer.getPlayer()) + 1;
                simonPlayer.addPoints(previousGame.getScoringType().getPoints(placement));
            }
        }
        callWinListeners(getPlayers());
        currentGame = getRandomGame();
        for (Player player : getPlayers()) {
            player.sendMessage("§aVi starter om 5 sekunder, vær klar!");
        }
        startGameTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentGame != null)
                    currentGame.start();
            }
        };
        startGameTask();
    }

    private void startGameTask() {
        startGameTask.runTaskLater(Pint.getInstance(), 20 * 5); // 5 seconds
    }
}
