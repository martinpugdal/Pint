package dk.martinersej.pint.game.games.simonsays.game;

import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class CraftStick extends SimonGame {

    public CraftStick(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public void startGame() {
        ItemStack item = new ItemStack(Material.LOG);
        for (Player player : getSimonSaysGame().getPlayers()) {
            // random int 0-8
            int random = (int) (Math.random() * 9);
            player.getInventory().setItem(random, item);
            player.sendMessage("§5§lSimon siger: §r§7Lav en pind");
        }
    }

    @Override
    public void stopGame() {
        for (Player player : getSimonSaysGame().getPlayers()) {
            player.closeInventory();
            player.getOpenInventory().getTopInventory().clear();
            player.getInventory().clear();
        }
    }

    @EventHandler
    public void onCraftStick(CraftItemEvent event) {
        if (!getSimonSaysGame().isPlayerInGame((Player) event.getWhoClicked())) return;
        if (event.getRecipe().getResult().getType().equals(Material.STICK)) {
            getSimonSaysGame().finishedPlayer((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (getSimonSaysGame().isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PARTICIPATION;
    }
}
