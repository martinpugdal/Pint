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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ShuffleColorGame extends Game {

    private ShuffleRound currentRound;
    private final List<GameMap> pickedMaps = new ArrayList<>();
    @Setter
    private boolean pvpEnabled = false;
    private final int maxRounds = 7;
    private int mapLowestY = 0;

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
            if (currentRound.getRoundNumber() >= maxRounds) {
                win(players);
            }
        });
    }

    @Override
    public void setup() {
        pickedMaps.clear();

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

        for (int i = 0; i <= maxRounds; i++) {
            GameMap map = maps.get((int) (Math.random() * maps.size()));
            maps.remove(map);
            pickedMaps.add(map);
        }
    }

    @Override
    public void start() {
        Pint.getInstance().getGameHandler().setGameRunning(true);
        registerEvents();
        getScoreboard().addPlayers(getPlayers());

        setCurrentGameMap(pickedMaps.get(0));
        getCurrentGameMap().pasteSchematic();

        addWinListeners();
        onGameStart();
    }

    @Override
    public void onGameStart() {
        currentRound = null;
        pvpEnabled = false;
        mapLowestY = getCurrentGameMap().getLowestYLevel();

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
        int cooldown = currentRound == null ? 10 : currentRound.getRoundDuration() - 1;
        if (cooldown < 2) {
            cooldown = 2;
        }
        currentRound = new ShuffleRound(this, roundNumber, cooldown);
        mapLowestY = getCurrentGameMap().getLowestYLevel();
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
        } else {
            event.setDamage(0.0);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            double y = event.getTo().getY();
            if (y < mapLowestY - 2) {
                onDeathToVoid(event.getPlayer());
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
