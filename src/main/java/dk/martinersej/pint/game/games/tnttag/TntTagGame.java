package dk.martinersej.pint.game.games.tnttag;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TntTagGame extends Game {

    private final ItemStack tntItem;
    private Round currentRound;

    public TntTagGame() {
        super(new GameInformation("TNT Tag", "§c", "Tag someone with TNT to win!", Material.TNT));

        this.tntItem = new ItemBuilder(Material.TNT).setName("§cTNT").setLore("§7Right click to tag someone!").toItemStack().clone();
    }

    private void addWinListener() {
        addWinListener(players -> {
            if (players.size() == 1) {
                win(players);
            }
        });
    }

    @Override
    public void onGameStart() {
        addWinListener();

        List<SpawnPoint> spawnPoints = getCurrentGameMap().getSpawnPoints();
        for (Player player : getPlayers()) {
            // teleport players to random spawn point on the map
            SpawnPoint spawnPoint = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
            player.teleport(spawnPoint.getLocation(getCurrentGameMap()));
        }

        startRound();
    }

    private void startRound() {
        if (currentRound != null) {
            currentRound.getRoundTask().cancel();
        }
        int roundNumber = currentRound == null ? 1 : currentRound.getRoundNumber() + 1;
        int cooldown = currentRound == null ? 60 : currentRound.getRoundDuration() - 5;
        if (cooldown < 10) {
            cooldown = 10;
        }
        currentRound = new Round(this, roundNumber, cooldown, new HashSet<>(getPlayers()));
        currentRound.start();
    }

    public void endRound() {
        if (getPlayers().size() > 1) {
            startRound();
        } else {
            endGame();
        }
    }

    private void endGame() {
        Player winner = new ArrayList<>(getPlayers()).get(0);
        Bukkit.broadcastMessage("§a" + winner.getName() + " §7has won the game!");
        onGameEnd();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Player tntPlayer = (Player) event.getDamager();
        Player taggedPlayer = (Player) event.getEntity();
        if (!isPlayerInGame(tntPlayer) && !isPlayerInGame(taggedPlayer)) {
            Bukkit.getLogger().warning("A player not in the game was damaged by another player not in the game!");
            return;
        }
        event.setCancelled(true);
        if (isTntPlayer(tntPlayer) && !isTntPlayer(taggedPlayer)) {
            tagPlayer(tntPlayer, taggedPlayer);
        }
    }

    @Override
    public void onGameEnd() {
        for (Player player : getPlayers()) {
            player.getEquipment().setHelmet(null);
        }
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
        }
        if (taggedPlayer != null) {
            taggedPlayer.getEquipment().setHelmet(tntItem);
        }
    }

    public void blowUpTntPlayer(Player player) {
        removePlayer(player);
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        float power = 4;
        player.setGameMode(GameMode.SPECTATOR);
        player.getWorld().createExplosion(x, y, z, power, false, false);
        new BukkitRunnable() {
            @Override
            public void run() {
                Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(player);
            }
        }.runTaskLater(Pint.getInstance(), 20 * 5);
    }

    public boolean isTntPlayer(Player player) {
        return player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().isSimilar(tntItem);
    }
}
