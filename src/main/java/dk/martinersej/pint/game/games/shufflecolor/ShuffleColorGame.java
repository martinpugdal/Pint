package dk.martinersej.pint.game.games.shufflecolor;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class ShuffleColorGame extends Game {

    private ShuffleRound currentRound;
    private final List<GameMap> pickedMaps = new ArrayList<>();
    @Setter
    private boolean pvpEnabled = false;
    private final int maxRounds = 7;

    public ShuffleColorGame() {
        super(new GameInformation("Color shuffle", "§5", "Stand on the right color to win!", new ItemStack(Material.WOOL, 1, (short) 12)));
    }

    @Override
    public void addWinListeners() {
        addWinListener(players -> {
            if (players.size() == 1) {
                win(players);
            }
        });
        addWinListener(players -> {
            // if time is up and all rounds are done, end the game and winners the remaining players
            if (currentRound.getRoundNumber() >= maxRounds) {
                win(players);
            }
        });
    }

    @Override
    public void setup() {
        // get maxRounds maps from the map pool where the size is the same for each map
        List<List<GameMap>> canBePicked = new ArrayList<>();
        Map<String, List<GameMap>> mapSizes = new HashMap<>();
        for (GameMap map : this.getActiveMaps()) {
            int width = map.getCorner1().getBlockX() - map.getCorner2().getBlockX();
            int length = map.getCorner1().getBlockZ() - map.getCorner2().getBlockZ();
            mapSizes.computeIfAbsent(width + "x" + length, k -> new ArrayList<>()).add(map);
        }

        // add the maps to the canBePicked list if the size is the same for all maps
        for (List<GameMap> maps : mapSizes.values()) {
            if (maps.size() >= maxRounds) {
                canBePicked.add(maps);
            }
        }

        if (canBePicked.isEmpty()) {
            Bukkit.getLogger().warning("Not enough maps to start color shuffle game");
            Pint.getInstance().getGameHandler().setCurrentGame(null);
            Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
            Pint.getInstance().getVoteHandler().startVoteTimer();
            return;
        }
        List<GameMap> maps = canBePicked.get((int) (Math.random() * canBePicked.size()));
        pickedMaps.clear(); // clear the list if it's not empty
        for (int i = 0; i < maxRounds; i++) {
            GameMap map = maps.get((int) (Math.random() * maps.size()));
            maps.remove(map);
            pickedMaps.add(map);
        }
        setupDefaultScoreboard();
    }

    @Override
    public void start() {
        Pint.getInstance().getGameHandler().setGameRunning(true);
        registerEvents();
        getScoreboard().addPlayers(getPlayers());

        setCurrentGameMap(pickedMaps.get(0));
        getCurrentGameMap().pasteSchematic();

        onGameStart();
    }

    @Override
    public void onGameStart() {
//        addWinListeners();

        currentRound = null;
        pvpEnabled = false;

        List<SpawnPoint> spawnPoints = getCurrentGameMap().getSpawnPoints();
        for (Player player : getPlayers()) {
            setPlayerToGameGamemode(player);
            Location teleport;
            if (spawnPoints.isEmpty()) {
                teleport = getCurrentGameMap().getSpawnLocation();
            } else {
                SpawnPoint spawnPoint = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
                teleport = spawnPoint.getLocation(getCurrentGameMap());
            }
            player.teleport(teleport);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                startRound();
            }
        }.runTaskLater(Pint.getInstance(), 20);
    }

    private void startRound() {
        if (currentRound != null) {
            currentRound.getRoundTask().cancel();
        }
        int roundNumber = currentRound == null ? 1 : currentRound.getRoundNumber() + 1;
        int cooldown = currentRound == null ? 15 : currentRound.getRoundDuration() - 5;
        if (cooldown < 5) {
            cooldown = 5;
        }
        currentRound = new ShuffleRound(this, roundNumber, cooldown);
        currentRound.start();
    }

    public void endRound() {
        new BukkitRunnable() {
            @Override
            public void run() {
                callWinListeners(getPlayers());
                if (Pint.getInstance().getGameHandler().isGameRunning()) {
                    startRound();
                }
            }
        }.runTaskLater(Pint.getInstance(), (int) (20 * 2.5));
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        if (!isPlayerInGame(attacker) || !isPlayerInGame(victim)) {
            return;
        }
        if (!pvpEnabled) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamageByVoid(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (isPlayerInGame(player) && event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
                onDeathToVoid(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onGameEnd() {
        if (currentRound != null) {
            currentRound.getRoundTask().cancel();
        }
        for (Player player : getPlayers()) {
            player.getInventory().clear();
            Pint.getInstance().getVoteHandler().getVoteUtil().setToPlainVoteGamemode(player);
        }
    }

    public void onDeathToVoid(Player player) {
        removePlayer(player);
        Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(player);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
