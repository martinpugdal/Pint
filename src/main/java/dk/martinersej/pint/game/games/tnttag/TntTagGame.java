package dk.martinersej.pint.game.games.tnttag;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TntTagGame extends Game {

    private final ItemStack tntItem;
    private TNTRound currentTNTRound;

    public TntTagGame() {
        super(new GameInformation("TNT Tag", "§c", "Tag someone with TNT to win!", Material.TNT));

        this.tntItem = new ItemBuilder(Material.TNT).setName("§cTNT").setLore("§7Right click to tag someone!").build().clone();
    }

    @Override
    public void addWinListeners() {
        addWinListener(players -> {
            if (players.size() == 1) {
                win(players);
            }
        });
    }

    @Override
    public void onGameStart() {
//        addWinListeners();

        List<SpawnPoint> spawnPoints = getCurrentGameMap().getSpawnPoints();
        for (Player player : getPlayers()) {
            // teleport players to random spawn point on the map
            setPlayerToGameGamemode(player);
            SpawnPoint spawnPoint = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
            player.teleport(spawnPoint.getLocation(getCurrentGameMap()));
        }

        startRound();
    }

    @Override
    public void onGameEnd() {
        currentTNTRound.getRoundTask().cancel();
        for (Player player : getPlayers()) {
            player.getEquipment().setHelmet(null);
        }
    }

    private void startRound() {
        if (currentTNTRound != null) {
            currentTNTRound.getRoundTask().cancel();
        }
        int roundNumber = currentTNTRound == null ? 1 : currentTNTRound.getRoundNumber() + 1;
        int cooldown = currentTNTRound == null ? 15 : currentTNTRound.getRoundDuration() - 5;
        if (cooldown < 10) {
            cooldown = 10;
        }
        currentTNTRound = new TNTRound(this, roundNumber, cooldown, new HashSet<>(getPlayers()));
        currentTNTRound.start();
    }

    public void endRound() {
        if (getPlayers().size() > 1) {
            startRound();
        }
//      else {
//            win(new HashSet<>(getPlayers()));
//        }
    }

    public List<Player> getTntPlayers() {
        List<Player> tntPlayers = new ArrayList<>();
        for (Player player : getPlayers()) {
            if (player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().isSimilar(tntItem)) {
                tntPlayers.add(player);
            }
        }
        return tntPlayers;
    }

    public void tagPlayer(Player tntPlayer, Player taggedPlayer) {
        if (tntPlayer != null) {
            tntPlayer.getEquipment().setHelmet(null);
            tntPlayer.getInventory().remove(tntItem);
        }
        if (taggedPlayer != null) {
            taggedPlayer.getEquipment().setHelmet(tntItem);
            taggedPlayer.getInventory().addItem(tntItem);
        }
    }

    public void blowUpTntPlayer(Player player) {
        removePlayer(player);
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        float power = 0f;
        player.setGameMode(GameMode.SPECTATOR);
        player.getWorld().createExplosion(x, y, z, power, false, false);
        new BukkitRunnable() {
            @Override
            public void run() {
                Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(player);
            }
        }.runTaskLater(Pint.getInstance(), 20 * 3);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player tntPlayer = (Player) event.getDamager();
        Player taggedPlayer = (Player) event.getEntity();
        if (!isPlayerInGame(tntPlayer) && !isPlayerInGame(taggedPlayer)) {
            return;
        }
        event.setDamage(0);
        if (isTntPlayer(tntPlayer) && !isTntPlayer(taggedPlayer)) {
            tagPlayer(tntPlayer, taggedPlayer);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (!isPlayerInGame(player)) {
            return;
        }
        removePlayer(player);
        player.spigot().respawn();
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTntBlowUp(EntityExplodeEvent event) {
        if (event.getEntity().getType() == EntityType.PRIMED_TNT) {
            event.setCancelled(true);
        }
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

    public boolean isTntPlayer(Player player) {
        return player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().isSimilar(tntItem);
    }
}
