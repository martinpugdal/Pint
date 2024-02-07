package dk.martinersej.pint.game.games.tnttag;

import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.map.maps.SpawnPoint;
import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TntTagGame extends Game {

    private final ItemStack tntItem;

    public TntTagGame() {
        super(new GameInformation("TNT Tag", "§c", "Tag someone with TNT to win!", Material.TNT));

        this.tntItem = new ItemBuilder(Material.TNT).setName("§cTNT").setLore("§7Right click to tag someone!").toItemStack().clone();
    }

    @Override
    public void onGameStart() {
        int tntPlayersAmount = (int) Math.floor((double) getPlayers().size() / 8);

        // find the tnt players by getPlayers()
        List<Player> tntPlayers = new ArrayList<>();

        for (int i = 0; i < tntPlayersAmount; i++) {
            Player player = getRandomPlayer();
            if (!tntPlayers.contains(player)) {
                tntPlayers.add(player);
            } else {
                i--;
            }
        }

        List<SpawnPoint> spawnPoints = getCurrentGameMap().getSpawnPoints();
        for (Player player : getPlayers()) {
            // teleport players to random spawn point on the map
            SpawnPoint spawnPoint = spawnPoints.get((int) (Math.random() * spawnPoints.size()));
            player.teleport(spawnPoint.getLocation(getCurrentGameMap()));

            // give players TNT if they are a tnt player
            if (tntPlayers.contains(player)) {
                tagPlayer(null, player);
            }
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

    public boolean isTntPlayer(Player player) {
        return player.getEquipment().getHelmet() != null && player.getEquipment().getHelmet().isSimilar(tntItem);
    }

    public Player getRandomPlayer() {
        List<Player> players = new ArrayList<>(getPlayers());
        return players.get((int) (Math.random() * players.size()));
    }
}
